package ru.protei.portal.core.service;

import org.apache.commons.fileupload.FileItem;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_DocumentState;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Document;
import ru.protei.portal.core.model.query.DocumentQuery;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

public interface DocumentService {

    @Privileged(requireAny = {En_Privilege.DOCUMENT_VIEW, En_Privilege.EQUIPMENT_VIEW})
    Result<SearchResult<Document>> getDocuments( AuthToken token, DocumentQuery query);

    @Privileged(requireAny = {En_Privilege.DOCUMENT_VIEW, En_Privilege.EQUIPMENT_VIEW})
    Result<SearchResult<Document>> getDocuments( AuthToken token, Long equipmentId);

    @Privileged(requireAny = {En_Privilege.DOCUMENT_VIEW, En_Privilege.EQUIPMENT_VIEW})
    Result<List<Document>> documentList( AuthToken token, Long equipmentId);

    @Privileged(requireAny = {En_Privilege.DOCUMENT_VIEW, En_Privilege.EQUIPMENT_VIEW})
    Result<Document> getDocument( AuthToken token, Long id);

    @Privileged(requireAny = {En_Privilege.DOCUMENT_CREATE, En_Privilege.EQUIPMENT_CREATE, En_Privilege.EQUIPMENT_EDIT})
    Result<Document> createDocument( AuthToken token, Document document, FileItem fileItem);

    @Privileged(requireAny = {En_Privilege.DOCUMENT_EDIT, En_Privilege.EQUIPMENT_CREATE, En_Privilege.EQUIPMENT_EDIT})
    @Auditable(En_AuditType.DOCUMENT_MODIFY)
    Result<Document> updateDocument( AuthToken token, Document document);

    @Privileged(requireAny = {En_Privilege.DOCUMENT_EDIT, En_Privilege.EQUIPMENT_CREATE, En_Privilege.EQUIPMENT_EDIT})
    @Auditable(En_AuditType.DOCUMENT_MODIFY)
    Result<Document> updateDocumentAndContent( AuthToken token, Document document, FileItem fileItem);

    @Privileged(requireAny = {En_Privilege.DOCUMENT_REMOVE, En_Privilege.EQUIPMENT_REMOVE})
    @Auditable(En_AuditType.DOCUMENT_REMOVE)
    Result<Long> removeDocument( AuthToken token, Long documentId, Long projectId);

    @Privileged(requireAny = {En_Privilege.DOCUMENT_VIEW, En_Privilege.PROJECT_VIEW})
    Result<SearchResult<Document>> getProjectDocuments( AuthToken token, Long projectId);

    @Privileged(requireAny = {En_Privilege.DOCUMENT_EDIT, En_Privilege.EQUIPMENT_CREATE, En_Privilege.EQUIPMENT_EDIT})
    @Auditable(En_AuditType.DOCUMENT_MODIFY)
    Result updateState( AuthToken token, Long documentId, En_DocumentState state);

    Result<String> getDocumentName(Long documentId);
}
