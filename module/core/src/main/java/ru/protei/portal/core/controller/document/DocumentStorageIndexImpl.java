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
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
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

public class DocumentStorageIndexImpl implements DocumentStorageIndex {

    private static final Logger log = LoggerFactory.getLogger(DocumentStorageIndexImpl.class);

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
        String indexPath = config.data().lucene().getIndexPath();
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
            if (indexWriter != null)
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
        indexWriter.addDocument(makeDocument(body, documentId, projectId));
        indexWriter.commit();
    }

    @Override
    public void addPdfDocument(byte[] fileData, Long documentId, Long projectId) throws IOException {
        addDocument(convertPdfDocumentToString(fileData), documentId, projectId);
    }

    @Override
    public void updateDocument(String body, Long documentId, Long projectId) throws IOException {
        indexWriter.updateDocument(
                new Term(ID_FIELD_NAME, String.valueOf(documentId)),
                makeDocument(body, documentId, projectId)
        );
        indexWriter.commit();
    }

    @Override
    public void updatePdfDocument(byte[] fileData, Long documentId, Long projectId) throws IOException {
        updateDocument(convertPdfDocumentToString(fileData), documentId, projectId);
    }

    @Override
    public List<Long> getDocumentsByQuery(String contentQuery, int maxHits) throws IOException {
        Query query = new PhraseQuery(CONTENT_FIELD_NAME, contentQuery);
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

    private String convertPdfDocumentToString(byte[] pdfDocument) throws IOException {
        try (PDDocument document = PDDocument.load(pdfDocument)) {
            return new PDFTextStripper().getText(document);
        }
    }

    private Document makeDocument(String body, Long documentId, Long projectId) {
        Document document = new Document();
        document.add(new StringField(ID_FIELD_NAME, Long.toString(documentId), Field.Store.YES));
        document.add(new StringField(PROJECT_ID_FIELD_NAME, Long.toString(projectId), Field.Store.YES));
        document.add(new TextField(CONTENT_FIELD_NAME, body, Field.Store.NO));
        return document;
    }
}
