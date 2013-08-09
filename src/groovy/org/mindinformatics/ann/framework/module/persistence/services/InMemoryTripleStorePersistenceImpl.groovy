package org.mindinformatics.ann.framework.module.persistence.services

import org.ontoware.rdf2go.Reasoning
import org.ontoware.rdf2go.model.Model
import org.openrdf.model.Literal
import org.openrdf.model.Statement
import org.openrdf.model.ValueFactory
import org.openrdf.query.QueryLanguage
import org.openrdf.query.TupleQuery
import org.openrdf.query.TupleQueryResult
import org.openrdf.rdf2go.RepositoryModelFactory
import org.openrdf.repository.Repository
import org.openrdf.repository.RepositoryConnection
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
	public String store(String username, String URL, File annotation) {
		
		ValueFactory f = repository.getValueFactory();
		org.openrdf.model.URI context1 = f.createURI("http://example.org/" + annotation.name);
		
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
		return null;
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
			String queryString = "SELECT ?s ?p ?o WHERE { ?s ?p ?o .}";
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



}
