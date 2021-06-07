package ru.protei.portal.core.client.youtrack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
            case "Done, Verified":
                return CrmConstants.State.VERIFIED;
            default:
                return CrmConstants.State.ACTIVE;
        }
    }
    private static final Logger log = LoggerFactory.getLogger(YoutrackConstansMapping.class);
}
