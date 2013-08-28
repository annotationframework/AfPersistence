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
package org.mindinformatics.ann.framework.module.encoding.annotatorjs

import org.codehaus.groovy.grails.web.json.JSONObject
import org.mindinformatics.ann.framework.module.encoding.IOpenAnnotationJsonEncoder
import org.openrdf.model.URI
import org.openrdf.model.ValueFactory
import org.openrdf.repository.Repository
import org.openrdf.repository.RepositoryConnection


/**
 * @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
 */
class AnnotatorJsEncoder implements IOpenAnnotationJsonEncoder {

	@Override
	public org.openrdf.model.URI encode(Repository repository, JSONObject json) {
		ValueFactory f = repository.getValueFactory();
		// Graph URI
		org.openrdf.model.URI context1 = f.createURI("http://example.org/annotation/graph/" + System.currentTimeMillis()); // Change to UUID
		
		org.openrdf.model.URI annotationUri = f.createURI("http://example.org/annotation/" + json.id);
		json.put("uri", annotationUri.toString());
		
		try {
			RepositoryConnection con = repository.getConnection();
			try {
				// Annotation type
				con.add(annotationUri,
					f.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
					f.createURI("http://www.w3.org/ns/oa#Annotation"), context1);
				
				// Annotation body
				org.openrdf.model.URI bodyUri = f.createURI("http://example.org/body/" + json.id);
				con.add(annotationUri,
					f.createURI("http://www.w3.org/ns/oa#hasBody"),
					bodyUri, 
					context1);
				con.add(bodyUri,
					f.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
					f.createURI("http://purl.org/dc/dcmitype/Text"), context1);
				con.add(bodyUri,
					f.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
					f.createURI("http://www.w3.org/2011/content#ContentAsText"), context1);
				con.add(bodyUri,
					f.createURI("http://purl.org/dc/elements/1.1/format"),
					f.createLiteral("text/plain"), context1);
				// Annotation body chars
				con.add(bodyUri,
					f.createURI("http://www.w3.org/2011/content#chars"),
					f.createLiteral(json.text), context1);
				
				// Annotation target
				org.openrdf.model.URI targetUri = f.createURI("http://example.org/target/" + json.id);
				con.add(annotationUri,
					f.createURI("http://www.w3.org/ns/oa#hasTarget"),
					targetUri, context1);
				con.add(targetUri,
					f.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
					f.createURI("http://www.w3.org/ns/oa#SpecificResource"), context1);
				con.add(targetUri,
					f.createURI("http://www.w3.org/ns/oa#hasSource"),
					f.createURI(json.uri), context1);
				// Annotation target selector
				org.openrdf.model.URI selectorUri = f.createURI("http://example.org/selector/" + json.id);
				con.add(targetUri,
					f.createURI("http://www.w3.org/ns/oa#hasSelector"),
					selectorUri, context1);
				con.add(selectorUri,
					f.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
					f.createURI("http://www.annotationframework.org/ns/af#AnnotatorPositionSelector"), context1);
				con.add(selectorUri,
					f.createURI("http://www.annotationframework.org/ns/af#start"),
					f.createLiteral(json.ranges[0].start), context1);
				con.add(selectorUri,
					f.createURI("http://www.annotationframework.org/ns/af#startOffset"),
					f.createLiteral(json.ranges[0].startOffset), context1);
				con.add(selectorUri,
					f.createURI("http://www.annotationframework.org/ns/af#end"),
					f.createLiteral(json.ranges[0].end), context1);
				con.add(selectorUri,
					f.createURI("http://www.annotationframework.org/ns/af#endOffset"),
					f.createLiteral(json.ranges[0].endOffset), context1);		
				
				return context1;
				
			} finally {
				con.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
