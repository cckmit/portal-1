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
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.event.DocumentDocFileUpdatedByMemberEvent;
import ru.protei.portal.core.event.DocumentMemberAddedEvent;
import ru.protei.portal.core.index.document.DocumentStorageIndex;
import ru.protei.portal.core.model.dao.DocumentDAO;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dao.ProjectDAO;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Document;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.DocumentQuery;
import ru.protei.portal.core.model.dto.Project;
import ru.protei.portal.core.model.dto.ProjectInfo;
import ru.protei.portal.core.model.view.PersonProjectMemberView;
import ru.protei.portal.core.service.events.EventPublisherService;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.portal.core.svn.document.DocumentSvnApi;
import ru.protei.portal.core.model.helper.DocumentUtils;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.core.utils.services.lock.LockService;
import ru.protei.winter.core.utils.services.lock.LockStrategy;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.io.*;
import java.util.*;
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
    ProjectDAO projectDAO;
    @Autowired
    PersonDAO personDAO;
    @Autowired
    DocumentStorageIndex documentStorageIndex;
    @Autowired
    LockService lockService;
    @Autowired
    DocumentSvnApi documentSvnApi;
    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;
    @Autowired
    EventPublisherService publisherService;
    @Autowired
    PolicyService policyService;
    @Autowired
    ProjectService projectService;
    @Autowired
    PortalConfig config;

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

        jdbcManyRelationsHelper.fill(document, "members");

        resetDocumentPrivacyInfo(document);

        return ok(document);
    }

    @Override
    @Transactional
    public Result<Document> createDocument(AuthToken token, Document document, FileItem docFile, FileItem pdfFile, FileItem approvalSheetFile, String author) {
        if (document == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        boolean withDoc = docFile != null;
        boolean withPdf = pdfFile != null;
        boolean withApprovalSheet = approvalSheetFile != null;
        En_DocumentFormat docFormat = withDoc ? predictDocFormat(docFile) : null;
        En_DocumentFormat pdfFormat = withPdf ? En_DocumentFormat.PDF : null;
        En_DocumentFormat ApprovalSheetFormat = withApprovalSheet ? En_DocumentFormat.AS : null;

        if (document.getProjectId() == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }
        ProjectInfo projectInfo = ProjectInfo.fromProject(projectDAO.get(document.getProjectId()));
        if (!DocumentUtils.isValidNewDocument(document, projectInfo, withDoc, withPdf)) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        En_ResultStatus validationStatus = checkDocumentDesignationValid(null, document);
        if (validationStatus != En_ResultStatus.OK) {
            return error(validationStatus);
        }

        return lockService.doWithLock(Document.class, DOCUMENT_ID_FOR_CREATE, LockStrategy.TRANSACTION, LOCK_TIMEOUT_TIME_UNIT, LOCK_TIMEOUT, () -> {

            document.setState(En_DocumentState.ACTIVE);

            if (!saveToDB(document)) {
                log.error("createDocument(): failed to create document at the db");
                return error(En_ResultStatus.NOT_CREATED);
            }

            Long documentId = document.getId();
            Long projectId = document.getProjectId();

            String commitMessageAdd = getCommitMessageAdd(documentId, projectId, author, "");
            String commitMessageRemoveAuto = getCommitMessageRemove(documentId, projectId, authorRollback, "");

            if (withPdf && !saveToIndex(pdfFile.get(), documentId, projectId)) {
                log.error("createDocument(" + documentId + "): failed to add pdf file to the index");
                if (!removeFromDB(documentId)) log.error("createDocument(" + documentId + "): failed to rollback document from the db");
                return error(En_ResultStatus.NOT_CREATED);
            }

            if (withDoc && !saveToSVN(docFile.getInputStream(), documentId, projectId, docFormat, commitMessageAdd)) {
                log.error("createDocument(" + documentId + "): failed to save doc file to the svn");
                if (!removeFromIndex(documentId)) log.error("createDocument(" + documentId + "): failed to rollback pdf file from the index");
                if (!removeFromDB(documentId)) log.error("createDocument(" + documentId + "): failed to rollback document from the db");
                return error(En_ResultStatus.NOT_CREATED);
            }

            if (withPdf && !saveToSVN(pdfFile.getInputStream(), documentId, projectId, pdfFormat, commitMessageAdd)) {
                log.error("createDocument(" + documentId + "): failed to save pdf file to the svn");
                if (withDoc && !removeFromSVN(documentId, projectId, docFormat, commitMessageRemoveAuto)) log.error("createDocument(" + documentId + "): failed to rollback doc file from the svn");
                if (!removeFromIndex(documentId)) log.error("createDocument(" + documentId + "): failed to rollback pdf file from the index");
                if (!removeFromDB(documentId)) log.error("createDocument(" + documentId + "): failed to rollback document from the db");
                return error(En_ResultStatus.NOT_CREATED);
            }

            if (withApprovalSheet && !saveToSVN(approvalSheetFile.getInputStream(), documentId, projectId, ApprovalSheetFormat, author)) {
                log.error("createDocument(" + documentId + "): failed to save approval sheet file to the svn");
                if (withDoc && !removeFromSVN(documentId, projectId, docFormat, authorRollback)) log.error("createDocument(" + documentId + "): failed to rollback doc file from the svn");
                if (withPdf && !removeFromSVN(documentId, projectId, pdfFormat, authorRollback)) log.error("createDocument(" + documentId + "): failed to rollback pdf file from the svn");
                if (!removeFromIndex(documentId)) log.error("createDocument(" + documentId + "): failed to rollback document file from the index");
                if (!removeFromDB(documentId)) log.error("createDocument(" + documentId + "): failed to rollback document from the db");
                return error(En_ResultStatus.NOT_CREATED);
            }

            List<Long> newMembers = CollectionUtils.stream(document.getMembers()).map(Person::getId).collect(Collectors.toList());
            List<Person> personList = getDocumentMemberAddedEvent( document, newMembers );

            return ok(document)
                    .publishEvent( new DocumentMemberAddedEvent(this, document, personList));
        });
    }

    @Override
    @Transactional
    public Result<Document> updateDocument(AuthToken token, Document document, FileItem docFile, FileItem pdfFile, FileItem approvalSheetFile, String author) {
        if (document == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        boolean withDoc = docFile != null;
        boolean withPdf = pdfFile != null;
        boolean withApprovalSheet = approvalSheetFile != null;
        En_DocumentFormat docFormat = withDoc ? predictDocFormat(docFile) : null;
        En_DocumentFormat pdfFormat = withPdf ? En_DocumentFormat.PDF : null;
        En_DocumentFormat ApprovalSheetFormat = withApprovalSheet ? En_DocumentFormat.AS : null;

        ProjectInfo projectInfo = ProjectInfo.fromProject(projectDAO.get(document.getProjectId()));
        if (document.getId() == null || !DocumentUtils.isValidDocument(document, projectInfo)) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        Long projectId = document.getProjectId();
        Long documentId = document.getId();

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

            if (oldDocument.getApproved() && document.getApproved() && (withDoc || withPdf)) {
                return error(En_ResultStatus.NOT_AVAILABLE);
            }

            List<En_DocumentFormat> listFormatsAtSvn = documentSvnApi.isProjectPathExist(projectId)
                    ? listDocumentFormatsAtSVN(documentId, projectId)
                    : Collections.emptyList();

            boolean withDocAtSvn = withDoc && listFormatsAtSvn.contains(docFormat);
            boolean withPdfAtSvn = withPdf && listFormatsAtSvn.contains(pdfFormat);
            boolean withApprovalSheetAtSvn = withApprovalSheet && listFormatsAtSvn.contains(ApprovalSheetFormat);

            boolean isPdfInSvn = listFormatsAtSvn.contains(En_DocumentFormat.PDF);
            if (!oldDocument.getApproved() && document.getApproved() && !(isPdfInSvn || withPdf)) {
                return error(En_ResultStatus.SVN_ERROR);
            }

            byte[] oldBytesDoc = withDoc && withDocAtSvn ? getFromSVN(documentId, projectId, docFormat) : null;
            byte[] oldBytesPdf = withPdf && withPdfAtSvn ? getFromSVN(documentId, projectId, pdfFormat) : null;
            byte[] oldBytesApprovalSheet = withApprovalSheet && withApprovalSheetAtSvn ? getFromSVN(documentId, projectId, ApprovalSheetFormat) : null;
            boolean withDocFileRollback = withDoc && oldBytesDoc != null;
            boolean withPdfFileRollback = withPdf && oldBytesPdf != null;
            boolean withApprovalSheetFileRollback = withApprovalSheet && oldBytesApprovalSheet != null;

            String commitMessageAdd = getCommitMessageAdd(documentId, projectId, author, "");
            String commitMessageUpdate = getCommitMessageUpdate(documentId, projectId, author, "");
            String commitMessageRemove = getCommitMessageRemove(documentId, projectId, author, "");
            String commitMessageRemoveAuto = getCommitMessageRemove(documentId, projectId, authorRollback, "");

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
                !updateAtSVN(docFile.getInputStream(), documentId, projectId, docFormat, commitMessageUpdate) :
                !saveToSVN(docFile.getInputStream(), documentId, projectId, docFormat, commitMessageAdd))
            ) {
                log.error("updateDocument(" + documentId + "): failed to update doc file at the svn");
                if (withPdfFileRollback && !updateAtIndex(oldBytesPdf, documentId, projectId)) log.error("updateDocument(" + documentId + "): failed to rollback pdf document from the index");
                if (!updateAtDB(oldDocument)) log.error("updateDocument(" + documentId + "): failed to rollback document from the db");
                return error(En_ResultStatus.NOT_CREATED);
            }

            if (withPdf && (withPdfAtSvn ?
                !updateAtSVN(pdfFile.getInputStream(), documentId, projectId, pdfFormat, commitMessageUpdate) :
                !saveToSVN(pdfFile.getInputStream(), documentId, projectId, pdfFormat, commitMessageAdd))
            ) {
                log.error("updateDocument(" + documentId + "): failed to update pdf file at the svn");
                if (withDocFileRollback && !updateAtSVN(oldBytesDoc, documentId, projectId, docFormat, commitMessageRemoveAuto)) log.error("updateDocument(" + documentId + "): failed to rollback doc file from the svn");
                if (withPdfFileRollback && !updateAtIndex(oldBytesPdf, documentId, projectId)) log.error("updateDocument(" + documentId + "): failed to rollback pdf document from the index");
                if (!updateAtDB(oldDocument)) log.error("updateDocument(" + documentId + "): failed to rollback document from the db");
                return error(En_ResultStatus.NOT_UPDATED);
            }

            if (withApprovalSheet && (withApprovalSheetAtSvn ?
                    !updateAtSVN(approvalSheetFile.getInputStream(), documentId, projectId, ApprovalSheetFormat, author) :
                    !saveToSVN(approvalSheetFile.getInputStream(), documentId, projectId, ApprovalSheetFormat, author))
            ) {
                log.error("updateDocument(" + documentId + "): failed to update approval sheet file at the svn");
                if (withDocFileRollback && !updateAtSVN(oldBytesDoc, documentId, projectId, docFormat, authorRollback)) log.error("updateDocument(" + documentId + "): failed to rollback doc file from the svn");
                if (withPdfFileRollback && !updateAtIndex(oldBytesPdf, documentId, projectId)) log.error("updateDocument(" + documentId + "): failed to rollback pdf document from the index");
                if (withApprovalSheetFileRollback && !updateAtIndex(oldBytesApprovalSheet, documentId, projectId)) log.error("updateDocument(" + documentId + "): failed to rollback approval sheet from the index");
                if (!updateAtDB(oldDocument)) log.error("updateDocument(" + documentId + "): failed to rollback document from the db");
                return error(En_ResultStatus.NOT_UPDATED);
            }

            if (withDoc) {
                removeDuplicatedDocFilesFromSvn(listFormatsAtSvn, docFormat, documentId, projectId, commitMessageRemove);
            }

            List<Long> newMembers = fetchNewMemberIds(oldDocument, document);
            List<Person> personList = getDocumentMemberAddedEvent( document, newMembers );

            return ok(document)
                    .publishEvent( new DocumentMemberAddedEvent(this, document, personList));
        });
    }

    @Override
    @Transactional
    public Result<Document> updateDocumentDocFileByMember(AuthToken token, Long documentId, FileItem docFile, String comment, String author) {

        if (documentId == null || docFile == null || StringUtils.isEmpty(comment)) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        En_DocumentFormat docFormat = predictDocFormat(docFile);

        if (docFormat != En_DocumentFormat.DOC && docFormat != En_DocumentFormat.DOCX) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        return lockService.doWithLock(Document.class, documentId, LockStrategy.TRANSACTION, LOCK_TIMEOUT_TIME_UNIT, LOCK_TIMEOUT, () -> {

            Document document = documentDAO.get(documentId);
            if (document == null) {
                return error(En_ResultStatus.NOT_FOUND);
            }
            jdbcManyRelationsHelper.fill(document, "members");
            Long projectId = document.getProjectId();

            if (document.getApproved()) {
                return error(En_ResultStatus.NOT_AVAILABLE);
            }

            List<En_DocumentFormat> formatsAtSvn;

            if(documentSvnApi.isProjectPathExist(projectId)){
                formatsAtSvn = listDocumentFormatsAtSVN(documentId, projectId);
            } else {
                log.error("updateDocumentDocFile(" + documentId + "): failed to update doc file at the svn. The path of this project doesn't exists");
                return error(En_ResultStatus.NOT_UPDATED);
            }
            boolean withDocAtSvn = formatsAtSvn.contains(docFormat);

            String commitMessageAdd = getCommitMessageAdd(documentId, projectId, author, comment);
            String commitMessageUpdate = getCommitMessageUpdate(documentId, projectId, author, comment);
            String commitMessageRemove = getCommitMessageRemove(documentId, projectId, author, "");

            if (withDocAtSvn ?
                !updateAtSVN(docFile.getInputStream(), documentId, projectId, docFormat, commitMessageUpdate) :
                !saveToSVN(docFile.getInputStream(), documentId, projectId, docFormat, commitMessageAdd)
            ) {
                log.error("updateDocumentDocFile(" + documentId + "): failed to update doc file at the svn");
                return error(En_ResultStatus.NOT_UPDATED);
            }

            removeDuplicatedDocFilesFromSvn(formatsAtSvn, docFormat, documentId, projectId, commitMessageRemove);
            List<Person> personList = getDocumentDocFileUpdatedByMember( document );
            Person initiator = personDAO.get(token.getPersonId());
            jdbcManyRelationsHelper.fill(initiator, Person.Fields.CONTACT_ITEMS);

            return ok(document)
                    .publishEvent( new DocumentDocFileUpdatedByMemberEvent(this, initiator, document, personList, comment));
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
            boolean idDocFile = format == En_DocumentFormat.DOCX || format == En_DocumentFormat.DOC;
            if (idDocFile && !hasAccessToDocFile(token, documentId)) {
                return error(En_ResultStatus.PERMISSION_DENIED);
            }
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
    @Transactional
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

    private List<Long> fetchNewMemberIds(Document oldDocument, Document newDocument) {
        List<Long> oldMembers = CollectionUtils.stream(oldDocument.getMembers()).map(Person::getId).collect(Collectors.toList());
        List<Long> newMembers = CollectionUtils.stream(newDocument.getMembers()).map(Person::getId).collect(Collectors.toList());
        return CollectionUtils.diffCollection(oldMembers, newMembers).getAddedEntries();
    }

    private  List<Person> getDocumentMemberAddedEvent(Document document, List<Long> personIds) {
        if (document == null || CollectionUtils.isEmpty(personIds)) {
            return null;
        }
        List<Person> people = personDAO.getListByKeys(personIds);
        jdbcManyRelationsHelper.fill(people, Person.Fields.CONTACT_ITEMS);
        return people;
    }

    private List<Person> getDocumentDocFileUpdatedByMember( Document document ) {
        List<Person> personList = new ArrayList<>();
        if (document.getContractor() != null) {
            jdbcManyRelationsHelper.fill(document.getContractor(), Person.Fields.CONTACT_ITEMS);
            personList.add(document.getContractor());
        }
        if (document.getRegistrar() != null) {
            jdbcManyRelationsHelper.fill(document.getRegistrar(), Person.Fields.CONTACT_ITEMS);
            personList.add(document.getRegistrar());
        }
        Result<PersonProjectMemberView> result = projectService.getProject(null, document.getProjectId())
                .map(Project::getLeader);
        if (result.isOk() && result.getData() != null) {
            Person leader = personDAO.get(result.getData().getId());
            jdbcManyRelationsHelper.fill(leader, Person.Fields.CONTACT_ITEMS);
            personList.add(leader);
        }
        personList.addAll(document.getMembers());
        return personList;
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

    private En_DocumentFormat predictDocFormat(FileItem fileItem) {
        String fileName = fileItem.getName();
        String fileExt = FilenameUtils.getExtension(fileName);
        En_DocumentFormat documentFormat = En_DocumentFormat.of(fileExt);
        return documentFormat == null ? En_DocumentFormat.DOCX : documentFormat;
    }

    private En_DocumentFormat mergeDocDocxFormats(Long documentId, Long projectId, En_DocumentFormat format) throws SVNException {
        if (format == En_DocumentFormat.PDF || format == En_DocumentFormat.AS) {
            return format;
        }
        List<En_DocumentFormat> formatsAtSvn = listDocumentFormatsAtSVN(documentId, projectId);
        if (formatsAtSvn.contains(En_DocumentFormat.DOCX)) {
            return En_DocumentFormat.DOCX;
        } else {
            return En_DocumentFormat.DOC;
        }
    }

    private boolean hasAccessToDocFile(AuthToken token, Long documentId) {
        if (policyService.hasGrantAccessFor(token.getRoles(), En_Privilege.DOCUMENT_EDIT)) {
            return true;
        }
        if (!policyService.hasGrantAccessFor(token.getRoles(), En_Privilege.DOCUMENT_VIEW)) {
            return false;
        }
        Document document = documentDAO.partialGet(documentId, "id");
        jdbcManyRelationsHelper.fill(document, "members");
        return CollectionUtils.stream(document.getMembers())
                .map(Person::getId)
                .collect(Collectors.toList())
                .contains(token.getPersonId());
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

    private boolean saveToSVN(byte[] bytes, Long documentId, Long projectId, En_DocumentFormat documentFormat, String commitMessage) {
        return saveToSVN(new ByteArrayInputStream(bytes), documentId, projectId, documentFormat, commitMessage);
    }

    private boolean saveToSVN(InputStream inputStream, Long documentId, Long projectId, En_DocumentFormat documentFormat, String commitMessage) {
        try {
            documentSvnApi.saveDocument(projectId, documentId, documentFormat, commitMessage, inputStream);
        } catch (Exception e) {
            log.error("saveToSVN(" + documentId + ", " + projectId + "): failed to save file to the svn", e);
            return false;
        }
        return true;
    }

    private boolean updateAtSVN(byte[] bytes, Long documentId, Long projectId, En_DocumentFormat documentFormat, String commitMessage) {
        return updateAtSVN(new ByteArrayInputStream(bytes), documentId, projectId, documentFormat, commitMessage);
    }

    private boolean updateAtSVN(InputStream inputStream, Long documentId, Long projectId, En_DocumentFormat documentFormat, String commitMessage) {
        try {
            documentSvnApi.updateDocument(projectId, documentId, documentFormat, commitMessage, inputStream);
        } catch (Exception e) {
            log.error("updateAtSVN(" + documentId + ", " + projectId + "): failed to update file at the svn", e);
            return false;
        }
        return true;
    }

    private boolean removeFromSVN(Long documentId, Long projectId, En_DocumentFormat documentFormat, String commitMessage) {
        try {
            documentSvnApi.removeDocument(projectId, documentId, documentFormat, commitMessage);
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

    private void removeDuplicatedDocFilesFromSvn(Collection<En_DocumentFormat> formatsAtSvn, En_DocumentFormat formatToKeep, Long documentId, Long projectId, String commitMessage) {
        Collection<En_DocumentFormat> formatsToRemove = CollectionUtils.stream(formatsAtSvn)
                .filter(format -> format == En_DocumentFormat.DOC || format == En_DocumentFormat.DOCX)
                .filter(format -> format != formatToKeep)
                .collect(Collectors.toList());
        for (En_DocumentFormat format : formatsToRemove) {
            if (!removeFromSVN(documentId, projectId, format, commitMessage)) {
                log.error("removeDuplicatedDocFilesFromSvn(" + documentId + "): cleanup | failed to remove " + format.getFormat() + " file from the svn");
            }
        }
    }

    private String getCommitMessageAdd(Long documentId, Long projectId, String author, String comment) {
        String commitTemplate = config.data().svn().getCommitMessageAdd();
        return getCommitMessage(commitTemplate, documentId, projectId, author, comment);
    }

    private String getCommitMessageUpdate(Long documentId, Long projectId, String author, String comment) {
        String commitTemplate = config.data().svn().getCommitMessageUpdate();
        return getCommitMessage(commitTemplate, documentId, projectId, author, comment);
    }

    private String getCommitMessageRemove(Long documentId, Long projectId, String author, String comment) {
        String commitTemplate = config.data().svn().getCommitMessageRemove();
        return getCommitMessage(commitTemplate, documentId, projectId, author, comment);
    }

    private String getCommitMessage(String commitTemplate, Long documentId, Long projectId, String author, String comment) {
        String commitMessage = String.format(commitTemplate, projectId, documentId, author);
        if (StringUtils.isNotEmpty(comment)) {
            commitMessage += ": " + comment;
        }
        return commitMessage;
    }
}
