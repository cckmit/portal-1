package ru.protei.portal.core.service;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.tmatesoft.svn.core.SVNErrorCode;
import org.tmatesoft.svn.core.SVNException;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.index.document.DocumentStorageIndex;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dao.DocumentDAO;
import ru.protei.portal.core.model.dict.En_CustomerType;
import ru.protei.portal.core.model.dict.En_DocumentFormat;
import ru.protei.portal.core.model.dict.En_DocumentState;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Document;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.DocumentQuery;
import ru.protei.portal.core.model.struct.Project;
import ru.protei.portal.core.svn.document.DocumentSvnApi;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.core.utils.services.lock.LockService;
import ru.protei.winter.core.utils.services.lock.LockStrategy;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.io.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.mysql.jdbc.StringUtils.isEmptyOrWhitespaceOnly;
import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

public class DocumentServiceImpl implements DocumentService {

    private static final Logger log = LoggerFactory.getLogger(DocumentServiceImpl.class);
    private static final Long DOCUMENT_ID_FOR_CREATE = -1L;
    private static final Long LOCK_TIMEOUT = 5L;
    private static final TimeUnit LOCK_TIMEOUT_TIME_UNIT = TimeUnit.SECONDS;
    private static final String authorRollback = "auto-rollback";

    @Autowired
    DocumentDAO documentDAO;
    @Autowired
    CaseObjectDAO caseObjectDAO;
    @Autowired
    DocumentStorageIndex documentStorageIndex;
    @Autowired
    LockService lockService;
    @Autowired
    DocumentSvnApi documentSvnApi;
    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;

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

        if (document.getProjectAsCaseObject() != null) {
            jdbcManyRelationsHelper.fill(document.getProjectAsCaseObject(), "locations");
        }
        jdbcManyRelationsHelper.fill(document, "members");

        resetDocumentPrivacyInfo(document);

