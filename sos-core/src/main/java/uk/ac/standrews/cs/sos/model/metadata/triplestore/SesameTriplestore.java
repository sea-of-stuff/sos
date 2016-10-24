package uk.ac.standrews.cs.sos.model.metadata.triplestore;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SesameTriplestore {

    public void addTriple(String subject, String predicate, String object) {

        Repository rep = new SailRepository(new MemoryStore());
        rep.initialize();
        ValueFactory f = rep.getValueFactory();

        try (RepositoryConnection conn = rep.getConnection()) {
            IRI pred = createPredicate(predicate);
            conn.add(f.createIRI(subject), pred, f.createLiteral(object));
            // conn.add(f.createIRI(subject), RDF.PREDICATE, f.createLiteral(object));
        }

    }

    public void getTriples(/* query */) {

    }

    public void persist() {

    }

    // TODO - look at how RDF or RDFS define the dictionary
    private IRI createPredicate(String predicate) {
        SimpleValueFactory factory = SimpleValueFactory.getInstance();
        return factory.createIRI("sos://", predicate);
    }
}
