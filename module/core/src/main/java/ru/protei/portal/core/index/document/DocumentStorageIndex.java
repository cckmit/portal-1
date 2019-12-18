package ru.protei.portal.core.index.document;

import java.io.IOException;
import java.util.List;

public interface DocumentStorageIndex {
    void addDocument(String body, Long documentId, Long projectId) throws IOException;

    void addPdfDocument(byte[] fileData, Long documentId, Long projectId) throws IOException;

    List<Long> getDocumentsByQuery(String contentQuery, int maxHits) throws IOException;

    void removeDocument(long documentId) throws IOException;

    void updateDocument(String body, Long documentId, Long projectId) throws IOException;

    void updatePdfDocument(byte[] fileData, Long documentId, Long projectId) throws IOException;

    boolean isIndexExists() throws IOException;
}
