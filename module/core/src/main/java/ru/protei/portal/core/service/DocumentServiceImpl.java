package ru.protei.portal.core.service;

import org.apache.commons.fileupload.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;
import org.tmatesoft.svn.core.SVNException;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.controller.document.DocumentStorageIndex;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dao.DocumentDAO;
import ru.protei.portal.core.model.dict.En_CustomerType;
import ru.protei.portal.core.model.dict.En_DocumentState;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Document;
import ru.protei.portal.core.model.query.DocumentQuery;
import ru.protei.portal.core.model.struct.Project;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.core.utils.services.lock.LockService;
import ru.protei.winter.core.utils.services.lock.LockStrategy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.mysql.jdbc.StringUtils.isEmptyOrWhitespaceOnly;
import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

public class DocumentServiceImpl implements DocumentService {

    private static final Logger log = LoggerFactory.getLogger(DocumentServiceImpl.class);

    @Autowired
    DocumentDAO documentDAO;

    @Autowired
    CaseObjectDAO caseObjectDAO;

    @Autowired
    DocumentStorageIndex documentStorageIndex;

    @Autowired
    LockService lockService;

    @Autowired
    DocumentSvnService documentSvnService;

    @Override
    public Result<SearchResult<Document>> getDocuments( AuthToken token, Long equipmentId) {
        DocumentQuery query = new DocumentQuery();
        query.setEquipmentIds(Collections.singletonList(equipmentId));
        return getDocuments(token, query);
    }

    @Override
    public Result<SearchResult<Document>> getDocuments( AuthToken token, DocumentQuery query) {

        try {
            checkApplyFullTextSearchFilter(query);
        } catch (IOException e) {
            return error(En_ResultStatus.INTERNAL_ERROR);
        }

        SearchResult<Document> sr = documentDAO.getSearchResult(query);

        sr.getResults().forEach(this::resetDocumentPrivacyInfo);

        return ok(sr);
    }

    @Override
    public Result<List<Document>> documentList( AuthToken token, Long equipmentId) {
        DocumentQuery query = new DocumentQuery();
        query.setEquipmentIds(Collections.singletonList(equipmentId));

        try {
            checkApplyFullTextSearchFilter(query);
        } catch (IOException e) {
            return error(En_ResultStatus.INTERNAL_ERROR);
        }

        List<Document> list = documentDAO.getListByQuery(query);
        if (list == null) {
            return error(En_ResultStatus.GET_DATA_ERROR);
        }

        list.forEach(this::resetDocumentPrivacyInfo);

        return ok(list);
    }

