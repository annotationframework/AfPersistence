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
package org.mindinformatics.ann.framework.module.encoding;

import grails.converters.JSON
import grails.test.mixin.*

import org.codehaus.groovy.grails.web.json.JSONObject
import org.junit.*
import org.mindinformatics.ann.framework.module.encoding.annotatorjs.AnnotatorJsEncoder
import org.openrdf.query.QueryLanguage
import org.openrdf.query.TupleQuery
import org.openrdf.query.TupleQueryResult
import org.openrdf.rdf2go.RepositoryModelFactory
import org.openrdf.repository.Repository
import org.openrdf.repository.RepositoryConnection
import org.openrdf.repository.sail.SailRepository
import org.openrdf.sail.memory.MemoryStore

//TODO test case with tags

/**
 * Testing the converter from the standard annotator.js format to the 
 * normalized Open Annotation format.
 * 
 * @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
 */
class AnnotatorJsEncoderTest {

	@Test
	void normalizeTest() {
		def json = '{"tags":[],"text":"safsafsa","quote":"hall","ranges":[{"endOffset":16,"start":"/div[1]/h4[1]","end":"/div[1]/h4[1]","startOffset":12}],"permissions":{"update":[],"admin":[],"delete":[],"read":[]},"uri":"http://afdemo.aws.af.cm/annotator/index","user":"jmiranda", "id":"32423432"}';
		
		JSONObject jsonObject = JSON.parse(json);
		
		RepositoryModelFactory mf = new RepositoryModelFactory();
		Repository repository;
		try {
			repository = new SailRepository(new MemoryStore());
			repository.initialize();
		} catch (Exception e) {
			System.out.println("D-----> " + e.getMessage());
		}
		
		AnnotatorJsEncoder a = new AnnotatorJsEncoder();
		org.openrdf.model.URI context1 = a.encode(repository, jsonObject);
		
		RepositoryConnection con1 = repository.getConnection();
		try {
			String queryString = "SELECT ?s ?p ?o WHERE { GRAPH <" + context1.toString() + "> { ?s ?p ?o .}}";
			TupleQuery tupleQuery = con1.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
			TupleQueryResult result = tupleQuery.evaluate();
			System.out.println('querying....');
			while(result.hasNext()) {
				System.out.println(result.next());
			}
		} catch (Exception e) {
			System.out.println('querying ex....' + e.getMessage());
			e.printStackTrace();
		} finally {
			con1.close();
		}
	}
}
