package ru.protei.portal.core.controller.document;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
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

public class DocumentIndexImpl implements DocumentIndex {

    private static final Logger log = LoggerFactory.getLogger(DocumentIndexImpl.class);

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
            indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            index = NIOFSDirectory.open(Paths.get(indexPath));
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
    public void removeDocument(long documentId) throws IOException {
        indexWriter.deleteDocuments(new Term(ID_FIELD_NAME, String.valueOf(documentId)));
    }

    @Override
    public void addDocument(String body, Long documentId, Long projectId) throws IOException {
        Document document = new Document();
        document.add(new StringField(ID_FIELD_NAME, Long.toString(documentId), Field.Store.YES));
        document.add(new StringField(PROJECT_ID_FIELD_NAME, Long.toString(projectId), Field.Store.YES));
        document.add(new TextField(CONTENT_FIELD_NAME, body, Field.Store.NO));
        indexWriter.addDocument(document);
        indexWriter.commit();
    }

    @Override
    public List<Long> getDocumentsByQuery(List<Long> searchIds, String contentQuery, int maxHits) throws IOException {
        Query query = getQuery(searchIds, contentQuery);
        DirectoryReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);

        List<Long> idList = new LinkedList<>();

         if (maxHits <= 0) {
             maxHits = Integer.MAX_VALUE;
         }
        TopDocs topDocs = searcher.search(query, maxHits);

        Arrays.stream(topDocs.scoreDocs)
                .forEach(scoreDoc -> {
                    int id = scoreDoc.doc;
                    try {
                        Long docId = Long.parseLong(searcher.doc(id).get(ID_FIELD_NAME));
                        idList.add(docId);
                    } catch (IOException e) {
                        log.error("Failed to find doc #" + id, e);
                        throw new RuntimeException(e);
                    } catch (NumberFormatException e) {
                        log.error("Failed to parse id field in doc #" + id, e);
                        throw new RuntimeException(e);
                    }
                });
        reader.close();
        return idList;
    }

    @Override
    public int countDocumentsByQuery(List<Long> searchIds, String contentQuery) throws IOException {
        Query query = getQuery(searchIds, contentQuery);
        DirectoryReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);
        int result =  searcher.count(query);
        reader.close();
        return result;
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
