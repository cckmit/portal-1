package ru.protei.portal.core.index;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LuceneIndex implements Closeable {

    private final String path;
    private final Analyzer analyzer;
    private final IndexWriter indexWriter;
    private boolean isClosed = false;

    public LuceneIndex(String path) throws IOException {
        this(path, new RussianAnalyzer());
    }

    public LuceneIndex(String path, Analyzer analyzer) throws IOException {
        this.path = path;
        this.analyzer = analyzer;
        this.indexWriter = new IndexWriter(
            openDirectory(),
            makeIndexWriterConfig(analyzer)
        );
    }

    @Override
    public void close() throws IOException {
        if (isClosed) return;
        isClosed = true;
        if (indexWriter != null) indexWriter.close();
        if (analyzer != null) analyzer.close();
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void addDocument(IndexableField...fields) throws IOException {
        if (isClosed) throw makeClosedException();
        Document document = makeDocument(fields);
        indexWriter.addDocument(document);
        indexWriter.commit();
    }

    public void updateDocument(Term term, IndexableField...fields) throws IOException {
        if (isClosed) throw makeClosedException();
        Document document = makeDocument(fields);
        indexWriter.updateDocument(term, document);
        indexWriter.commit();
    }

    public void deleteDocuments(Term...terms) throws IOException {
        if (isClosed) throw makeClosedException();
        indexWriter.deleteDocuments(terms);
    }

    public List<String> searchByField(String field2search, String search, String field2get, int maxHits) throws IOException {
        if (isClosed) throw makeClosedException();
        try (
            Directory directory = openDirectory();
            IndexReader reader = DirectoryReader.open(directory)
        ) {
            IndexSearcher searcher = new IndexSearcher(reader);
            QueryParser parser = new QueryParser(field2search, analyzer);
            Query query = parser.parse(search);
            TopDocs topDocs = searcher.search(query, maxHits);
            return Arrays.stream(topDocs.scoreDocs)
                .map(scoreDoc -> scoreDoc.doc)
                .map(id -> {
                    try {
                        return searcher.doc(id).get(field2get);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
        } catch (ParseException e) {
            throw new IOException(e);
        }
    }

    public boolean isExists() throws IOException {
        try (Directory directory = openDirectory()) {
            return DirectoryReader.indexExists(directory);
        }
    }

    public Directory openDirectory() throws IOException {
        return NIOFSDirectory.open(Paths.get(path));
    }

    public static String convertPdfDocumentToString(byte[] pdfDocument) throws IOException {
        try (PDDocument document = PDDocument.load(pdfDocument)) {
            return new PDFTextStripper().getText(document);
        }
    }

    private Document makeDocument(IndexableField...fields) {
        Document document = new Document();
        for (IndexableField field : fields) {
            document.add(field);
        }
        return document;
    }

    private IndexWriterConfig makeIndexWriterConfig(Analyzer analyzer) {
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        return config;
    }

    private RuntimeException makeClosedException() {
        return new IllegalStateException("LuceneIndex is closed");
    }
}
