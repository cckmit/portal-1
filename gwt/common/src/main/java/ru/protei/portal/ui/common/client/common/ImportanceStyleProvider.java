package ru.protei.portal.ui.common.client.common;

import ru.protei.portal.core.model.dict.En_ImportanceLevel;

/**
 * Провайдер css-класса для иконки критичности
 */
public class ImportanceStyleProvider {

    public static String getImportanceIcon(En_ImportanceLevel importance) {
        if (importance == null) {
            return "importance-level";
        }
        switch (importance){
            case BASIC: return "importance-level basic";
            case IMPORTANT: return "importance-level important";
            case CRITICAL: return "importance-level critical";
            case COSMETIC: return "importance-level cosmetic";
            case MEDIUM: return "importance-level medium";
            case EMERGENCY: return "importance-level emergency";
            case HIGH: return "importance-level high";
            case LOW: return "importance-level low";
            default: return "importance-level";
        }
    }
}
