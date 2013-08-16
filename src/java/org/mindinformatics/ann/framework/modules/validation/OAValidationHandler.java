/*
 * Copyright 2013 Massachusetts General Hospital
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.mindinformatics.ann.framework.modules.validation;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.ontoware.rdf2go.Reasoning;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.rdf2go.RepositoryModelFactory;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.sail.memory.MemoryStore;

import de.dfki.km.json.JSONUtils;

/**
 * @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
 */
public class OAValidationHandler {
	
	private final String VALIDATION_RULES_FILE = "OAConstraintsSPARQL.json";
	private final String QUERY_ALL = "SELECT ?x ?p ?y WHERE { ?x ?p ?y } ";
	private static Logger log = Logger.getLogger(OAValidationHandler.class);

	private Map<String, Object> createException(String label, String message) {
		log.error("{\"label\": \"" + label + "\", \"message\": \"" + message + "\"}");
		Map<String, Object> exception = new HashMap<String, Object>();
		exception.put("label", label);
		exception.put("message", message);
		return exception;
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<String,Object> validate(InputStream inputRDF, String contentType) {

		// Validation rules (later loaded from the external json file)
		List<Map<String, Object>> validationRules = null;
		
		// Model factory
		RepositoryModelFactory mf = new RepositoryModelFactory();
		
		// Results
		HashMap<String,Object> finalResult = new HashMap<String, Object>();
		
		try {
			if (contentType.contains("application/json")) {
				try {
					// Repository initialization
					Repository repository = new SailRepository(new MemoryStore());
					repository.initialize();
					
					// Loading of the content to validate
					log.info("Loading content...");
					RepositoryConnection connection = null;
					try {
						connection = repository.getConnection();
						connection.add(inputRDF, "http://localhost/jsonld/",RDFFormat.JSONLD);
						connection.commit();
					} catch (Exception ex) {
						finalResult.put("exception", createException("Content parsing failed", ex.getMessage()));
						return finalResult;
					} finally {
						if(connection!=null) connection.close();
					}

					// Model initialization
					log.info("Loading model...");
					Model model = mf.createModel(Reasoning.owl);
					model.open();
					
					TupleQueryResult results  = null;
					try {
						connection = repository.getConnection();

						TupleQuery tupleQuery = connection.prepareTupleQuery(QueryLanguage.SPARQL, QUERY_ALL);
						results = tupleQuery.evaluate();						
						
						while (results.hasNext()) {
							BindingSet bindingSet = results.next();
							
							log.debug(bindingSet.getValue("x").toString() + " - "
								+ bindingSet.getValue("p").toString() + " - "
								+ bindingSet.getValue("y").toString());

							model.addStatement(
								model.createURI(bindingSet.getValue("x").toString()),
								model.createURI(bindingSet.getValue("p").toString()),
								model.createURI(bindingSet.getValue("y").stringValue().toString()));
						}
					} catch (Exception ex) {						
						finalResult.put("exception", createException("Model cration failed", ex.getMessage()));
						return finalResult;
					} finally {
						if(results!=null)  results.close();
					}
					
					log.info("Number of imported triples " + model.size());
					
					// Load validation rules
					log.info("Loading validation rules...");
					try {
						if (validationRules == null) {
							InputStream in = this.getClass().getClassLoader().getResourceAsStream(VALIDATION_RULES_FILE);
							validationRules = (List<Map<String, Object>>) JSONUtils.fromInputStream(in, "UTF-8");
						}
					} catch (IOException ex) {
						finalResult.put("exception", createException("Validation rules loading failed", ex.getMessage()));
						return finalResult;
					}					
					
					log.info("Applying validation rules...");
					int totalPass = 0, totalError = 0, totalWarn = 0, totalSkip = 0, totalTotal = 0;
					ArrayList<Map<String, Object>> result = new ArrayList<Map<String, Object>>(validationRules);
					for (Map<String, Object> section : result) {
						// for each section
						int sectionPass = 0, sectionError = 0, sectionWarn = 0, sectionSkip = 0;
						for (Map<String, Object> rule : ((List<Map<String, Object>>) section.get("constraints"))) {
							log.debug("Number of loaded constraints " + rule.size());
							
							totalTotal++;
							// process each rule
							String queryString = (String) rule.get("query");
							String preconditionQueryString = (String) rule.get("precondition");
							log.debug("Preconditions:  " + preconditionQueryString);
							if (queryString == null || "".equals(queryString)) {
								totalSkip++;
								sectionSkip++;
								rule.put("status", "skip");
								rule.put("result", "Validation rule not implemented");
							} else {
								log.debug("Query: " + queryString);

								int count = 0;
								try {
									boolean preconditionOK = true;
									// TODO check if there is a precondition query and run
									// that first to determine whether rule applies
									if (preconditionQueryString != null && !"".equals(preconditionQueryString)) {
										boolean preconditionSatisfied = model.sparqlAsk(preconditionQueryString);
										if (!preconditionSatisfied) {
											log.debug("Precondition not satisfied");
											// if precondition did not produce any matches,
											// set status to skip
											rule.put("status", "skip");
											rule.put("result", "Rule does not apply to supplied data: "
												+ rule.get("preconditionMessage"));
											totalSkip++;
											sectionSkip++;
											preconditionOK = false;
										}
									}
									if (preconditionOK) {
										log.info("Running query... " + queryString);
										// run the query and store the result back into the
										// constraint object
										QueryResultTable resultTable = model.sparqlSelect(queryString);
										List<String> vars = resultTable.getVariables();
										List<Map<String, String>> matches = new ArrayList<Map<String, String>>();
										for (QueryRow row : resultTable) {
											boolean nullValues = true;
											for (String var : vars) {
												org.ontoware.rdf2go.model.node.Node val = row
														.getValue(var);
												if (val != null && !val.toString().equals("0")) {
													nullValues = false;
													HashMap<String, String> r = new HashMap<String, String>();
													r.put(var, row.getValue(var)
															.toString());
													matches.add(r);
												}
											}
											if (!nullValues) {
												count++;
											}
										}
										if (count == 0) {
											rule.put("status", "pass");
											rule.put("result", "");
											totalPass++;
											sectionPass++;
										} else {
											// if there are results, the validation failed,
											// so set the status from the severity
											// add results to the result so that they can be
											// displayed
											rule.put("result", matches);
											String severity = (String) rule.get("severity");
											rule.put("status", severity);
											if ("error".equals(severity)) {
												totalError++;
												sectionError++;
											} else {
												totalWarn++;
												sectionWarn++;
											}
										}
									}
								} catch (Exception e) {
									// if there were any errors running queries, set status
									// to skip
									log.warn("error validating: "
											+ rule.get("description") + " "
											+ e.getMessage());
									rule.put("status", "skip");
									rule.put("result", "Error evaluating validation rule: "
											+ e.getMessage());
									totalSkip++;
									sectionSkip++;
								}
							}
							
							// section summaries for validation report
							section.put("pass", sectionPass);
							section.put("error", sectionError);
							section.put("warn", sectionWarn);
							section.put("skip", sectionSkip);
							if (sectionError > 0) {
								section.put("status", "error");
							} else if (sectionWarn > 0) {
								section.put("status", "warn");
							} else if (sectionPass == 0) {
								section.put("status", "skip");
							} else {
								section.put("status", "pass");
							}
						}
					}
			        finalResult.put("result",result);
			        finalResult.put("error",totalError);
			        finalResult.put("warn", totalWarn);
			        finalResult.put("pass",totalPass);
			        finalResult.put("skip",totalSkip);
			        finalResult.put("total", totalTotal);

			        // destroy temp rdf model
			        model.close();
			        return finalResult;
				} catch (Exception ex) {
					finalResult.put("exception", createException("OA JSON validation failed", ex.getMessage()));
					return finalResult;
				}
			} else {
				finalResult.put("exception", createException("Format not supported", "Format not supported:" + contentType));
				return finalResult;
			}
		} catch (Exception ex) {
			finalResult.put("exception", createException("OA validation failed", ex.getMessage()));
			return finalResult;
		}
	}
}