    @Override
    public Result<Document> getDocument( AuthToken token, Long id) {

        Document document = documentDAO.get(id);

        if (document == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        resetDocumentPrivacyInfo(document);

        return ok(document);
    }

    @Override
    public Result<Document> createDocument( AuthToken token, Document document, FileItem fileItem) {

        if (document == null || !isValidDocument(document)) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        byte[] fileData = fileItem.get();

        InputStream fileInputStream;
        try {
            fileInputStream = fileItem.getInputStream();
        } catch (Exception e) {
            log.error("createDocument(" + document.getId() + "): failed to get input stream from file item", e);
            return error(En_ResultStatus.INTERNAL_ERROR);
        }

        return lockService.doWithLock(DocumentStorageIndex.class, "", LockStrategy.TRANSACTION, TimeUnit.SECONDS, 5, () -> {

            En_ResultStatus validationStatus = checkDocumentDesignationValid(null, document);
            if (validationStatus != En_ResultStatus.OK) {
                return error(validationStatus);
            }

            document.setState(En_DocumentState.ACTIVE);

            try {
                if (!documentDAO.saveOrUpdate(document)) {
                    log.error("createDocument(): failed to save/update document to the db");
                    return error(En_ResultStatus.INTERNAL_ERROR);
                }
            } catch (DuplicateKeyException ex) {
                return error(En_ResultStatus.ALREADY_EXIST);
            } catch (Exception e) {
                log.error("createDocument(): failed to save/update document to the db", e);
                return error(En_ResultStatus.INTERNAL_ERROR);
            }

            Long documentId = document.getId();
            Long projectId = document.getProjectId();

            try {
                documentStorageIndex.addPdfDocument(fileData, projectId, documentId);
            } catch (Exception e) {
                log.error("createDocument(" + documentId + "): failed to add file to the index", e);
                if (!documentDAO.removeByKey(documentId)) {
                    log.error("createDocument(" + documentId + "): failed to rollback document from the db");
                }
                return error(En_ResultStatus.INTERNAL_ERROR);
            }

            try {
                documentSvnService.saveDocument(projectId, documentId, fileInputStream);
            } catch (Exception e) {
                log.error("createDocument(" + documentId + "): failed to save file to the svn", e);
                try {
                    documentStorageIndex.removeDocument(documentId);
                } catch (IOException e1) {
                    log.error("createDocument(" + documentId + "): failed to rollback document from the index", e1);
                }
                if (!documentDAO.removeByKey(documentId)) {
                    log.error("createDocument(" + documentId + "): failed to rollback document from the db");
                }
                return error(En_ResultStatus.INTERNAL_ERROR);
            }

            return ok(document);
        });
    }

    @Override
    @Transactional
    public Result updateState( AuthToken token, Long documentId, En_DocumentState state) {
        if (documentId == null ) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        Document document = documentDAO.get(documentId);

        if (document == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        document.setState(state);

        if (documentDAO.updateState(document)) {
            return ok();
        } else {
            return error(En_ResultStatus.INTERNAL_ERROR);
        }
    }

    @Override
    public Result<Document> updateDocument( AuthToken token, Document document) {

        if (document == null || !isValidDocument(document)) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        Document oldDocument = documentDAO.get(document.getId());

        if (oldDocument == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        En_ResultStatus validationStatus = checkDocumentDesignationValid(oldDocument, document);
        if (validationStatus != En_ResultStatus.OK) {
            return error(validationStatus);
        }

        try {
            if (!documentDAO.saveOrUpdate(document)) {
                return error(En_ResultStatus.INTERNAL_ERROR);
            }
        } catch (DuplicateKeyException ex) {
            return error(En_ResultStatus.ALREADY_EXIST);
        }

        return ok(document);
    }
    @Override
    public Result<Document> updateDocumentAndContent( AuthToken token, Document document, FileItem fileItem) {

        if (document == null || !isValidDocument(document) || document.getId() == null || fileItem == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (document.getApproved()) {
            return error(En_ResultStatus.NOT_AVAILABLE);
        }

        final byte[] fileData = fileItem.get();
        final InputStream fileInputStream;
        try {
            fileInputStream = fileItem.getInputStream();
        } catch (IOException e) {
            log.error("updateDocumentAndContent(" + document.getId() + "): failed to get input stream from file item", e);
            return error(En_ResultStatus.INTERNAL_ERROR);
        }

        return lockService.doWithLock(DocumentStorageIndex.class, "", LockStrategy.TRANSACTION, TimeUnit.SECONDS, 5, () -> {

            final Long projectId = document.getProjectId(), documentId = document.getId();

            final Document oldDocument = documentDAO.get(document.getId());
            if (oldDocument == null) {
                return error(En_ResultStatus.INCORRECT_PARAMS);
            }
            En_ResultStatus validationStatus = checkDocumentDesignationValid(oldDocument, document);
            if (validationStatus != En_ResultStatus.OK) {
                return error(validationStatus);
            }
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            documentSvnService.getDocument(projectId, documentId, out);
            final byte[] oldFileData = out.toByteArray();
            out.close();

            try {
                documentDAO.merge(document);
            } catch (DuplicateKeyException e) {
                return error(En_ResultStatus.ALREADY_EXIST);
            } catch (Exception e) {
                log.error("updateDocumentAndContent(" + document.getId() + "): failed to merge document to the db", e);
                return error(En_ResultStatus.INTERNAL_ERROR);
            }

            try {
                documentStorageIndex.updatePdfDocument(fileData, projectId, documentId);
            } catch (Exception e) {
                log.error("updateDocumentAndContent(" + document.getId() + "): failed to update file to the index", e);
                if (!documentDAO.merge(oldDocument)) {
                    log.error("updateDocumentAndContent(" + document.getId() + "): failed to rollback document from the db");
                }
                return error(En_ResultStatus.INTERNAL_ERROR);
            }

            try {
                documentSvnService.updateDocument(projectId, documentId, fileInputStream);
            } catch (Exception e) {
                log.error("updateDocumentAndContent(" + document.getId() + "): failed to update file to the svn", e);
                if (!documentDAO.merge(oldDocument)) {
                    log.error("updateDocumentAndContent(" + document.getId() + "): failed to rollback document from the db");
                }
                try {
                    documentStorageIndex.updatePdfDocument(oldFileData, documentId, projectId);
                } catch (IOException e1) {
                    log.error("updateDocumentAndContent(" + document.getId() + "): failed to rollback document from the index", e1);
                }
                return error(En_ResultStatus.INTERNAL_ERROR);
            }

            return ok(document);
        });
    }

    @Override
    public Result<Document> removeDocument( AuthToken token, Document document) {

        if (document == null || document.getId() == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (document.getApproved()) {
            return error(En_ResultStatus.NOT_AVAILABLE);
        }

        return lockService.doWithLock(DocumentStorageIndex.class, "", LockStrategy.TRANSACTION, TimeUnit.SECONDS, 5, () -> {
            Long documentId = document.getId();
            Long projectId = document.getProjectId();
            documentDAO.removeByKey(documentId);
            documentSvnService.removeDocument(projectId, documentId);
            documentStorageIndex.removeDocument(documentId);
            return ok(document);
        });
    }

    @Override
    public Result<SearchResult<Document>> getProjectDocuments( AuthToken token, Long projectId) {
        DocumentQuery query = new DocumentQuery();
        query.setProjectIds(new LinkedList<>(Collections.singletonList(projectId)));
        return getDocuments(token, query);
    }

    private void checkApplyFullTextSearchFilter(DocumentQuery query) throws IOException {
        if (!isEmptyOrWhitespaceOnly(query.getInTextQuery())) {
            query.setOnlyIds(documentStorageIndex.getDocumentsByQuery(query.getInTextQuery(), query.limit));
        }
    }

    private void resetDocumentPrivacyInfo(Document document) {
        // RESET PRIVACY INFO
        if (document.getContractor() != null) {
            document.getContractor().resetPrivacyInfo();
        }
        if (document.getRegistrar() != null) {
            document.getRegistrar().resetPrivacyInfo();
        }
    }

    private En_ResultStatus checkDocumentDesignationValid(Document oldDocument, Document document) {

        if (oldDocument != null && document.getApproved() && (
                isValueSetTwice(oldDocument.getInventoryNumber(), document.getInventoryNumber()) ||
                        isValueSetTwice(oldDocument.getDecimalNumber(), document.getDecimalNumber()))) {
            return En_ResultStatus.INCORRECT_PARAMS;
        }

        if ((oldDocument == null || !Objects.equals(oldDocument.getInventoryNumber(), document.getInventoryNumber())) &&
                isDocumentInventoryNumberExists(document)) {
            return En_ResultStatus.INVENTORY_NUMBER_ALREADY_EXIST;
        }

        if ((oldDocument == null || !Objects.equals(oldDocument.getDecimalNumber(), document.getDecimalNumber())) &&
                isDocumentDecimalNumberExists(document)) {
            return En_ResultStatus.DECIMAL_NUMBER_ALREADY_EXIST;
        }

        return En_ResultStatus.OK;
    }

    private boolean isDocumentInventoryNumberExists(Document document) {
        return document.getInventoryNumber() != null && documentDAO.checkInventoryNumberExists(document.getInventoryNumber());
    }

    private boolean isDocumentDecimalNumberExists(Document document) {
        return document.getDecimalNumber() != null && documentDAO.checkDecimalNumberExists(document.getDecimalNumber());
    }

    private <T> boolean isValueSetTwice(T oldObj, T newObj) {
        return oldObj != null && !oldObj.equals(newObj);
    }

    private boolean isValidDocument(Document document){
        return document.isValid() && isValidInventoryNumberForMinistryOfDefence(document);
    }
    private boolean isValidInventoryNumberForMinistryOfDefence(Document document) {
        Project project = Project.fromCaseObject(caseObjectDAO.get(document.getProjectId()));
        if (project == null) return false;
        else {
            if (project.getCustomerType() == En_CustomerType.MINISTRY_OF_DEFENCE) {
                return document.getInventoryNumber() != null && (document.getInventoryNumber() > 0);
            }
        }
        return true;
    }
}
