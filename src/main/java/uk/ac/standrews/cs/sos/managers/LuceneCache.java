package uk.ac.standrews.cs.sos.managers;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import uk.ac.standrews.cs.sos.configurations.SeaConfiguration;
import uk.ac.standrews.cs.sos.model.implementations.components.manifests.AssetManifest;
import uk.ac.standrews.cs.sos.model.implementations.components.manifests.AtomManifest;
import uk.ac.standrews.cs.sos.model.implementations.components.manifests.CompoundManifest;
import uk.ac.standrews.cs.sos.model.implementations.utils.Content;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUID;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUIDsha1;

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
public class LuceneCache extends CommonCache {

    private static LuceneCache instance = null;
    private static IndexWriter indexWriter = null;
    private static SearcherManager searcherManager = null;
    private static IndexSearcher indexSearcher = null;
    private static SeaConfiguration instanceConfiguration;

    private static final int QUERY_RESULTS = 10; // TODO - what to do if more than 10 results? have a buffer?

    public static MemCache getInstance(SeaConfiguration configuration) throws IOException {
        if(instance == null) {
            instanceConfiguration = configuration;
            Directory dir = FSDirectory.open(new File(configuration.getIndexPath()).toPath());
            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

            iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
            indexWriter = new IndexWriter(dir, iwc);
            indexWriter.commit();

            boolean applyAllDeletes = true;
            searcherManager = new SearcherManager(indexWriter, applyAllDeletes, new SearcherFactory());
            instance = new LuceneCache();
        }
        return instance;
    }

    @Override
    public void killInstance() throws IOException {
        if (indexWriter != null && indexWriter.isOpen()) {
            indexWriter.close();
        }

        if (searcherManager != null) {
            searcherManager.close();
        }

        instance = null;
    }

    @Override
    public void flushDB() throws IOException {
        indexWriter.commit();
    }

    @Override
    protected void addAtomManifest(AtomManifest manifest) throws IOException {

        boolean manifestAlreadyIndexed = guidExists(manifest.getContentGUID(), LuceneKeys.HANDLE_GUID);
        if(!manifestAlreadyIndexed) {
            indexAtomManifest(manifest);
        }
    }

    @Override
    protected void addCompoundManifest(CompoundManifest manifest) throws IOException {
        boolean manifestAlreadyIndexed = guidExists(manifest.getContentGUID(), LuceneKeys.HANDLE_GUID);
        if(!manifestAlreadyIndexed) {
            indexCompoundManifest(manifest);
        }
    }

    @Override
    protected void addAssetManifest(AssetManifest manifest) throws IOException {
        boolean manifestAlreadyIndexed = guidExists(manifest.getVersionGUID(), LuceneKeys.HANDLE_VERSION);
        if(!manifestAlreadyIndexed) {
            indexAssetManifest(manifest);
        }
    }

    @Override
    public Collection<GUID> getManifestsOfType(String type) throws IOException, ParseException {
        updateIndexSearcher();

        Term term = new Term(LuceneKeys.HANDLE_TYPE, type);
        Collection<GUID> retval = getGUIDsFromSearch(term);

        releaseIndexSearcher();
        return retval;
    }

    @Override
    public SeaConfiguration getConfiguration() {
        return instanceConfiguration;
    }

    @Override
    public Collection<GUID> getVersions(GUID invariant) throws IOException {
        updateIndexSearcher();

        Term term = new Term(LuceneKeys.HANDLE_GUID, invariant.toString());
        Collection<GUID> retval = new ArrayList<>();
        Query query = new TermQuery(term);
        TopDocs topDocs = indexSearcher.search(query, QUERY_RESULTS);
        ScoreDoc[] hits = topDocs.scoreDocs;
        for (int i = 0; i < hits.length; i++) {
            Document doc = indexSearcher.doc(hits[i].doc);
            String guid = doc.get(LuceneKeys.HANDLE_VERSION);
            retval.add(new GUIDsha1(guid));
        }

        releaseIndexSearcher();
        return retval;
    }

    @Override
    public Collection<GUID> getMetaLabelMatches(String value) throws IOException {
        updateIndexSearcher();

        Term term = new Term(LuceneKeys.HANDLE_LABEL, value);
        Collection<GUID> retval = getGUIDsFromSearch(term);

        releaseIndexSearcher();
        return retval;

    }

