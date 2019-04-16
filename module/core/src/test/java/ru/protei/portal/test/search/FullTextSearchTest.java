package ru.protei.portal.test.search;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.RAMDirectory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class FullTextSearchTest {
    public static final String[] TEST_PDF_FILE_PATHS = {
            "scoring.pdf"
    };

    public static final String QUERY = "query";
    public static final String BAD_QUERY = "gshingvisoiwutinorvwgurhgniwu";

    @Test
    public void testReadPdfDocument() {
        try {
            String content = getPdfContent(TEST_PDF_FILE_PATHS[0]);
            Assert.assertNotNull(content);
            Assert.assertTrue(!content.isEmpty());
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testIndexPdfDocument() {
        try {
            index(createIndexWriter(), TEST_PDF_FILE_PATHS);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testFullTextSearch() {
        try {
            IndexWriter writer = createIndexWriter();
            index(writer, TEST_PDF_FILE_PATHS);
            Query query = new QueryParser("content", new StandardAnalyzer()).parse(QUERY);
            IndexReader reader = DirectoryReader.open(writer.getDirectory());
            IndexSearcher searcher = new IndexSearcher(reader);
            Analyzer analyzer = new StandardAnalyzer();
            Long totalHits = searcher.search(query, 1).totalHits;
            Assert.assertTrue(totalHits > 0);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testFullTextSearchBadQuery() {
        try {
            IndexWriter writer = createIndexWriter();
            index(writer, TEST_PDF_FILE_PATHS);
            Query query = new QueryParser("content", new StandardAnalyzer()).parse(BAD_QUERY);
            IndexReader reader = DirectoryReader.open(writer.getDirectory());
            IndexSearcher searcher = new IndexSearcher(reader);
            Analyzer analyzer = new StandardAnalyzer();
            Long totalHits = searcher.search(query, 1).totalHits;
            Assert.assertTrue(totalHits == 0);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    public void index(IndexWriter writer, String... paths) throws IOException {
        for (String path : paths) {
            String content = getPdfContent(path);
            Document document = new Document();
            document.add(new StringField("name", path, Field.Store.YES));
            document.add(new TextField("content", content, Field.Store.YES));
            writer.addDocument(document);
        }
        writer.close();
    }

    public static IndexWriter createIndexWriter() throws IOException {
        return new IndexWriter(
                new RAMDirectory(),
                new IndexWriterConfig(new StandardAnalyzer())
        );
    }

    public String getPdfContent(String path) throws IOException {
        File pdfFile = new File(getClass().getClassLoader().getResource(path).getFile());
        System.out.println(new File(".").getAbsolutePath());
        PDDocument doc = PDDocument.load(pdfFile);
        String content = new PDFTextStripper().getText(doc);
        doc.close();
        return content;
    }
}
