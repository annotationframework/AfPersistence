package org.mindinformatics.ann.framework.module.persistence.services

import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject
import org.ontoware.rdf2go.Reasoning
import org.ontoware.rdf2go.model.Model
import org.openrdf.model.Literal
import org.openrdf.model.Statement
import org.openrdf.model.ValueFactory
import org.openrdf.query.BindingSet
import org.openrdf.query.QueryLanguage
import org.openrdf.query.TupleQuery
import org.openrdf.query.TupleQueryResult
import org.openrdf.rdf2go.RepositoryModelFactory
import org.openrdf.repository.Repository
import org.openrdf.repository.RepositoryConnection
import org.openrdf.repository.RepositoryResult
import org.openrdf.repository.sail.SailRepository
import org.openrdf.rio.RDFFormat
import org.openrdf.sail.memory.MemoryStore

class InMemoryTripleStorePersistenceImpl implements ITripleStorePersistence {
	
	Repository repository;
	Model model;
		
	public InMemoryTripleStorePersistenceImpl() {
		RepositoryModelFactory mf = new RepositoryModelFactory();
		model = mf.createModel(Reasoning.owl);

		try {
			repository = new SailRepository(new MemoryStore());
			repository.initialize();
		} catch (Exception e) {
			System.out.println("D-----> " + e.getMessage());
		}
	}
	
	@Override
	public Repository getConnection() {
		return repository;
	}
	
	@Override
	public String store(String username, String URL, File annotation) {
		
		def grailsApplication = ApplicationHolder.application;
		ValueFactory f = repository.getValueFactory();
		org.openrdf.model.URI context1 = f.createURI(grailsApplication.config.af.node.base.url + 'blob/' + annotation.name.replaceAll("annotation-","").replaceAll(".json", ""));
		
		RepositoryConnection connection = repository.getConnection();
		InputStream inputRDF = new FileInputStream(annotation);
		try {
			connection.add(inputRDF, "http://localhost/jsonld/",RDFFormat.JSONLD, context1);
			connection.commit();
		} catch (Exception ex) {
			System.out.println("parsing  failed!");
		} finally {
			connection.close();
		}
		return context1;
	}
	
	@Override
	public String store(String username, String URL, List<Statement> statements) {
		ValueFactory f = repository.getValueFactory();
		org.openrdf.model.URI context1 = f.createURI("http://example.org/" + System.currentTimeMillis());
		try {
			RepositoryConnection con = repository.getConnection();
			try {
				String namespace = "http://www.foo.com/bar#";
				org.openrdf.model.URI mySubject = f.createURI(namespace, "actor1");
				org.openrdf.model.URI myPredicate = f.createURI(namespace, "hasName");
				Literal myObject = f.createLiteral("Tom Hanks");

				con.add(mySubject, myPredicate, myObject, context1);
			} finally {
				con.close();
			}
		} catch (Exception e) {
			// handle exception
			System.out.println('Exception.... ' + e.getMessage());
			e.printStackTrace();
		}
		RepositoryConnection con2 = repository.getConnection();
		System.out.println(con2.isEmpty());
		return context1;
	}

	@Override
	public List<Statement> retrieve(String username, String URL) {
		System.out.println('querying....');
		RepositoryConnection con = repository.getConnection();
		try {
			String queryString = "SELECT ?s ?p ?o WHERE { GRAPH <http://example.org/context1> { ?s ?p ?o .}}";
			TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
			TupleQueryResult result = tupleQuery.evaluate();
			System.out.println('querying....');
			while(result.hasNext()) {
				System.out.println(result.next());
			}
		} catch (Exception e) {
			System.out.println('querying ex....' + e.getMessage());

			e.printStackTrace();
		} finally {
			con.close();
		}
	}

	@Override
	public List<Statement> retrieve(String URL) {
		System.out.println('querying....');
		RepositoryConnection con = repository.getConnection();
		try {
			//String queryString = "SELECT ?s ?p ?o WHERE { GRAPH <http://example.org/1376074729068> { ?s ?p ?o .}}";
			//String queryString = "SELECT ?s ?p ?o WHERE { ?s ?p ?o .}";
			String queryString = "SELECT ?g ?s ?p ?o WHERE { GRAPH ?g { ?s ?p ?o .}}";
			TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
			TupleQueryResult result = tupleQuery.evaluate();
			System.out.println('querying....');
			while(result.hasNext()) {
				System.out.println(result.next());
			}
		} catch (Exception e) {
			System.out.println('querying ex....' + e.getMessage());
			e.printStackTrace();
		} finally {
			con.close();
		}
	}
	
	@Override
	public List<Statement> retrieveGraph(String URL) {
		ValueFactory f = repository.getValueFactory();
		List<Statement> stats = new  ArrayList<Statement>();
		System.out.println('querying graph.... ' + URL);
		RepositoryConnection con = repository.getConnection();
		try {
			//String queryString = "SELECT ?s ?p ?o WHERE { GRAPH <http://example.org/1376074729068> { ?s ?p ?o .}}";
			//String queryString = "SELECT ?s ?p ?o WHERE { ?s ?p ?o .}";
			String queryString = "SELECT ?g ?s ?p ?o WHERE { GRAPH <" + URL + "> { ?s ?p ?o .}}";
			TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
			TupleQueryResult result = tupleQuery.evaluate();
			while(result.hasNext()) {
				BindingSet bs = result.next();
				System.out.println(bs.getBinding("s").getValue().stringValue() + " - " + bs.getBinding("p").getValue().stringValue()  + " - " + bs.getBinding("o").getValue().stringValue());
			}
			
			RepositoryResult<Statement> res = con.getStatements(null, null, null, true, f.createURI(URL));
			while(res.hasNext()) {
				stats.add(res.next());
			}
			
		} catch (Exception e) {
			System.out.println('querying ex....' + e.getMessage());
			e.printStackTrace();
		} finally {
			con.close();
		}
		return stats;
	}

	@Override
	public JSONObject retrieveGraphAsJson(String URL) {
		ValueFactory f = repository.getValueFactory();
		JSONObject res = new JSONObject();
		System.out.println('querying graph.... ' + URL);
		RepositoryConnection con = repository.getConnection();
		try {
			//String queryString = "SELECT ?s ?p ?o WHERE { GRAPH <http://example.org/1376074729068> { ?s ?p ?o .}}";
			//String queryString = "SELECT ?s ?p ?o WHERE { ?s ?p ?o .}";
			String queryString = "SELECT ?s ?p ?o WHERE { GRAPH <" + URL + "> { ?s ?p ?o .}}";
			TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
			TupleQueryResult result = tupleQuery.evaluate();

			JSONArray stats = new  JSONArray();
			
			int counterLimit = 0
			while(result.hasNext() && counterLimit<20) {
				BindingSet bs = result.next();
				JSONObject triple = new JSONObject();
				triple.put("g", URL);
				triple.put("s", bs.getBinding("s").getValue().stringValue());
				triple.put("p", bs.getBinding("p").getValue().stringValue());
				triple.put("o", bs.getBinding("o").getValue().stringValue());
				stats.add(triple);
				counterLimit++;
				//System.out.println(bs.getBinding("s").getValue().stringValue() + " - " + bs.getBinding("p").getValue().stringValue()  + " - " + bs.getBinding("o").getValue().stringValue());
			}
			
			if(result.hasNext()) res.put("truncated", "yes");
			else res.put("truncated", "no");
			res.put("graph", URL);
			res.put("triples", stats);
		} catch (Exception e) {
			System.out.println('querying ex....' + e.getMessage());
			e.printStackTrace();
		} finally {
			con.close();
		}
		return res;
	}


}
