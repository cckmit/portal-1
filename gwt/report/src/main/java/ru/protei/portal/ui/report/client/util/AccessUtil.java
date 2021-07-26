package ru.protei.portal.ui.report.client.util;

import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_ReportType;
import ru.protei.portal.core.model.dict.En_Scope;
import ru.protei.portal.core.model.struct.Pair;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.*;

public class AccessUtil {

    private static final Map<En_ReportType, Pair<En_Privilege, List<En_Scope>>> type2privilege = new HashMap<En_ReportType, Pair<En_Privilege, List<En_Scope>>>() {{
        put(En_ReportType.CASE_OBJECTS, new Pair<>(En_Privilege.ISSUE_REPORT, listOf()));
        put(En_ReportType.CASE_TIME_ELAPSED, new Pair<>(En_Privilege.ISSUE_REPORT, listOf(En_Scope.SYSTEM)));
        put(En_ReportType.CASE_RESOLUTION_TIME, new Pair<>(En_Privilege.ISSUE_REPORT, listOf(En_Scope.SYSTEM)));
        put(En_ReportType.PROJECT, new Pair<>(En_Privilege.ISSUE_REPORT, listOf(En_Scope.SYSTEM)));
        put(En_ReportType.CONTRACT, new Pair<>(En_Privilege.CONTRACT_REPORT, listOf(En_Scope.SYSTEM)));
        put(En_ReportType.NIGHT_WORK, new Pair<>(En_Privilege.ISSUE_REPORT, listOf(En_Scope.SYSTEM)));
        put(En_ReportType.YT_WORK, new Pair<>(En_Privilege.YT_REPORT, listOf(En_Scope.SYSTEM)));
    }};

    public static boolean canView(PolicyService policyService) {
        for (Pair<En_Privilege, List<En_Scope>> entry : privileges()) {
            En_Privilege privilege = entry.getA();
            List<En_Scope> scopes = entry.getB();
            boolean hasAccess = hasAccess(policyService, privilege, scopes);
            if (hasAccess) {
                return true;
            }
        }
        return false;
    }

    public static boolean canEdit(PolicyService policyService) {
        return canView(policyService);
    }

    public static List<En_ReportType> availableReportTypes(PolicyService policyService) {
        List<En_ReportType> types = new ArrayList<>();
        for (Map.Entry<En_ReportType, Pair<En_Privilege, List<En_Scope>>> entry : type2privilege.entrySet()) {
            En_ReportType type = entry.getKey();
            En_Privilege privilege = entry.getValue().getA();
            List<En_Scope> scopes = entry.getValue().getB();
            boolean hasAccess = hasAccess(policyService, privilege, scopes);
            if (hasAccess) {
                types.add(type);
            }
        }
        return types;
    }

    private static boolean hasAccess(PolicyService policyService, En_Privilege privilege, List<En_Scope> scopes) {
        if (isEmpty(scopes)) {
            return policyService.hasPrivilegeFor(privilege);
        } else {
            return stream(scopes)
                    .allMatch(scope -> policyService.hasScopeForPrivilege(privilege, scope));
        }
    }

    private static List<Pair<En_Privilege, List<En_Scope>>> privileges() {
        return stream(type2privilege.values())
                .distinct()
                .collect(Collectors.toList());
    }
}
