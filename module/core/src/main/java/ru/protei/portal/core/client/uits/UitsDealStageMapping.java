package ru.protei.portal.core.client.uits;

import ru.protei.portal.core.model.util.CrmConstants.*;

public class UitsDealStageMapping {
    public static Long toCaseState(String uitsStageId) {
        if (uitsStageId == null)
            return null;
        switch (uitsStageId) {
            case UitsState.NEW:
                return State.CREATED;
            case UitsState.EXECUTING:
            case UitsState.REPORT_PREPARE:
                return State.OPENED;
            case UitsState.FEEDBACK_EXPECT:
                return State.INFO_REQUEST;
            case UitsState.WON:
                return State.DONE;
            case UitsState.LOSE:
                return State.CANCELED;
            default:
                return null;
        }
    }
}
