package ru.protei.portal.ui.common.client.util;

import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.util.CrmConstants;

import java.util.*;
import java.util.stream.Collectors;

public class CaseStateUtils {

    private static List<Long> activeStateIds;
    private static List<Long> newStateIds;
    private static List<Long> filterCaseResolutionTimeActiveStateIds;
    private static Map<Long, String> jiraStatusComments;

    static {

        activeStateIds = new ArrayList<>(11);
        activeStateIds.add(CrmConstants.State.CREATED);
        activeStateIds.add(CrmConstants.State.OPENED);
        activeStateIds.add(CrmConstants.State.ACTIVE);
        activeStateIds.add(CrmConstants.State.TEST_LOCAL);
        activeStateIds.add(CrmConstants.State.WORKAROUND);
        activeStateIds.add(CrmConstants.State.INFO_REQUEST);
        activeStateIds.add(CrmConstants.State.NX_REQUEST);
        activeStateIds.add(CrmConstants.State.CUST_REQUEST);
        activeStateIds.add(CrmConstants.State.CUST_PENDING);
        activeStateIds.add(CrmConstants.State.TEST_CUST);
        activeStateIds.add(CrmConstants.State.DEVELOPMENT);
        activeStateIds.add(CrmConstants.State.COMMERCIAL_NEGOTIATIONS);

        newStateIds = new ArrayList<>(3);
        newStateIds.add(CrmConstants.State.CREATED);
        newStateIds.add(CrmConstants.State.OPENED);
        newStateIds.add(CrmConstants.State.ACTIVE);

        filterCaseResolutionTimeActiveStateIds = new ArrayList<>(8);
        filterCaseResolutionTimeActiveStateIds.add(CrmConstants.State.CREATED);
        filterCaseResolutionTimeActiveStateIds.add(CrmConstants.State.OPENED);
        filterCaseResolutionTimeActiveStateIds.add(CrmConstants.State.ACTIVE);
        filterCaseResolutionTimeActiveStateIds.add(CrmConstants.State.TEST_LOCAL);
        filterCaseResolutionTimeActiveStateIds.add(CrmConstants.State.WORKAROUND);
        filterCaseResolutionTimeActiveStateIds.add(CrmConstants.State.INFO_REQUEST);
        filterCaseResolutionTimeActiveStateIds.add(CrmConstants.State.NX_REQUEST);
        filterCaseResolutionTimeActiveStateIds.add(CrmConstants.State.CUST_REQUEST);

        jiraStatusComments = new HashMap<>(3);
        jiraStatusComments.put(CrmConstants.State.VERIFIED, "Поскольку статус является терминальным, то в него переводим только после получения подтверждения от Заказчика, что тикет можно закрыть.\nВ противном случае ставим Request to customer или Request to NX.");
        jiraStatusComments.put(CrmConstants.State.NX_REQUEST, "Не забывать переводить в этот статус, в противном случае Заказчик ждёт реакции от нас.");
        jiraStatusComments.put(CrmConstants.State.CUST_REQUEST, "Не забывать переводить в этот статус, в противном случае Заказчик ждёт реакции от нас.");
    }

    public static List<Long> getActiveStateIds() {
        return activeStateIds;
    }

    public static List<Long> getNewStateIds() {
        return newStateIds;
    }

    public static Set<CaseState> getFilterCaseResolutionTimeActiveStates() {
        return filterCaseResolutionTimeActiveStateIds.stream().map(id -> new CaseState(id)).collect(Collectors.toSet());
    }

    public static Map<Long, String> getJiraStatusComments() {
        return jiraStatusComments;
    }
}
