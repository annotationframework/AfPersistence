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
package org.mindinformatics.ann.framework.module.persistence.services;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

/**
 * http://jena.apache.org/tutorials/rdf_api.html
 * 
 * @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
 */
public class InMemoryJenaTripleStoreImpl {

	Dataset dataset;
	
	public InMemoryJenaTripleStoreImpl() {
		dataset =  DatasetFactory.createMem();
	}
	
	/**
	 * Returns the in memory Dataset implementation
	 * @return	The Dataset
	 */
	public Dataset getDataset() {
		return dataset;
	}
	
	/**
	 * Stores a Named Graph
	 * @param username	Username of the user who performed the request
	 * @param graphUri		URI identifying the Named Graph
	 * @param model		The Model to store
	 */
	public void store(String username, String graphUri, Model model) {
		dataset.addNamedModel(graphUri, model);
	}
	
	/**
	 * Returns the Model of a specific Named Graph
	 * @param username  Username of the user who performed the request
	 * @param graphUri		URI identifying the Named Graph
	 * @return The Model of the Named Graph
	 */
	public Model getNamedModel(String username, String graphUri) {
		return dataset.getNamedModel(graphUri);
	}
	
	/**
	 * Returns the Model of the Named Graph of a specific Annotation
	 * @param username	Username of the user who performed the request
	 * @param annotationUri		URI identifying an Annotation
	 * @return The Model of the Named Graph of a specific Annotation
	 */
	public Model retrieveAnnotationModel(String username, String annotationUri) {
		String queryString = "SELECT ?g WHERE { GRAPH ?g { <"
				+ annotationUri
				+ "> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/oa#Annotation> .}}";
		Query query = QueryFactory.create(queryString);
		QueryExecution qExec = QueryExecutionFactory.create(query, dataset);
		try {
			ResultSet results = qExec.execSelect();
			for (; results.hasNext();) {
				QuerySolution soln = results.nextSolution();
				Resource graphUri = soln.getResource("g"); 	
				return getNamedModel(username, graphUri.getURI());		
			}
		} finally {
			qExec.close();
		}
		return null;
	}
	
	/**
	 * Returns the list of Statements of a specific Named Graph
	 * @param username	Username of the user who performed the request
	 * @param graphUri		URI identifying the Named Graph
	 * @return The list of Statements of the Named Graph
	 */
	public List<Statement> retrieveGraphStatements(String username, String graphUri) {
		List<Statement> statements = new ArrayList<Statement>();
		StmtIterator iter = dataset.getNamedModel(graphUri).listStatements();
		while (iter.hasNext()) {
			Statement stmt      = iter.nextStatement();  // get next statement
			statements.add(stmt);
		}
		return statements;
	}
	
	/**
	 * Returns the list of Statements of the Named Graph of a specific Annotation
	 * @param username	Username of the user who performed the request
	 * @param annotationUri		URI identifying an Annotation
	 * @return The list of Statements of the Named Graph of a specific Annotation
	 */
	public List<Statement> retrieveAnnotation(String username, String annotationUri) {
		String queryString = "SELECT ?g WHERE { GRAPH ?g { <"
				+ annotationUri
				+ "> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <>http://www.w3.org/ns/oa#Annotation .}}";
		Query query = QueryFactory.create(queryString);
		QueryExecution qExec = QueryExecutionFactory.create(query, dataset);
		try {
			ResultSet results = qExec.execSelect();
			for (; results.hasNext();) {
				QuerySolution soln = results.nextSolution();
				Resource graphUri = soln.getResource("g"); 	
				return retrieveGraphStatements(username, graphUri.getURI());		
			}
		} finally {
			qExec.close();
		}
		return null;
	}
}
