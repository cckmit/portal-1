package ru.protei.portal.core.controller.document;


import java.io.IOException;
import java.util.List;

public interface DocumentIndex {
    void addDocument(String body, Long documentId, Long projectId) throws IOException;

    List<Long> getDocumentsByQuery(List<Long> searchIds, String contentQuery, int maxHits) throws IOException;

    int countDocumentsByQuery(List<Long> searchIds, String contentQuery) throws IOException;

    void removeDocument(long documentId) throws IOException;
}
