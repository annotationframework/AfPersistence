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
import org.mindinformatics.ann.framework.module.encoding.IContentAsRdfVocabulary
import org.mindinformatics.ann.framework.module.encoding.IDublinCoreElementsVocabulary
import org.mindinformatics.ann.framework.module.encoding.IDublinCoreTypesVocabulary
import org.mindinformatics.ann.framework.module.encoding.IMimeTypesVocabulary
import org.mindinformatics.ann.framework.module.encoding.IOpenAnnotation
import org.mindinformatics.ann.framework.module.encoding.IOpenAnnotationJsonEncoder
import org.mindinformatics.ann.framework.module.encoding.IRdfVocabulary
import org.openrdf.model.Literal
import org.openrdf.model.URI
import org.openrdf.model.ValueFactory
import org.openrdf.repository.Repository
import org.openrdf.repository.RepositoryConnection


/**
 * @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
 */
class AnnotatorJsEncoder implements IOpenAnnotationJsonEncoder {

	
	private void addStatement(RepositoryConnection con, ValueFactory f, org.openrdf.model.URI subject, String predicate, String object, org.openrdf.model.URI context) {
		con.add(subject, f.createURI(predicate), f.createURI(object), context);
	}
	
	private void addStatement(RepositoryConnection con, ValueFactory f, org.openrdf.model.URI subject, String predicate,  org.openrdf.model.URI object, org.openrdf.model.URI context) {
		con.add(subject, f.createURI(predicate), object, context);
	}
	
	private void addStatement(RepositoryConnection con, ValueFactory f, org.openrdf.model.URI subject, String predicate,  Literal object, org.openrdf.model.URI context) {
		con.add(subject, f.createURI(predicate), object, context);
	}
	
	private void createTextualBody(RepositoryConnection con, ValueFactory f,  org.openrdf.model.URI annotationUri,  org.openrdf.model.URI bodyUri, String text, org.openrdf.model.URI context) {
		addStatement(con, f, bodyUri, IRdfVocabulary.PROPERTY_TYPE_URI, IDublinCoreTypesVocabulary.CLASS_TEXT_URI, context);
		addStatement(con, f, bodyUri, IRdfVocabulary.PROPERTY_TYPE_URI, IContentAsRdfVocabulary.CLASS_CONTENTASTEXT_URI, context);
		addStatement(con, f, bodyUri, (IDublinCoreElementsVocabulary.PROPERTY_FORMAT_URI), f.createLiteral(IMimeTypesVocabulary.VALUE_TEXT_JSON), context);
		addStatement(con, f, bodyUri, IContentAsRdfVocabulary.PROPERTY_CHARS_URI, f.createLiteral(text), context);
		addStatement(con, f, annotationUri, IOpenAnnotation.PROPERTY_HASBODY_URI, bodyUri, context);
	}
	
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
				addStatement(con, f, annotationUri, IRdfVocabulary.PROPERTY_TYPE_URI, IOpenAnnotation.CLASS_ANNOTATION_URI, context1);
				
				// Annotation body
				org.openrdf.model.URI bodyUri = f.createURI("http://example.org/body/" + json.id);
				createTextualBody(con, f, annotationUri, bodyUri, json.text, context1);
				
				// Annotation target
				org.openrdf.model.URI targetUri = f.createURI("http://example.org/target/" + json.id);
				addStatement(con, f, annotationUri, IOpenAnnotation.PROPERTY_HASTARGET_URI, targetUri, context1);
				addStatement(con, f, targetUri, IRdfVocabulary.PROPERTY_TYPE_URI, IOpenAnnotation.CLASS_SPECIFICRESOURCE_URI, context1);
				addStatement(con, f, targetUri, IOpenAnnotation.PROPERTY_HASSOURCE_URI, json.uri, context1);
				
				// Annotation target selector
				for(int i=0; i<json.ranges.size(); i++) {
					org.openrdf.model.URI selectorUri = f.createURI("http://example.org/selector/" + json.id + "_" + i);
					addStatement(con, f, targetUri, IOpenAnnotation.PROPERTY_HASSELECTOR_URI, selectorUri, context1);
					addStatement(con, f, selectorUri, IRdfVocabulary.PROPERTY_TYPE_URI, "http://www.annotationframework.org/ns/af#AnnotatorPositionSelector", context1);
					addStatement(con, f, selectorUri, "http://www.annotationframework.org/ns/af#start", f.createLiteral(json.ranges[i].start), context1);
					addStatement(con, f, selectorUri, "http://www.annotationframework.org/ns/af#startOffset", f.createLiteral(json.ranges[i].startOffset), context1);
					addStatement(con, f, selectorUri, "http://www.annotationframework.org/ns/af#end", f.createLiteral(json.ranges[i].end), context1);
					addStatement(con, f, selectorUri, "http://www.annotationframework.org/ns/af#endOffset", f.createLiteral(json.ranges[i].endOffset), context1);
				}						
				return context1;
				
			} finally {
				con.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
