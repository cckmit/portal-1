package ru.protei.portal.core.client.youtrack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.util.CrmConstants;

public class YoutrackConstansMapping {
    public static Long toCaseState(String ytStateId) {
        if (ytStateId == null)
            return null;
        switch (ytStateId) {
            case "New":
            case "Новый":
                return CrmConstants.State.CREATED;
            case "Done":
            case "Выдан заказчику":
            case "Complete":
                return CrmConstants.State.DONE;
            case "Ignore":
                return CrmConstants.State.IGNORED;
            case "Closed":
                return CrmConstants.State.CLOSED;
            case "Canceled":
            case "Отменен":
                return CrmConstants.State.CANCELED;
            case "Verified":
                return CrmConstants.State.VERIFIED;
            default:
                return CrmConstants.State.ACTIVE;
        }
    }

    public static En_ImportanceLevel toCaseImportance(String ytpriority) {
        En_ImportanceLevel result = null;

        if (ytpriority != null) {
            switch (ytpriority) {
                case "Show-stopper":
                case "Critical":
                    result = En_ImportanceLevel.CRITICAL;
                    break;
                case "Important":
                    result = En_ImportanceLevel.IMPORTANT;
                    break;
                case "Basic":
                    result = En_ImportanceLevel.BASIC;
                    break;
                case "Low":
                    result = En_ImportanceLevel.COSMETIC;
                    break;
                default:
                    return result = null;
            }

            if (result == null) {
                log.warn("toCaseImportance(): Detected unknown YouTrack priority level= {}", ytpriority);
            }
        }
        return result;
    }

    private static final Logger log = LoggerFactory.getLogger(YoutrackConstansMapping.class);
}
