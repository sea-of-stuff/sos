package uk.ac.standrews.cs.sos.impl.metadata.triplestore;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import uk.ac.standrews.cs.sos.interfaces.metadata.Triplestore;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SesameTriplestore implements Triplestore {

    private Repository repo;
    private ValueFactory valueFactory;

//    File dataDir = new File("/path/to/datadir/");
//    Repository repo = new SailRepository(new NativeStore(dataDir));

    public SesameTriplestore() {
        repo = new SailRepository(new MemoryStore());
        repo.initialize();
        valueFactory = repo.getValueFactory();
    }

    @Override
    public void addTriple(String subject, String predicate, String object) {

        try (RepositoryConnection conn = repo.getConnection()) {
            IRI subj = createSOSIRI(subject);
            IRI pred = createSOSIRI(predicate);
            Literal obj = valueFactory.createLiteral(object);

            conn.add(subj, pred, obj);
            // conn.add(f.createIRI(subject), RDF.PREDICATE, f.createLiteral(object));
        }

    }

    // see http://docs.rdf4j.org/programming/#_using_a_repository_repositoryconnections
    @Override
    public void getTriples(String predicate) {

        try (RepositoryConnection conn = repo.getConnection()) {
            String queryString = "PREFIX sos:<sos://> SELECT ?x ?y WHERE { ?x sos:" + predicate + " ?y . } ";
            TupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, queryString);


            try (TupleQueryResult result = tupleQuery.evaluate()) {
                while (result.hasNext()) {  // iterate over the result
                    BindingSet bindingSet = result.next();
                    Value valueOfX = bindingSet.getValue("x");
                    Value valueOfY = bindingSet.getValue("y");

                    System.out.println(valueOfX + " ||| " + valueOfY);
                }
            }
        }

    }

    @Override
    public void getTriples(String object, String predicate) {

        try (RepositoryConnection conn = repo.getConnection()) {
            String queryString = "PREFIX sos:<sos://> SELECT ?y WHERE { sos:" + object + " sos:" + predicate + " ?y . } ";
            TupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, queryString);


            try (TupleQueryResult result = tupleQuery.evaluate()) {
                while (result.hasNext()) {  // iterate over the result
                    BindingSet bindingSet = result.next();
                    Value valueOfY = bindingSet.getValue("y");

                    System.out.println(valueOfY);
                }
            }
        }

    }

    @Override
    public void persist() {

    }

    // TODO - look at how RDF or RDFS define the dictionary
    private IRI createSOSIRI(String predicate) {
        return valueFactory.createIRI("sos://", predicate);
    }
}
