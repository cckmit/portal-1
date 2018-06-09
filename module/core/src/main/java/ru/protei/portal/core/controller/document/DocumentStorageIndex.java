package ru.protei.portal.core.controller.document;


import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface DocumentStorageIndex {
    void addDocument(String body, Long documentId, Long projectId) throws IOException;

    void addPdfDocument(InputStream stream, Long documentId, Long projectId) throws IOException;

    List<Long> getDocumentsByQuery(String contentQuery, int maxHits) throws IOException;

    void removeDocument(long documentId) throws IOException;
}
