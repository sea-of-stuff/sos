package uk.ac.standrews.cs.sos.model.index;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.IndexException;
import uk.ac.standrews.cs.sos.interfaces.index.Index;
import uk.ac.standrews.cs.sos.model.SeaConfiguration;
import uk.ac.standrews.cs.sos.model.manifests.AssetManifest;
import uk.ac.standrews.cs.sos.model.manifests.AtomManifest;
import uk.ac.standrews.cs.sos.model.manifests.CompoundManifest;
import uk.ac.standrews.cs.sos.model.manifests.Content;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * Caching mechanism based on Lucene-core.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class LuceneIndex extends CommonIndex {

    private static LuceneIndex instance = null;
    private static IndexWriter indexWriter = null;
    private static SearcherManager searcherManager = null;
    private static IndexSearcher indexSearcher = null;
    private static SeaConfiguration instanceConfiguration;

    public static Index getInstance(SeaConfiguration configuration) throws IndexException {
        if(instance == null) {
            try {
                init(configuration);
            } catch (IOException e) {
                throw new IndexException(e);
            }
        }
        return instance;
    }

    private static void init(SeaConfiguration configuration) throws IOException {
        instanceConfiguration = configuration;
        String indexPath = instanceConfiguration.getIndexPath();
        Directory dir = FSDirectory.open(new File(indexPath).toPath());
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

        iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
        indexWriter = new IndexWriter(dir, iwc);
        indexWriter.commit();

        searcherManager = new SearcherManager(indexWriter, true, new SearcherFactory());
        instance = new LuceneIndex();
    }

    @Override
    public void killInstance() throws IndexException {
        try {
            if (indexWriter != null && indexWriter.isOpen()) {
                indexWriter.close();
            }

            if (searcherManager != null) {
                searcherManager.close();
            }
        } catch (IOException e) {
            throw new IndexException(e);
        }

        instance = null;
    }

    @Override
    public void flushDB() throws IndexException {
        try {
            indexWriter.commit();
        } catch (IOException e) {
            throw new IndexException(e);
        }
    }

    @Override
    protected void addAtomManifest(AtomManifest manifest) throws IndexException {
        boolean manifestAlreadyIndexed = guidExists(manifest.getContentGUID(), LuceneKeys.HANDLE_GUID);
        if(!manifestAlreadyIndexed) {
            indexAtomManifest(manifest);
        }
    }

    @Override
    protected void addCompoundManifest(CompoundManifest manifest) throws IndexException {
        boolean manifestAlreadyIndexed = guidExists(manifest.getContentGUID(), LuceneKeys.HANDLE_GUID);
        if(!manifestAlreadyIndexed) {
            indexCompoundManifest(manifest);
        }
    }

    @Override
    protected void addAssetManifest(AssetManifest manifest) throws IndexException {
        boolean manifestAlreadyIndexed = guidExists(manifest.getVersionGUID(), LuceneKeys.HANDLE_VERSION);
        if(!manifestAlreadyIndexed) {
            try {
                indexAssetManifest(manifest);
            } catch (IOException e) {
                throw new IndexException(e);
            }
        }
    }

    @Override
    public Collection<IGUID> getManifestsOfType(String type, int results, int skip) throws IndexException {
        updateIndexSearcher();

        Term term = new Term(LuceneKeys.HANDLE_TYPE, type);
        Collection<IGUID> retval = getGUIDsFromSearch(term, results, skip);

        releaseIndexSearcher();
        return retval;
    }

    @Override
    public SeaConfiguration getConfiguration() {
        return instanceConfiguration;
    }

    @Override
    public Collection<IGUID> getVersions(IGUID invariant, int results, int skip) throws IndexException {
        if (skip > results) {
            throw new IndexException();
        }

        updateIndexSearcher();

        Collection<IGUID> retval = new ArrayList<>();
        try {
            Term term = new Term(LuceneKeys.HANDLE_GUID, invariant.toString());
            Query query = new TermQuery(term);
            TopDocs topDocs = indexSearcher.search(query, results);
            ScoreDoc[] hits = topDocs.scoreDocs;
            for (int i = skip; i < hits.length; i++) {
                Document doc = indexSearcher.doc(hits[i].doc);
                String guid = doc.get(LuceneKeys.HANDLE_VERSION);
                retval.add(GUIDFactory.recreateGUID(guid));
            }
        } catch (IOException | GUIDGenerationException e) {
            throw new IndexException(e);
        }

        releaseIndexSearcher();
        return retval;
    }

    @Override
    public Collection<IGUID> getMetaLabelMatches(String value, int results, int skip) throws IndexException {
        updateIndexSearcher();

        Term term = new Term(LuceneKeys.HANDLE_LABEL, value);
        Collection<IGUID> retval = getGUIDsFromSearch(term, results, skip);

        releaseIndexSearcher();
        return retval;

    }

    private static void updateIndexSearcher() throws IndexException {
        try {
            searcherManager.maybeRefresh();
            indexSearcher = searcherManager.acquire();
        } catch (IOException e) {
            throw new IndexException(e);
        }
    }

    private static void releaseIndexSearcher() throws IndexException {
        try {
            searcherManager.release(indexSearcher);
            indexSearcher = null;
        } catch (IOException e) {
            throw new IndexException(e);
        }
    }

    private Collection<IGUID> getGUIDsFromSearch(Term term, int results, int skip) throws IndexException {
        if (skip > results) {
            throw new IndexException();
        }

        Collection<IGUID> retval = new HashSet<>();
        try {
            Query query = new TermQuery(term);
            TopDocs topDocs = indexSearcher.search(query, results);
            ScoreDoc[] hits = topDocs.scoreDocs;
            for (int i = skip; i < hits.length; i++) {
                Document doc = indexSearcher.doc(hits[i].doc);
                String guid = doc.get(LuceneKeys.HANDLE_GUID);
                retval.add(GUIDFactory.recreateGUID(guid));
            }
        } catch (IOException | GUIDGenerationException e) {
            throw new IndexException(e);
        }

        return retval;
    }

    private void indexAtomManifest(AtomManifest manifest) throws IndexException {
        Document doc = new Document();

        IGUID guid = manifest.getContentGUID();
        String type = manifest.getManifestType();
        doc.add(new StringField(LuceneKeys.HANDLE_GUID, guid.toString(), Field.Store.YES));
        doc.add(new StringField(LuceneKeys.HANDLE_TYPE, type, Field.Store.YES));

        try {
            indexWriter.addDocument(doc);
            indexWriter.commit();
        } catch (IOException e) {
            throw new IndexException(e);
        }
    }

    private void indexCompoundManifest(CompoundManifest manifest) throws IndexException {
        Document doc = new Document();
        IGUID guid = manifest.getContentGUID();
        String type = manifest.getManifestType();
        doc.add(new StringField(LuceneKeys.HANDLE_GUID, guid.toString(), Field.Store.YES));
        doc.add(new StringField(LuceneKeys.HANDLE_TYPE, type, Field.Store.YES));

        Collection<Content> contents = manifest.getContents();
        for (Content content : contents) {
            if (!contentExists(content)) {
                indexContent(content);
            }
        }

        try {
            indexWriter.addDocument(doc);
            indexWriter.commit();
        } catch (IOException e) {
            throw new IndexException(e);
        }
    }

    private void indexAssetManifest(AssetManifest manifest) throws IOException {
        Document doc = new Document();
        IGUID version = manifest.getVersionGUID();
        String type = manifest.getManifestType();
        IGUID invariant = manifest.getInvariantGUID();

        doc.add(new StringField(LuceneKeys.HANDLE_VERSION, version.toString(), Field.Store.YES));
        doc.add(new StringField(LuceneKeys.HANDLE_TYPE, type, Field.Store.YES));
        doc.add(new StringField(LuceneKeys.HANDLE_GUID, invariant.toString(), Field.Store.YES));

        indexWriter.addDocument(doc);
        indexWriter.commit();
    }

    private void indexContent(Content content) throws IndexException {
        Document contentDoc = new Document();
        contentDoc.add(new StringField(LuceneKeys.HANDLE_GUID, content.getGUID().toString(), Field.Store.YES));
        contentDoc.add(new StringField(LuceneKeys.HANDLE_LABEL, content.getLabel(), Field.Store.YES));
        try {
            indexWriter.addDocument(contentDoc);
        } catch (IOException e) {
            throw new IndexException(e);
        }
    }

    private boolean guidExists(IGUID guid, String key) throws IndexException {
        updateIndexSearcher();

        // http://stackoverflow.com/questions/30810879/how-to-check-if-document-exists-in-lucene-index
        TopDocs results;
        try {
            results = indexSearcher.search(new TermQuery(new Term(key, guid.toString())), 1);
        } catch (IOException e) {
            throw new IndexException(e);
        }

        releaseIndexSearcher();

        return results.totalHits > 0;
    }

    private boolean contentExists(Content content) throws IndexException {
        updateIndexSearcher();

        IGUID contentGUID = content.getGUID();
        String label = content.getLabel();

        BooleanQuery matchingQuery = new BooleanQuery();
        matchingQuery.add(new TermQuery(new Term(LuceneKeys.HANDLE_GUID, contentGUID.toString())), BooleanClause.Occur.MUST);
        matchingQuery.add(new TermQuery(new Term(LuceneKeys.HANDLE_LABEL, label)), BooleanClause.Occur.MUST);
        TopDocs results;
        try {
            results = indexSearcher.search(matchingQuery, 1);
        } catch (IOException e) {
            throw new IndexException(e);
        }

        releaseIndexSearcher();

        return results.totalHits > 0;
    }
}