    private static void updateIndexSearcher() throws IOException {
        searcherManager.maybeRefresh();
        indexSearcher = searcherManager.acquire();
    }

    private static void releaseIndexSearcher() throws IOException {
        searcherManager.release(indexSearcher);
        indexSearcher = null;
    }

    private Collection<GUID> getGUIDsFromSearch(Term term) throws IOException {
        Collection<GUID> retval = new HashSet<>();
        Query query = new TermQuery(term);
        TopDocs topDocs = indexSearcher.search(query, QUERY_RESULTS);
        ScoreDoc[] hits = topDocs.scoreDocs;
        for (int i = 0; i < hits.length; i++) {
            Document doc = indexSearcher.doc(hits[i].doc);
            String guid = doc.get(LuceneKeys.HANDLE_GUID);
            retval.add(new GUIDsha1(guid));
        }

        return retval;
    }

    private void indexAtomManifest(AtomManifest manifest) throws IOException {
        Document doc = new Document();

        GUID guid = manifest.getContentGUID();
        String type = manifest.getManifestType();
        doc.add(new StringField(LuceneKeys.HANDLE_GUID, guid.toString(), Field.Store.YES));
        doc.add(new StringField(LuceneKeys.HANDLE_TYPE, type, Field.Store.YES));

        indexWriter.addDocument(doc);
        indexWriter.commit();
    }

    private void indexCompoundManifest(CompoundManifest manifest) throws IOException {
        Document doc = new Document();
        GUID guid = manifest.getContentGUID();
        String type = manifest.getManifestType();
        doc.add(new StringField(LuceneKeys.HANDLE_GUID, guid.toString(), Field.Store.YES));
        doc.add(new StringField(LuceneKeys.HANDLE_TYPE, type, Field.Store.YES));

        Collection<Content> contents = manifest.getContents();
        for (Content content : contents) {
            if (!contentExists(content)) {
                indexContent(content);
            }
        }

        indexWriter.addDocument(doc);
        indexWriter.commit();
    }

    private void indexAssetManifest(AssetManifest manifest) throws IOException {
        Document doc = new Document();
        GUID version = manifest.getVersionGUID();
        String type = manifest.getManifestType();
        GUID invariant = manifest.getInvariantGUID();
        Collection<GUID> prevs = manifest.getPreviousManifests();
        Collection<GUID> metadata = manifest.getMetadata();
        Content content = manifest.getContent();

        doc.add(new StringField(LuceneKeys.HANDLE_VERSION, version.toString(), Field.Store.YES));
        doc.add(new StringField(LuceneKeys.HANDLE_TYPE, type, Field.Store.YES));
        doc.add(new StringField(LuceneKeys.HANDLE_GUID, invariant.toString(), Field.Store.YES));

        // TODO - index rest of asset

        indexWriter.addDocument(doc);
        indexWriter.commit();
    }

    private void indexContent(Content content) throws IOException {
        Document contentDoc = new Document();
        contentDoc.add(new StringField(LuceneKeys.HANDLE_GUID, content.getGUID().toString(), Field.Store.YES));
        contentDoc.add(new StringField(LuceneKeys.HANDLE_LABEL, content.getLabel(), Field.Store.YES));
        indexWriter.addDocument(contentDoc);
    }

    private boolean guidExists(GUID guid, String key) throws IOException {
        updateIndexSearcher();

        // http://stackoverflow.com/questions/30810879/how-to-check-if-document-exists-in-lucene-index
        TopDocs results = indexSearcher.search(new TermQuery(new Term(key, guid.toString())), 1);

        releaseIndexSearcher();

        return results.totalHits > 0;
    }

    private boolean contentExists(Content content) throws IOException {
        updateIndexSearcher();

        GUID contentGUID = content.getGUID();
        String label = content.getLabel();

        BooleanQuery matchingQuery = new BooleanQuery();
        matchingQuery.add(new TermQuery(new Term(LuceneKeys.HANDLE_GUID, contentGUID.toString())), BooleanClause.Occur.MUST);
        matchingQuery.add(new TermQuery(new Term(LuceneKeys.HANDLE_LABEL, label)), BooleanClause.Occur.MUST);
        TopDocs results = indexSearcher.search(matchingQuery, 1);

        releaseIndexSearcher();

        return results.totalHits > 0;
    }
}