        return ok(document);
    }

    @Override
    public Result<Document> createDocument(AuthToken token, Document document, FileItem docFile, FileItem pdfFile, String author) {

        boolean withDoc = docFile != null;
        boolean withPdf = pdfFile != null;
        En_DocumentFormat docFormat = withDoc ? predictDocFormat(docFile) : null;
        En_DocumentFormat pdfFormat = withPdf ? En_DocumentFormat.PDF : null;

        if (document == null || !isValidNewDocument(document, withDoc, withPdf)) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        En_ResultStatus validationStatus = checkDocumentDesignationValid(null, document);
        if (validationStatus != En_ResultStatus.OK) {
            return error(validationStatus);
        }

        if (document.getApproved() && !withPdf) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        return lockService.doWithLock(Document.class, DOCUMENT_ID_FOR_CREATE, LockStrategy.TRANSACTION, LOCK_TIMEOUT_TIME_UNIT, LOCK_TIMEOUT, () -> {

            document.setState(En_DocumentState.ACTIVE);

            if (!saveToDB(document)) {
                log.error("createDocument(): failed to create document at the db");
                return error(En_ResultStatus.NOT_CREATED);
            }

            Long documentId = document.getId();
            Long projectId = document.getProjectId();

            if (withPdf && !saveToIndex(pdfFile.get(), documentId, projectId)) {
                log.error("createDocument(" + documentId + "): failed to add pdf file to the index");
                if (!removeFromDB(documentId)) log.error("createDocument(" + documentId + "): failed to rollback document from the db");
                return error(En_ResultStatus.NOT_CREATED);
            }

            if (withDoc && !saveToSVN(docFile.getInputStream(), documentId, projectId, docFormat, author)) {
                log.error("createDocument(" + documentId + "): failed to save doc file to the svn");
                if (!removeFromIndex(documentId)) log.error("createDocument(" + documentId + "): failed to rollback pdf file from the index");
                if (!removeFromDB(documentId)) log.error("createDocument(" + documentId + "): failed to rollback document from the db");
                return error(En_ResultStatus.NOT_CREATED);
            }

            if (withPdf && !saveToSVN(pdfFile.getInputStream(), documentId, projectId, pdfFormat, author)) {
                log.error("createDocument(" + documentId + "): failed to save pdf file to the svn");
                if (withDoc && !removeFromSVN(documentId, projectId, docFormat, authorRollback)) log.error("createDocument(" + documentId + "): failed to rollback doc file from the svn");
                if (!removeFromIndex(documentId)) log.error("createDocument(" + documentId + "): failed to rollback pdf file from the index");
                if (!removeFromDB(documentId)) log.error("createDocument(" + documentId + "): failed to rollback document from the db");
                return error(En_ResultStatus.NOT_CREATED);
            }

            jdbcManyRelationsHelper.fill(document, "members");
            return ok(document);
        });
    }

    @Override
    public Result<Document> updateDocument(AuthToken token, Document document, FileItem docFile, FileItem pdfFile, String author) {

        boolean withDoc = docFile != null;
        boolean withPdf = pdfFile != null;
        En_DocumentFormat docFormat = withDoc ? predictDocFormat(docFile) : null;
        En_DocumentFormat pdfFormat = withPdf ? En_DocumentFormat.PDF : null;

        if (document == null || document.getId() == null || !isValidDocument(document)) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        Long projectId = document.getProjectId();
        Long documentId = document.getId();

        if (document.getApproved() && (withDoc || withPdf)) {
            return error(En_ResultStatus.NOT_AVAILABLE);
        }

        return lockService.doWithLock(Document.class, documentId, LockStrategy.TRANSACTION, LOCK_TIMEOUT_TIME_UNIT, LOCK_TIMEOUT, () -> {

            Document oldDocument = documentDAO.get(documentId);
            if (oldDocument == null) {
                return error(En_ResultStatus.NOT_FOUND);
            }
            jdbcManyRelationsHelper.fill(oldDocument, "members");

            En_ResultStatus validationStatus = checkDocumentDesignationValid(oldDocument, document);
            if (validationStatus != En_ResultStatus.OK) {
                return error(validationStatus);
            }

            List<En_DocumentFormat> formatsAtSvn = (withDoc || withPdf) ? listDocumentFormatsAtSVN(documentId, projectId) : Collections.emptyList();
            boolean withDocAtSvn = withDoc && formatsAtSvn.contains(docFormat);
            boolean withPdfAtSvn = withPdf && formatsAtSvn.contains(pdfFormat);
            byte[] oldBytesDoc = withDoc && withDocAtSvn ? getFromSVN(documentId, projectId, docFormat) : null;
            byte[] oldBytesPdf = withPdf && withPdfAtSvn ? getFromSVN(documentId, projectId, pdfFormat) : null;
            boolean withDocFileRollback = withDoc && oldBytesDoc != null;
            boolean withPdfFileRollback = withPdf && oldBytesPdf != null;

            if (!updateAtDB(document)) {
                log.error("updateDocument(" + documentId + "): failed to update document at the db");
                return error(En_ResultStatus.NOT_UPDATED);
            }

            if (withPdf && !updateAtIndex(pdfFile.get(), documentId, projectId)) {
                log.error("updateDocument(" + documentId + "): failed to update pdf file at the index");
                if (!updateAtDB(oldDocument)) log.error("updateDocument(" + documentId + "): failed to rollback document from the db");
                return error(En_ResultStatus.NOT_UPDATED);
            }

            if (withDoc && (withDocAtSvn ?
                !updateAtSVN(docFile.getInputStream(), documentId, projectId, docFormat, author) :
                !saveToSVN(docFile.getInputStream(), documentId, projectId, docFormat, author))
            ) {
                log.error("updateDocument(" + documentId + "): failed to update doc file at the svn");
                if (withPdfFileRollback && !updateAtIndex(oldBytesPdf, documentId, projectId)) log.error("updateDocument(" + documentId + "): failed to rollback pdf document from the index");
                if (!updateAtDB(oldDocument)) log.error("updateDocument(" + documentId + "): failed to rollback document from the db");
                return error(En_ResultStatus.NOT_CREATED);
            }

            if (withPdf && (withPdfAtSvn ?
                !updateAtSVN(pdfFile.getInputStream(), documentId, projectId, pdfFormat, author) :
                !saveToSVN(pdfFile.getInputStream(), documentId, projectId, pdfFormat, author))
            ) {
                log.error("updateDocument(" + documentId + "): failed to update pdf file at the svn");
                if (withDocFileRollback && !updateAtSVN(oldBytesDoc, documentId, projectId, docFormat, authorRollback)) log.error("updateDocument(" + documentId + "): failed to rollback doc file from the svn");
                if (withPdfFileRollback && !updateAtIndex(oldBytesPdf, documentId, projectId)) log.error("updateDocument(" + documentId + "): failed to rollback pdf document from the index");
                if (!updateAtDB(oldDocument)) log.error("updateDocument(" + documentId + "): failed to rollback document from the db");
                return error(En_ResultStatus.NOT_UPDATED);
            }

            if (withDoc) {
                List<En_DocumentFormat> filesToRemove = formatsAtSvn
                        .stream()
                        .filter(format -> format == En_DocumentFormat.DOC || format == En_DocumentFormat.DOCX)
                        .filter(format -> format != docFormat)
                        .collect(Collectors.toList());
                if (!filesToRemove.isEmpty()) {
                    log.info("updateDocument(" + documentId + "): cleanup | going to remove files from svn: " + StringUtils.join(filesToRemove, ", "));
                    for (En_DocumentFormat format : filesToRemove) {
                        if (!removeFromSVN(documentId, projectId, format, author)) {
                            log.error("updateDocument(" + documentId + "): cleanup | failed to remove " + format.getFormat() + " file from the svn");
                        }
                    }
                }
            }

            jdbcManyRelationsHelper.fill(document, "members");
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
    public Result<En_DocumentFormat> getDocumentFile(AuthToken token, Long documentId, Long projectId, En_DocumentFormat format, OutputStream outputStream) {
        try {
            format = mergeDocDocxFormats(documentId, projectId, format);
            documentSvnApi.getDocument(projectId, documentId, format, outputStream);
            return ok(format);
        } catch (SVNException e) {
            if (e.getErrorMessage() != null && e.getErrorMessage().getErrorCode() == SVNErrorCode.FS_NOT_FOUND) {
                log.info("getDocumentFile(): File not found (projectId = " + projectId + ", documentId = " + documentId + ")", e);
                return error(En_ResultStatus.NOT_FOUND);
            } else {
                log.error("getDocumentFile(): Failed to get file (projectId = " + projectId + ", documentId = " + documentId + "), " +
                          "error code = " + (e.getErrorMessage() != null ? e.getErrorMessage().getErrorCode() : "null"), e);
                return error(En_ResultStatus.INTERNAL_ERROR);
            }
        }
    }

    @Override
    public Result<String> getDocumentName(Long documentId) {
        String name = documentDAO.getName(documentId);
        return ok(name);
    }

    @Override
    public Result<Long> removeDocument(AuthToken token, Long documentId, Long projectId, String author) {

        if (documentId == null || projectId == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        return lockService.doWithLock(Document.class, documentId, LockStrategy.TRANSACTION, LOCK_TIMEOUT_TIME_UNIT, LOCK_TIMEOUT, () -> {

            if (!removeFromDB(documentId)) {
                log.error("removeDocument(" + documentId + "): failed to remove document from the db");
                return error(En_ResultStatus.NOT_REMOVED);
            }

            if (!removeFromIndex(documentId)) {
                log.error("removeDocument(" + documentId + "): failed to remove document from the index | data inconsistency");
            }

            for (En_DocumentFormat format : En_DocumentFormat.values()) {
                if (!removeFromSVN(documentId, projectId, format, author)) {
                    log.error("removeDocument(" + documentId + "): failed to remove " + format.getFormat() + " file from the svn | data inconsistency");
                }
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

    private boolean isValidNewDocument(Document document, boolean withDoc, boolean withPdf) {
        if (withDoc && !withPdf) {
            return StringUtils.isNotEmpty(document.getName()) &&
                    document.getProjectId() != null;
        } else {
            return isValidDocument(document);
        }
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

    private En_DocumentFormat predictDocFormat(FileItem fileItem) {
        String fileName = fileItem.getName();
        String fileExt = FilenameUtils.getExtension(fileName);
        En_DocumentFormat documentFormat = En_DocumentFormat.of(fileExt);
        return documentFormat == null ? En_DocumentFormat.DOCX : documentFormat;
    }

    private En_DocumentFormat mergeDocDocxFormats(Long documentId, Long projectId, En_DocumentFormat format) throws SVNException {
        if (format == En_DocumentFormat.PDF) {
            return format;
        }
        List<En_DocumentFormat> formatsAtSvn = listDocumentFormatsAtSVN(documentId, projectId);
        if (formatsAtSvn.contains(En_DocumentFormat.DOCX)) {
            return En_DocumentFormat.DOCX;
        } else {
            return En_DocumentFormat.DOC;
        }
    }

    private boolean saveToDB(Document document) {
        try {
            if (documentDAO.persist(document) == null) {
                log.error("saveToDB(): failed to save document to the db");
                return false;
            }
            jdbcManyRelationsHelper.persist(document, "members");
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
            jdbcManyRelationsHelper.persist(document, "members");
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
            documentStorageIndex.addPdfDocument(data, documentId, projectId);
        } catch (Exception e) {
            log.error("saveToIndex(" + documentId + ", " + projectId + "): failed to add file to the index", e);
            return false;
        }
        return true;
    }

    private boolean updateAtIndex(byte[] data, Long documentId, Long projectId) {
        try {
            documentStorageIndex.updatePdfDocument(data, documentId, projectId);
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

    private boolean saveToSVN(byte[] bytes, Long documentId, Long projectId, En_DocumentFormat documentFormat, String author) {
        return saveToSVN(new ByteArrayInputStream(bytes), documentId, projectId, documentFormat, author);
    }

    private boolean saveToSVN(InputStream inputStream, Long documentId, Long projectId, En_DocumentFormat documentFormat, String author) {
        try {
            documentSvnApi.saveDocument(projectId, documentId, documentFormat, author, inputStream);
        } catch (Exception e) {
            log.error("saveToSVN(" + documentId + ", " + projectId + "): failed to save file to the svn", e);
            return false;
        }
        return true;
    }

    private boolean updateAtSVN(byte[] bytes, Long documentId, Long projectId, En_DocumentFormat documentFormat, String author) {
        return updateAtSVN(new ByteArrayInputStream(bytes), documentId, projectId, documentFormat, author);
    }

    private boolean updateAtSVN(InputStream inputStream, Long documentId, Long projectId, En_DocumentFormat documentFormat, String author) {
        try {
            documentSvnApi.updateDocument(projectId, documentId, documentFormat, author, inputStream);
        } catch (Exception e) {
            log.error("updateAtSVN(" + documentId + ", " + projectId + "): failed to update file at the svn", e);
            return false;
        }
        return true;
    }

    private boolean removeFromSVN(Long documentId, Long projectId, En_DocumentFormat documentFormat, String author) {
        try {
            documentSvnApi.removeDocument(projectId, documentId, documentFormat, author);
        } catch (SVNException e) {
            if (e.getErrorMessage() != null && e.getErrorMessage().getErrorCode() == SVNErrorCode.FS_NOT_FOUND) {
                return true;
            }
            log.error("removeFromSVN(" + documentId + ", " + projectId + "): failed to remove document from the svn", e);
            return false;
        } catch (Exception e) {
            log.error("removeFromSVN(" + documentId + ", " + projectId + "): failed to remove document from the svn", e);
            return false;
        }
        return true;
    }

    private byte[] getFromSVN(Long documentId, Long projectId, En_DocumentFormat documentFormat) throws SVNException, IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            documentSvnApi.getDocument(projectId, documentId, documentFormat, out);
            return out.toByteArray();
        } catch (SVNException e) {
            if (e.getErrorMessage() != null && e.getErrorMessage().getErrorCode() == SVNErrorCode.FS_NOT_FOUND) {
                return null;
            }
            throw e;
        }
    }

    private List<En_DocumentFormat> listDocumentFormatsAtSVN(Long documentId, Long projectId) throws SVNException {
        return documentSvnApi.listDocuments(projectId, documentId)
                .stream()
                .map(FilenameUtils::getName)
                .filter(Objects::nonNull)
                .filter(filename -> filename.startsWith(String.valueOf(documentId)))
                .map(FilenameUtils::getExtension)
                .map(En_DocumentFormat::of)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
