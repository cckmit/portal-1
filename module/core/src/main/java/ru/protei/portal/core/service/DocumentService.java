package ru.protei.portal.core.service;

import org.apache.commons.fileupload.FileItem;
import org.springframework.scheduling.annotation.Async;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_DocumentFormat;
import ru.protei.portal.core.model.dict.En_DocumentState;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.api.ApiDocument;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Document;
import ru.protei.portal.core.model.query.DocumentQuery;
import ru.protei.portal.core.model.struct.DocumentFile;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.io.OutputStream;
import java.util.List;

import static ru.protei.portal.config.MainConfiguration.BACKGROUND_TASKS;

public interface DocumentService {

    @Privileged(requireAny = {En_Privilege.DOCUMENT_VIEW, En_Privilege.EQUIPMENT_VIEW})
    Result<SearchResult<Document>> getDocuments( AuthToken token, DocumentQuery query);

    @Async(BACKGROUND_TASKS)
    void documentBuildFullIndex();

    @Privileged(requireAny = {En_Privilege.DOCUMENT_VIEW, En_Privilege.EQUIPMENT_VIEW})
    Result<SearchResult<Document>> getDocuments( AuthToken token, Long equipmentId);

    @Privileged(requireAny = {En_Privilege.DOCUMENT_VIEW, En_Privilege.EQUIPMENT_VIEW})
    Result<List<Document>> documentList( AuthToken token, Long equipmentId);

    @Privileged(requireAny = {En_Privilege.DOCUMENT_VIEW, En_Privilege.EQUIPMENT_VIEW})
    Result<Document> getDocument( AuthToken token, Long id);

    @Privileged(requireAny = {En_Privilege.DOCUMENT_CREATE, En_Privilege.EQUIPMENT_CREATE, En_Privilege.EQUIPMENT_EDIT})
    @Auditable(En_AuditType.DOCUMENT_CREATE)
    Result<Document> createDocument(AuthToken token, Document document, DocumentFile docFile, DocumentFile pdfFile, DocumentFile approvalSheetFile, String author);

    @Privileged(requireAny = {En_Privilege.DOCUMENT_EDIT, En_Privilege.EQUIPMENT_CREATE, En_Privilege.EQUIPMENT_EDIT})
    @Auditable(En_AuditType.DOCUMENT_MODIFY)
    Result<Document> updateDocument( AuthToken token, Document document, FileItem docFile, FileItem pdfFile, FileItem approvalSheetFile, String author);

    @Privileged(requireAny = {En_Privilege.DOCUMENT_VIEW, En_Privilege.DOCUMENT_EDIT})
    @Auditable(En_AuditType.DOCUMENT_MODIFY)
    Result<Document> updateDocumentDocFileByMember(AuthToken token, Long documentId, FileItem docFile, String comment, String author);

    @Privileged(requireAny = {En_Privilege.DOCUMENT_REMOVE, En_Privilege.EQUIPMENT_REMOVE})
    @Auditable(En_AuditType.DOCUMENT_REMOVE)
    Result<Long> removeDocument( AuthToken token, Long documentId, Long projectId, String author);

    @Privileged(requireAny = {En_Privilege.DOCUMENT_VIEW, En_Privilege.PROJECT_VIEW})
    Result<SearchResult<Document>> getProjectDocuments( AuthToken token, Long projectId);

    @Privileged(requireAny = {En_Privilege.DOCUMENT_EDIT, En_Privilege.EQUIPMENT_CREATE, En_Privilege.EQUIPMENT_EDIT})
    @Auditable(En_AuditType.DOCUMENT_MODIFY)
    Result updateState( AuthToken token, Long documentId, En_DocumentState state);

    @Privileged(requireAny = {En_Privilege.DOCUMENT_VIEW, En_Privilege.DOCUMENT_EDIT, En_Privilege.EQUIPMENT_VIEW})
    Result<En_DocumentFormat> getDocumentFile(AuthToken token, Long documentId, Long projectId, En_DocumentFormat format, OutputStream outputStream);

    Result<String> getDocumentName(Long documentId);

    @Privileged(requireAny = {En_Privilege.DOCUMENT_CREATE, En_Privilege.EQUIPMENT_CREATE, En_Privilege.EQUIPMENT_EDIT})
    @Auditable(En_AuditType.DOCUMENT_CREATE)
    Result<Document> createDocumentByApi(AuthToken token, ApiDocument apiDocument);

    @Privileged(requireAny = {En_Privilege.DOCUMENT_REMOVE, En_Privilege.EQUIPMENT_REMOVE})
    @Auditable(En_AuditType.DOCUMENT_REMOVE)
    Result<Long> removeDocumentByApi( AuthToken token, Long documentId);
}
