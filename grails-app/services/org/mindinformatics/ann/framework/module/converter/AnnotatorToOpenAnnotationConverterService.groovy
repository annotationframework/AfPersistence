package org.mindinformatics.ann.framework.module.converter

import org.codehaus.groovy.grails.web.json.JSONObject
import org.openrdf.model.ValueFactory
import org.openrdf.query.QueryLanguage
import org.openrdf.query.TupleQuery
import org.openrdf.query.TupleQueryResult
import org.openrdf.repository.RepositoryConnection


class AnnotatorToOpenAnnotationConverterService {

	def iTripleStorePersistence;
	
	public void normalize(JSONObject json) {
		
		iTripleStorePersistence.getRepository();
		
		//Model model = mf.createModel(Reasoning.owl);
		//model.open();
		
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
				
				// Annotation body type
				con.add(bodyUri,
					f.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
					f.createURI("http://purl.org/dc/dcmitype/Text"), context1);
				
				// Annotation body chars
				con.add(bodyUri,
					f.createURI("http://www.w3.org/2011/content#chars"),
					f.createLiteral(json.text), context1);
				
				RepositoryConnection con1 = repository.getConnection();
				try {
					String queryString = "SELECT ?p ?o WHERE { GRAPH <" + context1.toString() + "> { <" + bodyUri + "> ?p ?o .}}";
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
				
			} finally {
				con.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
