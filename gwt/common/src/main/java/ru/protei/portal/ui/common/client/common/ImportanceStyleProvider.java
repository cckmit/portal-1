package ru.protei.portal.ui.common.client.common;

/**
 * Провайдер css-класса для иконки критичности
 */
public class ImportanceStyleProvider {
    public static String getImportanceIcon(String importanceCode) {
        if (importanceCode == null) {
            return "importance-level";
        }

        return "importance-level " + importanceCode.toLowerCase();
    }
}
