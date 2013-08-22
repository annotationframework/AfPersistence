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
package org.mindinformatics.ann.framework.module.persistence.services

import org.codehaus.groovy.grails.web.json.JSONObject
import org.openrdf.model.Statement

/**
 * @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
 */
interface ITripleStorePersistence {
	
	public String store(String username, String URL, File annotation);
	
	/**
	 * Storage of the normalized triples encoding the annotation..
	 * @param username		Username of the user performing the request
	 * @param URL			Address of the annotated resource
	 * @param statement		List of statements encoding the annotations
	 * @return The URI of the graph created to store the annotation
	 */
	public String store(String username, String URL, List<Statement> statements);
	
	/**
	 * Retrieval of annotation for a given resource.
	 * @param username		Username of the user performing the request
	 * @param URL			Address of the resource of interest
	 * @return The collection of annotation statements
	 */
	public List<Statement> retrieve(String username, String URL);
	
	/**
	 * Retrieval of annotation public annotation for a given resource.
	 * @param URL			Address of the resource of interest
	 * @return The collection of annotation statements
	 */
	public List<Statement> retrieve(String URL);
	
	public List<Statement> retrieveGraph(String URL);
	
	public JSONObject retrieveGraphAsJson(String URL);
}
