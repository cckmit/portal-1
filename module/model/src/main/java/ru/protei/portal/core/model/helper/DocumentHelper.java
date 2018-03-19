package ru.protei.portal.core.model.helper;

import ru.protei.portal.core.model.ent.Documentation;

public class DocumentHelper {
    public static boolean isDocumentValid(Documentation doc) {
        return doc.getDecimalNumber() != null &&
                doc.getType() != null &&
                doc.getManagerId() != null &&
                doc.getInventoryNumber() != null &&
                doc.getInventoryNumber() > 0 &&
                HelperFunc.isNotEmpty(doc.getProject()) &&
                HelperFunc.isNotEmpty(doc.getName());
    }
}
