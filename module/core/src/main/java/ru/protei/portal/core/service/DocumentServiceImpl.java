package ru.protei.portal.core.service;

import org.apache.commons.fileupload.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.mysql.jdbc.StringUtils.isEmptyOrWhitespaceOnly;
import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

public class DocumentServiceImpl implements DocumentService {

    private static final Logger log = LoggerFactory.getLogger(DocumentServiceImpl.class);
    private static final Long DOCUMENT_ID_FOR_CREATE = -1L;
    private static final Long LOCK_TIMEOUT = 5L;
    private static final TimeUnit LOCK_TIMEOUT_TIME_UNIT = TimeUnit.SECONDS;

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
        } catch (Exception e) {
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
        } catch (Exception e) {
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

        if (document == null || !isValidDocument(document) || fileItem == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        En_ResultStatus validationStatus = checkDocumentDesignationValid(null, document);
        if (validationStatus != En_ResultStatus.OK) {
            return error(validationStatus);
        }

        InputStream fileInputStream = getInputStream(fileItem);
        if (fileInputStream == null) {
            return error(En_ResultStatus.INTERNAL_ERROR);
        }

        return lockService.doWithLock(Document.class, DOCUMENT_ID_FOR_CREATE, LockStrategy.TRANSACTION, LOCK_TIMEOUT_TIME_UNIT, LOCK_TIMEOUT, () -> {

            document.setState(En_DocumentState.ACTIVE);

            if (!saveToDB(document)) {
                log.error("createDocument(): failed to save/update document to the db");
                return error(En_ResultStatus.NOT_CREATED);
            }

            Long documentId = document.getId();
            Long projectId = document.getProjectId();

            if (!saveToIndex(fileItem.get(), documentId, projectId)) {
                log.error("createDocument(" + documentId + "): failed to add file to the index");
                if (!removeFromDB(documentId)) log.error("createDocument(" + documentId + "): failed to rollback document from the db");
                return error(En_ResultStatus.NOT_CREATED);
            }

            if (!saveToSVN(fileInputStream, documentId, projectId)) {
                log.error("createDocument(" + documentId + "): failed to save file to the svn");
                if (!removeFromIndex(documentId)) log.error("createDocument(" + documentId + "): failed to rollback document from the index");
                if (!removeFromDB(documentId)) log.error("createDocument(" + documentId + "): failed to rollback document from the db");
                return error(En_ResultStatus.NOT_CREATED);
            }

            return ok(document);
        });
    }

    @Override
    @Transactional
    public Result updateState( AuthToken token, Long documentId, En_DocumentState state) {

        if (documentId == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        Document document = new Document();
        document.setId(documentId);
        document.setState(state);

        if (documentDAO.updateState(document)) {
            return ok();
        } else {
            return error(En_ResultStatus.NOT_UPDATED);
        }
    }

    @Override
    @Transactional
    public Result<Document> updateDocument( AuthToken token, Document document) {

        if (document == null || document.getId() == null || !isValidDocument(document)) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        Document oldDocument = documentDAO.get(document.getId());

        if (oldDocument == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        En_ResultStatus validationStatus = checkDocumentDesignationValid(oldDocument, document);
        if (validationStatus != En_ResultStatus.OK) {
            return error(validationStatus);
        }

        if (!updateAtDB(document)) {
            log.error("updateDocument(" + document.getId() + "): failed to update document at the db");
            return error(En_ResultStatus.NOT_UPDATED);
        }

        return ok(document);
    }

    @Override
    public Result<Document> updateDocumentAndContent( AuthToken token, Document document, FileItem fileItem) {

        if (document == null || document.getId() == null || !isValidDocument(document) || fileItem == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        Long projectId = document.getProjectId();
        Long documentId = document.getId();

        if (document.getApproved()) {
            return error(En_ResultStatus.NOT_AVAILABLE);
        }

        InputStream fileInputStream = getInputStream(fileItem);
        if (fileInputStream == null) {
            return error(En_ResultStatus.INTERNAL_ERROR);
        }

        return lockService.doWithLock(Document.class, documentId, LockStrategy.TRANSACTION, LOCK_TIMEOUT_TIME_UNIT, LOCK_TIMEOUT, () -> {

            Document oldDocument = documentDAO.get(documentId);
            if (oldDocument == null) {
                return error(En_ResultStatus.NOT_FOUND);
            }

            En_ResultStatus validationStatus = checkDocumentDesignationValid(oldDocument, document);
            if (validationStatus != En_ResultStatus.OK) {
                return error(validationStatus);
            }

            byte[] oldFileData = getFromSVN(documentId, projectId);

            if (!updateAtDB(document)) {
                log.error("updateDocumentAndContent(" + documentId + "): failed to update document at the db");
                return error(En_ResultStatus.NOT_UPDATED);
            }

            if (!updateAtIndex(fileItem.get(), documentId, projectId)) {
                log.error("updateDocumentAndContent(" + documentId + "): failed to update file at the index");
                if (!updateAtDB(oldDocument)) log.error("updateDocumentAndContent(" + documentId + "): failed to rollback document from the db");
                return error(En_ResultStatus.NOT_UPDATED);
            }

            if (!updateAtSVN(fileInputStream, documentId, projectId)) {
                log.error("updateDocumentAndContent(" + documentId + "): failed to update file at the svn");
                if (!updateAtDB(oldDocument)) log.error("updateDocumentAndContent(" + documentId + "): failed to rollback document from the db");
                if (!updateAtIndex(oldFileData, documentId, projectId)) log.error("updateDocumentAndContent(" + documentId + "): failed to rollback document from the index");
                return error(En_ResultStatus.NOT_UPDATED);
            }

            return ok(document);
        });
    }

    @Override
    public Result<Long> removeDocument( AuthToken token, Long documentId, Long projectId) {

        if (documentId == null || projectId == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        return lockService.doWithLock(Document.class, documentId, LockStrategy.TRANSACTION, LOCK_TIMEOUT_TIME_UNIT, LOCK_TIMEOUT, () -> {

            if (!removeFromDB(documentId)) {
                log.error("removeDocument(" + documentId + "): failed to remove document from the db");
                return error(En_ResultStatus.NOT_REMOVED);
            }

            if (!removeFromSVN(documentId, projectId)) {
                log.error("removeDocument(" + documentId + "): failed to remove document from the svn | data inconsistency");
            }

            if (!removeFromIndex(documentId)) {
                log.error("removeDocument(" + documentId + "): failed to remove document from the index | data inconsistency");
            }

            return ok(documentId);
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
        if (!document.getApproved()) {
            return true;
        }
        Project project = Project.fromCaseObject(caseObjectDAO.get(document.getProjectId()));
        if (project == null) {
            return false;
        }
        if (project.getCustomerType() == En_CustomerType.MINISTRY_OF_DEFENCE) {
            return document.getInventoryNumber() != null && (document.getInventoryNumber() > 0);
        }
        return true;
    }

    private boolean saveToDB(Document document) {
        try {
            if (documentDAO.persist(document) == null) {
                log.error("saveToDB(): failed to save document to the db");
                return false;
            }
        } catch (Exception e) {
            log.error("saveToDB(): failed to save document to the db", e);
            return false;
        }
        return true;
    }

    private boolean updateAtDB(Document document) {
        try {
            if (!documentDAO.merge(document)) {
                log.error("updateAtDB(): failed to update document at the db");
                return false;
            }
        } catch (Exception e) {
            log.error("updateAtDB(): failed to update document at the db", e);
            return false;
        }
        return true;
    }

    private boolean removeFromDB(Long documentId) {
        try {
            if (!documentDAO.removeByKey(documentId)) {
                log.error("removeFromDB(" + documentId + "): failed to remove document from the db");
                return false;
            }
        } catch (Exception e) {
            log.error("removeFromDB(" + documentId + "): failed to remove document from the db", e);
            return false;
        }
        return true;
    }

    private boolean saveToIndex(byte[] data, Long documentId, Long projectId) {
        try {
            documentStorageIndex.addPdfDocument(data, projectId, documentId);
        } catch (Exception e) {
            log.error("saveToIndex(" + documentId + ", " + projectId + "): failed to add file to the index", e);
            return false;
        }
        return true;
    }

    private boolean updateAtIndex(byte[] data, Long documentId, Long projectId) {
        try {
            documentStorageIndex.updatePdfDocument(data, projectId, documentId);
        } catch (Exception e) {
            log.error("updateAtIndex(" + documentId + ", " + projectId + "): failed to update file at the index", e);
            return false;
        }
        return true;
    }

    private boolean removeFromIndex(Long documentId) {
        try {
            documentStorageIndex.removeDocument(documentId);
        } catch (Exception e) {
            log.error("removeFromIndex(" + documentId + "): failed to remove file from the index", e);
            return false;
        }
        return true;
    }

    private boolean saveToSVN(InputStream inputStream, Long documentId, Long projectId) {
        try {
            documentSvnService.saveDocument(projectId, documentId, inputStream);
        } catch (Exception e) {
            log.error("saveToSVN(" + documentId + ", " + projectId + "): failed to save file to the svn", e);
            return false;
        }
        return true;
    }

    private boolean updateAtSVN(InputStream inputStream, Long documentId, Long projectId) {
        try {
            documentSvnService.updateDocument(projectId, documentId, inputStream);
        } catch (Exception e) {
            log.error("updateAtSVN(" + documentId + ", " + projectId + "): failed to update file at the svn", e);
            return false;
        }
        return true;
    }

    private boolean removeFromSVN(Long documentId, Long projectId) {
        try {
            documentSvnService.removeDocument(projectId, documentId);
        } catch (Exception e) {
            log.error("removeFromSVN(" + documentId + ", " + projectId + "): failed to remove document from the svn", e);
            return false;
        }
        return true;
    }

    private byte[] getFromSVN(Long documentId, Long projectId) throws SVNException, IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            documentSvnService.getDocument(projectId, documentId, out);
            return out.toByteArray();
        }
    }

    private InputStream getInputStream(FileItem fileItem) {
        try {
            return fileItem.getInputStream();
        } catch (Exception e) {
            log.error("getInputStream(): failed to get input stream from file item", e);
            return null;
        }
    }
}
