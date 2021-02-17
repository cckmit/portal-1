package ru.protei.portal.ui.common.client.common;

import static ru.protei.portal.core.model.util.CrmConstants.Style.IMPORTANCE_LEVEL;

/**
 * Провайдер css-класса для иконки критичности
 */
public class ImportanceStyleProvider {
    public static String getImportanceIcon(String importanceCode) {
        if (importanceCode == null) {
            return IMPORTANCE_LEVEL;
        }

        return IMPORTANCE_LEVEL + " " + importanceCode.toLowerCase();
    }
}
