package ru.protei.portal.core.controller.document;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.config.PortalConfig;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static ru.protei.portal.core.model.helper.HelperFunc.isEmpty;

public class DocumentStorageImpl implements DocumentStorage {

    private static final Logger log = LoggerFactory.getLogger(DocumentStorageImpl.class);

    private static final String ID_FIELD_NAME = "id";
    private static final String PROJECT_ID_FIELD_NAME = "project_id";
    private static final String CONTENT_FIELD_NAME = "content";

    @Autowired
    PortalConfig config;

    Directory index;
    Analyzer analyzer = new RussianAnalyzer();
    IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
    IndexWriter indexWriter;

    @PostConstruct
    public void init() {
        String indexPath = config.data().fullTextSearch().getIndexPath();
        try {
            index = new NIOFSDirectory(Paths.get(indexPath));
            indexWriter = new IndexWriter(index, indexWriterConfig);
        } catch (IOException e) {
            log.error("Failed to init index directory (path: " + indexPath + ")", e);
        }
    }

    @PreDestroy
    public void destroy() {
        try {
            indexWriter.close();
        } catch (IOException e) {
            log.error("Failed to close index writer", e);
        }
    }

    @Override
    public void addDocument(String body, Long documentId, Long projectId) throws IOException {
        Document document = new Document();
        document.add(new StringField(ID_FIELD_NAME, Long.toString(documentId), Field.Store.YES));
        document.add(new StringField(PROJECT_ID_FIELD_NAME, Long.toString(projectId), Field.Store.YES));
        document.add(new TextField(CONTENT_FIELD_NAME, body, Field.Store.NO));
        indexWriter.addDocument(document);
    }

    @Override
    public List<Long> getDocumentsByQuery(List<Long> searchIds, String contentQuery) throws IOException {
        Query query = getQuery(searchIds, contentQuery);
        IndexReader reader = DirectoryReader.open(indexWriter);
        IndexSearcher searcher = new IndexSearcher(reader);

        List<Long> idList = new LinkedList<>();
        TopDocs topDocs = searcher.search(query, 10);

        Arrays.stream(topDocs.scoreDocs)
                .forEach(scoreDoc -> {
                    int id = scoreDoc.doc;
                    try {
                        Long docId = Long.parseLong(searcher.doc(id).get(ID_FIELD_NAME));
                        idList.add(docId);
                    } catch (IOException e) {
                        log.error("Failed to find doc #" + id, e);
                    } catch (NumberFormatException e) {
                        log.error("Failed to parse id field in doc #" + id, e);
                    }
                });
        return idList;
    }

    @Override
    public int countDocumentsByQuery(List<Long> searchIds, String contentQuery) throws IOException {
        Query query = getQuery(searchIds, contentQuery);
        IndexReader reader = DirectoryReader.open(indexWriter);
        IndexSearcher searcher = new IndexSearcher(reader);
        return searcher.count(query);
    }


    private Query getQuery(List<Long> searchIds, String contentQuery) {
        if (searchIds == null || searchIds.isEmpty()) {
            return new PhraseQuery(CONTENT_FIELD_NAME, contentQuery);
        }
        BooleanQuery.Builder idQuery = new BooleanQuery.Builder();
        searchIds.forEach(id ->
                idQuery.add(new TermQuery(new Term(ID_FIELD_NAME, String.valueOf(id))), BooleanClause.Occur.SHOULD)
        );

        if (isEmpty(contentQuery)) {
            return idQuery.build();
        }

        return new BooleanQuery.Builder()
                .add(new PhraseQuery(CONTENT_FIELD_NAME, contentQuery), BooleanClause.Occur.MUST)
                .add(idQuery.build(), BooleanClause.Occur.MUST)
                .build();
    }
}
