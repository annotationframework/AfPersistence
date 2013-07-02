package net.metadata.openannotation.lorestore.servlet.rdf2go;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ontoware.rdf2go.Reasoning;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
import org.openrdf.model.Value;
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
import org.springframework.web.servlet.ModelAndView;

import de.dfki.km.json.JSONUtils;


public class OAValidationHandler {

	List<Map<String, Object>> validationRules;

	@SuppressWarnings("unchecked")
	public ModelAndView validate(InputStream inputRDF, String contentType) {
		ModelAndView mav = new ModelAndView("validation");
		RepositoryModelFactory mf = new RepositoryModelFactory();
		Model model = mf.createModel(Reasoning.owl);
		Repository repository;
		try {
			if (contentType.contains("application/json")) {
				try {
					repository = new SailRepository(new MemoryStore());
					repository.initialize();
					
					RepositoryConnection connection = repository.getConnection();
					try {
						connection.add(inputRDF, "http://localhost/jsonld/",RDFFormat.JSONLD);
						connection.commit();
					} catch (Exception ex) {
						System.out.println("parsing  failed!");
					} finally {
						connection.close();
					}

					// Load the model
					model.open();
					RepositoryConnection con = repository.getConnection();

					String query = "SELECT ?x ?p ?y WHERE { ?x ?p ?y } ";
					TupleQuery tupleQuery = con.prepareTupleQuery(
							QueryLanguage.SPARQL, query);
					TupleQueryResult res = tupleQuery.evaluate();
					try {
						while (res.hasNext()) {
							BindingSet bindingSet = res.next();
							
							System.out.println(bindingSet.getValue("x").toString() + " - "
									+ bindingSet.getValue("p").toString() + " - "
									+ bindingSet.getValue("y").toString());
							
							Value valueOfX = bindingSet.getValue("x");
							Value valueOfP = bindingSet.getValue("p");
							Value valueOfY = bindingSet.getValue("y");

							model.addStatement(model.createURI(valueOfX.toString()),
									model.createURI(valueOfP.toString()),
									model.createURI(valueOfY.stringValue().toString()));
						}
					} catch (Exception e) {
						System.out.println("A-----> " + e.getMessage());
					} finally {
						res.close();
					}
					
					System.out.println("----0 " + model.size());
					
					// Load validation rules
					if (validationRules == null) {
						
						InputStream in = this.getClass().getClassLoader()
								.getResourceAsStream("OAConstraintsSPARQL.json");
						try {
							validationRules = (List<Map<String, Object>>) JSONUtils.fromInputStream(in, "UTF-8");
						} catch (IOException e) {
							System.out.println("Validation rules loading  failed!");
						}
					}
					
					
					
					int totalPass = 0, totalError = 0, totalWarn = 0, totalSkip = 0, totalTotal = 0;
					// clone the validation rules into an object that we will pass to the
					// ModelAndView
					ArrayList<Map<String, Object>> result = new ArrayList<Map<String, Object>>(validationRules);
					for (Map<String, Object> section : result) {
						// for each section
						int sectionPass = 0, sectionError = 0, sectionWarn = 0, sectionSkip = 0;
						for (Map<String, Object> rule : ((List<Map<String, Object>>) section.get("constraints"))) {
							System.out.println("----1  " + rule.size());
							
							totalTotal++;
							// process each rule
							String queryString = (String) rule.get("query");
							String preconditionQueryString = (String) rule.get("precondition");
							System.out.println("----Pre  " + preconditionQueryString);
							if (queryString == null || "".equals(queryString)) {
								totalSkip++;
								sectionSkip++;
								rule.put("status", "skip");
								rule.put("result", "Validation rule not implemented");
								System.out.println("----NO  " + queryString);
							} else {
								System.out.println("----YES  " + queryString);
								//model.open();

								/*
								RepositoryConnection conn = null;
								try {
									conn = repository.getConnection();
								} catch (RepositoryException e1) {
									// TODO Auto-generated catch block
									System.out.println("A-----> " + e1.getMessage());
								}
								*/

								int count = 0;
								int precondcount = 0;
								try {
									boolean preconditionOK = true;
									// TODO check if there is a precondition query and run
									// that first to determine whether rule applies

									if (preconditionQueryString != null && !"".equals(preconditionQueryString)) {
										System.out.println("ASK-----> ");
										boolean preconditionSatisfied = model.sparqlAsk(preconditionQueryString);
										// TupleQuery tupleQuery =
										// con.prepareTupleQuery(QueryLanguage.SPARQL,
										// preconditionQueryString);
										if(!preconditionSatisfied) System.out.println("NOOOOOOOOOOOOOO");
										if (!preconditionSatisfied) {
											// if precondition did not produce any matches,
											// set status to skip
											rule.put("status", "skip");
											rule.put(
													"result",
													"Rule does not apply to supplied data: "
															+ rule.get("preconditionMessage"));

											totalSkip++;
											sectionSkip++;
											preconditionOK = false;
										}
									}
									if (preconditionOK) {
										System.out.println("QUERY-----> ");
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
												// LOG.info(var + " " + row.toString());
												if (val != null
														&& !val.toString().equals("0")) {
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
									System.out.println("error validating: "
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
					
					// store results of validation in ModelAndView:
			        HashMap<String,Object> finalResult = new HashMap<String, Object>();
			        finalResult.put("result",result);
			        finalResult.put("error",totalError);
			        finalResult.put("warn", totalWarn);
			        finalResult.put("pass",totalPass);
			        finalResult.put("skip",totalSkip);
			        finalResult.put("total", totalTotal);
			        mav.addObject("result", finalResult);
			        // destroy temp rdf model
			        model.close();
			        return mav;


				} catch (Exception e) {
					// throw new
					// RequestFailureException(HttpServletResponse.SC_BAD_REQUEST,"Invalid JSON-LD");
					System.out.println("D-----> " + e.getMessage());
				}

			} else {
				System.out.println("What?");
				return mav;
			}
			
			
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			return null;
		}
		
		

	}
}
