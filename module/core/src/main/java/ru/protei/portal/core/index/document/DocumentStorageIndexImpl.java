package ru.protei.portal.core.index.document;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.index.LuceneIndex;
import ru.protei.portal.core.model.helper.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DocumentStorageIndexImpl implements DocumentStorageIndex {

    private static final Logger log = LoggerFactory.getLogger(DocumentStorageIndexImpl.class);

    private static final String ID_FIELD_NAME = "id";
    private static final String PROJECT_ID_FIELD_NAME = "project_id";
    private static final String CONTENT_FIELD_NAME = "content";

    private LuceneIndex index;
    private String indexPath;

    @Autowired
    PortalConfig config;

    @PostConstruct
    public void init() {
        try {
            getIndex();
        } catch (IOException e) {
            log.error("init(): Failed to init index", e);
        }
    }

    @PreDestroy
    public void destroy() {
        try {
            if (index != null && !index.isClosed()) {
                index.close();
            }
        } catch (IOException e) {
            log.error("destroy(): Failed to close index", e);
        } finally {
            index = null;
            indexPath = null;
        }
    }

    @Override
    public void removeDocument(long documentId) throws IOException {
        getIndex().deleteDocuments(new Term(ID_FIELD_NAME, String.valueOf(documentId)));
    }

    @Override
    public void addDocument(String body, Long documentId, Long projectId) throws IOException {
        getIndex().addDocument(makeFields(documentId, projectId, body));
    }

    @Override
    public void addPdfDocument(byte[] fileData, Long documentId, Long projectId) throws IOException {
        addDocument(LuceneIndex.convertPdfDocumentToString(fileData), documentId, projectId);
    }

    @Override
    public void updateDocument(String body, Long documentId, Long projectId) throws IOException {
        getIndex().updateDocument(
                new Term(ID_FIELD_NAME, String.valueOf(documentId)),
                makeFields(documentId, projectId, body)
        );
    }

    @Override
    public void updatePdfDocument(byte[] fileData, Long documentId, Long projectId) throws IOException {
        updateDocument(LuceneIndex.convertPdfDocumentToString(fileData), documentId, projectId);
    }

    @Override
    public List<Long> getDocumentsByQuery(String contentQuery, int maxHits) throws IOException {
        if (maxHits <= 0) {
            maxHits = Integer.MAX_VALUE;
        }
        List<String> result = getIndex().searchByField(CONTENT_FIELD_NAME, contentQuery, ID_FIELD_NAME, maxHits);
        return CollectionUtils.stream(result)
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isIndexExists() throws IOException {
        return getIndex().isExists();
    }

    private LuceneIndex getIndex() throws IOException {
        return getIndex(getPathFromConfig());
    }

    private LuceneIndex getIndex(String path) throws IOException {
        if (index != null && !index.isClosed() && Objects.equals(indexPath, path)) {
            return index;
        }
        if (index != null) {
            if (!index.isClosed()) {
                index.close();
            }
            index = null;
        }
        index = new LuceneIndex(path);
        indexPath = path;
        return index;
    }

    private String getPathFromConfig() {
        return config.data().lucene().getIndexPath();
    }

    private IndexableField[] makeFields(Long documentId, Long projectId, String content) {
        IndexableField[] fields = new IndexableField[3];
        fields[0] = new StringField(ID_FIELD_NAME, Long.toString(documentId), Field.Store.YES);
        fields[1] = new StringField(PROJECT_ID_FIELD_NAME, Long.toString(projectId), Field.Store.YES);
        fields[2] = new TextField(CONTENT_FIELD_NAME, content, Field.Store.NO);
        return fields;
    }
}
