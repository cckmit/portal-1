package ru.protei.portal.core.model.util;

import java.util.Arrays;
import java.util.List;

public class CaseStateUtil {

    public static boolean isTerminalState(long stateId) {
        return terminalStates.contains(stateId);
    }

    public static boolean isCompletedState(long stateId) {
        return completedStates.contains(stateId);
    }


    private static final List<Long> terminalStates = Arrays.asList(
            CrmConstants.State.VERIFIED
    );

    private static final List<Long> completedStates = Arrays.asList(
            CrmConstants.State.VERIFIED,
            CrmConstants.State.DONE,
            CrmConstants.State.CANCELED,
            CrmConstants.State.IGNORED,
            CrmConstants.State.SOLVED_NOT_A_PROBLEM,
            CrmConstants.State.SOLVED_FIXED,
            CrmConstants.State.SOLVED_DUPLICATED
    );
}
