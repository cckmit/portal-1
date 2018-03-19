package ru.protei.portal.core.model.helper;

import ru.protei.portal.core.model.ent.Document;

public class DocumentHelper {
    public static boolean isDocumentValid(Document doc) {
        return doc.getDecimalNumber() != null &&
                doc.getDecimalNumber().getId() != null &&
                doc.getType() != null &&
                doc.getManagerId() != null &&
                doc.getInventoryNumber() != null &&
                doc.getInventoryNumber() > 0 &&
                HelperFunc.isNotEmpty(doc.getProject()) &&
                HelperFunc.isNotEmpty(doc.getName());
    }
}
