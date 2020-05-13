package ru.protei.portal.ui.common.client.common;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.ent.CaseState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Статусы обращений
 */
public abstract class IssueStates implements Activity{

    private static Set<CaseState> EnCaseStatesToCaseStateSet(Collection<En_CaseState> enCaseStates) {
        return enCaseStates.stream().map(CaseState::new).collect(Collectors.toSet());
    }

    @Inject
    public void onInit() {
        dashboardActiveStateIds = new ArrayList<>(10);
        dashboardActiveStateIds.add((long)En_CaseState.CREATED.getId());
        dashboardActiveStateIds.add((long)En_CaseState.OPENED.getId());
        dashboardActiveStateIds.add((long)En_CaseState.ACTIVE.getId());
        dashboardActiveStateIds.add((long)En_CaseState.TEST_LOCAL.getId());
        dashboardActiveStateIds.add((long)En_CaseState.WORKAROUND.getId());
        dashboardActiveStateIds.add((long)En_CaseState.INFO_REQUEST.getId());
        dashboardActiveStateIds.add((long)En_CaseState.NX_REQUEST.getId());
        dashboardActiveStateIds.add((long)En_CaseState.CUST_REQUEST.getId());
        dashboardActiveStateIds.add((long)En_CaseState.CUST_PENDING.getId());
        dashboardActiveStateIds.add((long)En_CaseState.TEST_CUST.getId());

        dashboardNewIssueStatesIds = new ArrayList<>(3);
        dashboardNewIssueStatesIds.add((long)En_CaseState.CREATED.getId());
        dashboardNewIssueStatesIds.add((long)En_CaseState.OPENED.getId());
        dashboardNewIssueStatesIds.add((long)En_CaseState.ACTIVE.getId());

        List<En_CaseState> issueFilterWidgetActiveEnStates = new ArrayList<>(8);
        issueFilterWidgetActiveEnStates.add(En_CaseState.CREATED);
        issueFilterWidgetActiveEnStates.add(En_CaseState.OPENED);
        issueFilterWidgetActiveEnStates.add(En_CaseState.ACTIVE);
        issueFilterWidgetActiveEnStates.add(En_CaseState.TEST_LOCAL);
        issueFilterWidgetActiveEnStates.add(En_CaseState.WORKAROUND);
        issueFilterWidgetActiveEnStates.add(En_CaseState.INFO_REQUEST);
        issueFilterWidgetActiveEnStates.add(En_CaseState.NX_REQUEST);
        issueFilterWidgetActiveEnStates.add(En_CaseState.CUST_REQUEST);

        issueFilterWidgetActiveStates = EnCaseStatesToCaseStateSet(issueFilterWidgetActiveEnStates);

        tableDefaultQueryStateIds = new ArrayList<>(3);
        tableDefaultQueryStateIds.add((long)En_CaseState.CREATED.getId());
        tableDefaultQueryStateIds.add((long)En_CaseState.OPENED.getId());
        tableDefaultQueryStateIds.add((long)En_CaseState.ACTIVE.getId());

        employeeRegistrationEnState  = new ArrayList<>(3);
        employeeRegistrationEnState.add(En_CaseState.ACTIVE);
        employeeRegistrationEnState.add(En_CaseState.CREATED);
        employeeRegistrationEnState.add(En_CaseState.DONE);

    }

    public List<Long> getDashboardActiveStateIds() {
        return dashboardActiveStateIds;
    }

    public List<Long> getDashboardNewIssueStatesIds() {
        return dashboardNewIssueStatesIds;
    }

    public Set<CaseState> getIssueFilterWidgetActiveStates() {
        return issueFilterWidgetActiveStates;
    }

    public List<Long> getTableDefaultQueryStateIds() {
        return tableDefaultQueryStateIds;
    }

    public List<En_CaseState> getEmployeeRegistrationEnState() {
        return employeeRegistrationEnState;
    }

    private List<Long> dashboardActiveStateIds;
    private List<Long> dashboardNewIssueStatesIds;
    private Set<CaseState> issueFilterWidgetActiveStates;
    private List<Long> tableDefaultQueryStateIds;
    private List<En_CaseState> employeeRegistrationEnState;
}
