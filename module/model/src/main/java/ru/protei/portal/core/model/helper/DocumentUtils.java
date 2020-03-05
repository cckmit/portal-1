package ru.protei.portal.core.model.helper;

import ru.protei.portal.core.model.dict.En_CustomerType;
import ru.protei.portal.core.model.dict.En_DocumentCategory;
import ru.protei.portal.core.model.ent.Document;
import ru.protei.portal.core.model.ent.DocumentType;
import ru.protei.portal.core.model.struct.ProjectInfo;
import ru.protei.portal.core.model.util.documentvalidators.DocumentDecimalNumberValidator;

import static ru.protei.portal.core.model.helper.StringUtils.isEmpty;

public class DocumentUtils {

    static public boolean isValidNewDocument(Document document,  ProjectInfo project, boolean isDocFileSet, boolean isPdfFileSet) {
        if (document.getApproved() && !isPdfFileSet) {
            return false;
        }

        if (isDocFileSet && !isPdfFileSet) {
            return StringUtils.isNotEmpty(document.getName()) &&
                    document.getProjectId() != null;
        } else {
            return isValidDocument(document, project);
        }
    }

    static public boolean isValidDocument(Document document, ProjectInfo project){
        return document.isValid()
                && isValidInventoryNumberForMinistryOfDefence(document, project)
                && isValidDecimalNumber(document)
                && isValidApproveFields(document);
    }

    static public boolean isValidInventoryNumberForMinistryOfDefence(Document document, ProjectInfo project) {
        if (!document.getApproved()) {
            return true;
        }
        if (project == null) {
            return false;
        }
        if (needToCheckInventoryNumber(project, document.getApproved(), document.getType())) {
            return document.getInventoryNumber() != null && (document.getInventoryNumber() > 0);
        }
        return true;
    }

    static public boolean needToCheckInventoryNumber(ProjectInfo project,
                                                      boolean isApproved, DocumentType documentType) {
        return isApproved &&
                project.getCustomerType() == En_CustomerType.MINISTRY_OF_DEFENCE
                && documentType != null
                && documentType.getDocumentCategory() != En_DocumentCategory.ABROAD;
    }

    static public boolean isValidApproveFields(Document document) {
        if (!document.getApproved()) {
            return true;
        }
        return document.getApprovedBy() != null && document.getApprovalDate() != null;
    }

    static public boolean isValidDecimalNumber(Document document) {
        if (!document.getApproved()) {
            return true;
        }
        return DocumentDecimalNumberValidator.isValid(document.getDecimalNumber(), document.getType().getDocumentCategory());
    }
}
