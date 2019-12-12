package ru.protei.portal.test.index;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.protei.portal.core.index.LuceneIndex;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class LuceneIndexTest {

    private static final Logger log = LoggerFactory.getLogger(LuceneIndexTest.class);
    private static final String path = "test-lucene-index";
    private static final String documentPath = "scoring.pdf";
    private static final String FIELD_ID = "id";
    private static final String FIELD_CONTENT = "content";
    private static final String QUERY = "Scoring";
    private static final String BAD_QUERY = "gshingvisoiwutinorvwgurhgniwu";

    private LuceneIndex index;

    @Before
    public void setup() {
        try {
            index = new LuceneIndex(path);
            log.info("Index created");
        } catch (IOException e) {
            log.error("Failed to setup", e);
            Assert.fail(e.getMessage());
        }
    }

    @After
    public void teardown() {
        try {
            index.close();
            log.info("Index closed");
        } catch (IOException e) {
            log.error("Failed to teardown", e);
            Assert.fail(e.getMessage());
        } finally {
            deleteFolder(path);
        }
    }

    @Test
    public void testIndexCreateClose() {
        log.info("NOOP");
    }

    @Test
    public void testAddDocument() {
        try {
            String content = getPdfAsString(documentPath);
            index.addDocument(makeFields(1L, content));
            log.info("Document added");
        } catch (IOException e) {
            log.error("Test failed", e);
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testAddAndUpdateDocument() {
        try {

            String content = getPdfAsString(documentPath);

            index.addDocument(makeFields(1L, content));
            log.info("Document added");

            index.updateDocument(
                    new Term(FIELD_ID, String.valueOf(1L)),
                    makeFields(1L, content)
            );
            log.info("Document updated");

        } catch (IOException e) {
            log.error("Test failed", e);
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testAddAndRemoveDocument() {
        try {

            String content = getPdfAsString(documentPath);
            index.addDocument(makeFields(1L, content));
            log.info("Document added");

            index.deleteDocuments(new Term(FIELD_ID, String.valueOf(1L)));
            log.info("Document removed");

        } catch (IOException e) {
            log.error("Test failed", e);
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testAddAndFindDocument() {
        try {

            String content = getPdfAsString(documentPath);
            index.addDocument(makeFields(1L, content));
            log.info("Document added");

            List<String> result = index.searchByField(FIELD_CONTENT, QUERY, FIELD_ID, 100);

            Assert.assertNotNull(result);
            Assert.assertEquals("Document not found", 1, result.size());
            long docId = Long.parseLong(result.get(0));
            Assert.assertEquals("Found wrong document", 1L, docId);

            log.info("Found document with id {}", docId);

        } catch (IOException e) {
            log.error("Test failed", e);
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testAddAndNotFindDocument() {
        try {

            String content = getPdfAsString(documentPath);
            index.addDocument(makeFields(1L, content));
            log.info("Document added");

            List<String> result = index.searchByField(FIELD_CONTENT, BAD_QUERY, FIELD_ID, 100);

            Assert.assertNotNull(result);
            Assert.assertEquals("Document should not be found", 0, result.size());

            log.info("Document not found");

        } catch (IOException e) {
            log.error("Test failed", e);
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testIndexExists() {
        try {

            String content = getPdfAsString(documentPath);
            index.addDocument(makeFields(1L, content));
            log.info("Document added");

            boolean exists = index.isExists();
            Assert.assertTrue("Index should exists", exists);
            log.info("Index exists");

        } catch (IOException e) {
            log.error("Test failed", e);
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testIndexNotExists() {
        try {
            boolean exists = index.isExists();
            Assert.assertFalse("Index should not exists", exists);
            log.info("Index not exists");
        } catch (IOException e) {
            log.error("Test failed", e);
            Assert.fail(e.getMessage());
        }
    }

    private IndexableField[] makeFields(Long id, String content) {
        IndexableField[] fields = new IndexableField[2];
        fields[0] = new StringField(FIELD_ID, Long.toString(id), Field.Store.YES);
        fields[1] = new TextField(FIELD_CONTENT, content, Field.Store.NO);
        return fields;
    }

    private String getPdfAsString(String path) throws IOException {
        String filePath = getResourceFilePath(path);
        byte[] data = FileUtils.readFileToByteArray(new File(filePath));
        return LuceneIndex.convertPdfDocumentToString(data);
    }

    private String getResourceFilePath(String path) {
        return getClass().getClassLoader().getResource(path).getPath();
    }

    private void deleteFolder(String path) {
        try {
            FileUtils.deleteDirectory(new File(path));
        } catch (IOException e) {
            log.error("Failed to delete folder", e);
        }
    }
}
