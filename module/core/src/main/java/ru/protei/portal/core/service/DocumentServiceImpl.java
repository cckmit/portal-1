package ru.protei.portal.core.service;

import org.apache.commons.fileupload.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.tmatesoft.svn.core.SVNException;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.controller.document.DocumentStorageIndex;
import ru.protei.portal.core.model.dao.DocumentDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Document;
import ru.protei.portal.core.model.query.DocumentQuery;
import ru.protei.winter.core.utils.services.lock.LockService;
import ru.protei.winter.core.utils.services.lock.LockStrategy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.mysql.jdbc.StringUtils.isEmptyOrWhitespaceOnly;

public class DocumentServiceImpl implements DocumentService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);

    @Autowired
    DocumentDAO documentDAO;

    @Autowired
    DocumentStorageIndex documentStorageIndex;

    @Autowired
    LockService lockService;

    @Autowired
    DocumentSvnService documentSvnService;


    @Override
    public CoreResponse<Integer> count(AuthToken token, DocumentQuery query) {
        try {
            checkApplyFullTextSearchFilter(query);
        } catch (IOException e) {
            return new CoreResponse<Integer>().error(En_ResultStatus.INTERNAL_ERROR);
        }
      
        return new CoreResponse<Integer>().success(documentDAO.countByQuery(query));
    }

    @Override
    public CoreResponse<List<Document>> documentList(AuthToken token, DocumentQuery query) {
        try {
            checkApplyFullTextSearchFilter(query);
        } catch (IOException e) {
            return new CoreResponse<List<Document>>().error(En_ResultStatus.INTERNAL_ERROR);
        }

        List<Document> list = documentDAO.getListByQuery(query);
        if (list == null) {
            return new CoreResponse<List<Document>>().error(En_ResultStatus.GET_DATA_ERROR);
        }

        list.forEach( document -> {
                    // RESET PRIVACY INFO
                    if (document.getContractor() != null) {
                        document.getContractor().resetPrivacyInfo();
                    }
                    if (document.getRegistrar() != null) {
                        document.getRegistrar().resetPrivacyInfo();
                    }
                }
        );


        return new CoreResponse<List<Document>>().success(list);
    }

    private void checkApplyFullTextSearchFilter(DocumentQuery query)  throws IOException {
        if (!isEmptyOrWhitespaceOnly(query.getInTextQuery())) {
            query.setOnlyIds(documentStorageIndex.getDocumentsByQuery(query.getInTextQuery(), query.limit));
        }
    }

    @Override
    public CoreResponse<Document> getDocument(AuthToken token, Long id) {
        Document document = documentDAO.get(id);
        if (document == null)
            return new CoreResponse<Document>().error(En_ResultStatus.NOT_FOUND);

        // RESET PRIVACY INFO
        if (document.getContractor() != null) {
            document.getContractor().resetPrivacyInfo();
        }
        if (document.getRegistrar() != null) {
            document.getRegistrar().resetPrivacyInfo();
        }

        return new CoreResponse<Document>().success(document);
    }

    @Override
    @Transactional
    public CoreResponse<Document> updateDocument(AuthToken token, Document document) {
        if (document == null || !document.isValid()) {
            return new CoreResponse<Document>().error(En_ResultStatus.INCORRECT_PARAMS);
        }
        if (!isDocumentInventoryNumberUnique(document)) {
            return new CoreResponse<Document>().error(En_ResultStatus.INVENTORY_NUMBER_ALREADY_EXIST);
        }

        if (!documentDAO.saveOrUpdate(document)) {
            return new CoreResponse<Document>().error(En_ResultStatus.INTERNAL_ERROR);
        }
        return new CoreResponse<Document>().success(document);
    }

    @Override
    public CoreResponse<Document> updateDocumentAndContent(AuthToken token, Document document, FileItem fileItem) {

        if (document == null || !document.isValid() || document.getId() == null || fileItem == null) {
            return new CoreResponse<Document>().error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (document.getApproved()) {
            return new CoreResponse<Document>().error(En_ResultStatus.NOT_AVAILABLE);
        }

        final byte[] fileData = fileItem.get();
        final InputStream fileInputStream;
        try {
            fileInputStream = fileItem.getInputStream();
        } catch (IOException e) {
            logger.error("updateDocumentAndContent(" + document.getId() + "): Failed to get input stream from file item", e);
            return new CoreResponse<Document>().error(En_ResultStatus.INTERNAL_ERROR);
        }

        return lockService.doWithLock(DocumentStorageIndex.class, "", LockStrategy.TRANSACTION, TimeUnit.SECONDS, 5, () -> {
            if (!isDocumentInventoryNumberUnique(document)) {
                return new CoreResponse<Document>().error(En_ResultStatus.INVENTORY_NUMBER_ALREADY_EXIST);
            }

            final Long projectId = document.getProjectId(), documentId = document.getId();

            final Document oldDocument = documentDAO.get(document.getId());
            if (oldDocument == null) {
                return new CoreResponse<Document>().error(En_ResultStatus.INCORRECT_PARAMS);
            }
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            documentSvnService.getDocument(projectId, documentId, out);
            final byte[] oldFileData = out.toByteArray();
            out.close();

            try {
                documentDAO.merge(document);
                documentStorageIndex.updatePdfDocument(fileData, projectId, documentId);
                documentSvnService.updateDocument(projectId, documentId, fileInputStream);
                return new CoreResponse<Document>().success(document);
            } catch (SVNException | IOException e) {
                logger.error("updateDocumentAndContent(" + document.getId() + "): Failed to update, rolling back", e);
                documentDAO.merge(oldDocument);
                try {
                    documentStorageIndex.updatePdfDocument(oldFileData, documentId, projectId);
                } catch (IOException e1) {
                    logger.error("updateDocumentAndContent(" + document.getId() + "): Failed to update, rolling back | failed to update document at the index", e1);
                }
            }
            return new CoreResponse<Document>().error(En_ResultStatus.INTERNAL_ERROR);
        });
    }

    @Override
    public CoreResponse<Document> createDocument(AuthToken token, Document document, FileItem fileItem) {

        if (document == null || !document.isValid()) {
            return new CoreResponse<Document>().error(En_ResultStatus.INCORRECT_PARAMS);
        }

        byte[] fileData = fileItem.get();

        InputStream fileInputStream;
        try {
            fileInputStream = fileItem.getInputStream();
        } catch (IOException e) {
            logger.error("createDocument(" + document.getId() + "): failed to get input stream from file item", e);
            return new CoreResponse<Document>().error(En_ResultStatus.INTERNAL_ERROR);
        }

        return lockService.doWithLock(DocumentStorageIndex.class, "", LockStrategy.TRANSACTION, TimeUnit.SECONDS, 5, () -> {
            if (!isDocumentInventoryNumberUnique(document)) {
                return new CoreResponse<Document>().error(En_ResultStatus.INVENTORY_NUMBER_ALREADY_EXIST);
            }

            if (!documentDAO.saveOrUpdate(document)) {
                return new CoreResponse<Document>().error(En_ResultStatus.INTERNAL_ERROR);
            }

            Long documentId = document.getId(), projectId = document.getProjectId();

            try {
                documentStorageIndex.addPdfDocument(fileData, projectId, documentId);
            } catch (IOException e) {
                logger.error("createDocument(" + document.getId() + "): failed to add file to the index", e);
                documentDAO.removeByKey(documentId);
                return new CoreResponse<Document>().error(En_ResultStatus.INTERNAL_ERROR);
            }
            try {
                documentSvnService.saveDocument(projectId, documentId, fileInputStream);
                return new CoreResponse<Document>().success(document);
            } catch (SVNException e) {
                logger.error("createDocument(" + document.getId() + "): failed to save in the repository", e);
                try {
                    documentStorageIndex.removeDocument(documentId);
                } catch (IOException e1) {
                    logger.error("createDocument(" + document.getId() + "): failed to delete document from the index");
                }
                documentDAO.removeByKey(documentId);
                return new CoreResponse<Document>().error(En_ResultStatus.INTERNAL_ERROR);
            }
        });
    }

    private boolean isDocumentInventoryNumberUnique(Document document) {
        return document.getInventoryNumber() == null || documentDAO.checkInventoryNumberNotExists(document.getInventoryNumber());
    }
}
