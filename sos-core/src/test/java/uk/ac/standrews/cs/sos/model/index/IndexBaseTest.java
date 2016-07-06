package uk.ac.standrews.cs.sos.model.index;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import uk.ac.standrews.cs.sos.exceptions.IndexException;
import uk.ac.standrews.cs.sos.interfaces.index.Index;

import java.io.IOException;
import java.lang.reflect.Method;

import static uk.ac.standrews.cs.sos.model.index.IndexBaseTest.INDEX_TYPE.LUCENE;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class IndexBaseTest {

    protected abstract INDEX_TYPE getIndexType();
    protected Index index;

    @BeforeMethod
    public void setUp(Method method) throws IndexException {
        INDEX_TYPE type = getIndexType();
        System.out.println(type.toString() + " :: " + method.getName());
        index = new IndexFactory().getIndex(type);
    }

    @AfterMethod
    public void tearDown() throws IOException, IndexException {
        index.flushDB();
        index.killInstance();

        // HelperTest.DeletePath(Configuration.getInstance().getIndexDirectory());
    }

    @DataProvider(name = "index-manager-provider")
    public static Object[][] indexProvider() throws IOException {
        return new Object[][] {
                {LUCENE}
        };
    }

    public enum INDEX_TYPE {
        LUCENE
    }

    public class IndexFactory {

        public Index getIndex(INDEX_TYPE type) throws IndexException {
            switch(type) {
                case LUCENE:
                    return LuceneIndex.getInstance();
            }
            return null;
        }
    }
}
