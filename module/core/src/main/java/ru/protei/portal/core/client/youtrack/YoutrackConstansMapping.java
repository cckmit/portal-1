package ru.protei.portal.core.client.youtrack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;

import java.util.List;

public class YoutrackConstansMapping {
    public static En_CaseState toCaseState( String ytStateId) {
        if (ytStateId == null)
            return null;
        switch (ytStateId) {
            case "New":
            case "Новый":
                return En_CaseState.CREATED;
            case "Done":
            case "Выдан заказчику":
            case "Complete":
                return En_CaseState.DONE;
            case "Ignore":
                return En_CaseState.IGNORED;
            case "Closed":
                return En_CaseState.CLOSED;
            case "Canceled":
            case "Отменен":
                return En_CaseState.CANCELED;
            case "Verified":
                return En_CaseState.VERIFIED;
            default:
                return En_CaseState.ACTIVE;
        }
    }

    public static En_CaseState toCaseState( List<String> ytStateIds ) {
        if (ytStateIds == null || ytStateIds.size() != 1)
            return null;
        return toCaseState(ytStateIds.get(0));
    }

    public static En_ImportanceLevel toCaseImportance( String ytpriority ) {
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
                log.warn( "toCaseImportance(): Detected unknown YouTrack priority level= {}", ytpriority );
            }
        }
        return result;
    }

    private static final Logger log = LoggerFactory.getLogger( YoutrackConstansMapping.class );
}
