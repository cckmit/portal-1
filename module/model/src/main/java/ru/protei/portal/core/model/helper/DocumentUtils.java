package ru.protei.portal.core.model.helper;

import ru.protei.portal.core.model.dict.En_CustomerType;
import ru.protei.portal.core.model.dict.En_DocumentCategory;
import ru.protei.portal.core.model.ent.Document;
import ru.protei.portal.core.model.struct.ProjectInfo;

public class DocumentUtils {

    static public boolean isValidNewDocument(Document document,  ProjectInfo project, boolean isPdfFileSet, boolean isDocFileSet) {
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
                && isValidApproveFields(document);
    }

    static public boolean isValidInventoryNumberForMinistryOfDefence(Document document, ProjectInfo project) {
        if (!document.getApproved()) {
            return true;
        }
        if (project == null) {
            return false;
        }
        if (needToCheckInventoryNumber(document, project)) {
            return document.getInventoryNumber() != null && (document.getInventoryNumber() > 0);
        }
        return true;
    }

    static private boolean needToCheckInventoryNumber(Document document, ProjectInfo project) {
        return document.getApproved() &&
                project.getCustomerType() == En_CustomerType.MINISTRY_OF_DEFENCE
                && document.getType() != null
                && document.getType().getDocumentCategory() != En_DocumentCategory.ABROAD;
    }

    static public boolean isValidApproveFields(Document document) {
        if (!document.getApproved()) {
            return true;
        }
        return document.getApprovedBy() != null && document.getApprovalDate() != null;
    }
}
