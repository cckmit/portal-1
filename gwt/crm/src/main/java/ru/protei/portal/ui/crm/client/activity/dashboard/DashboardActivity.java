package ru.protei.portal.ui.crm.client.activity.dashboard;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.IssueStates;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.shared.model.Profile;
import ru.protei.winter.web.common.client.events.SectionEvents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Активити Дашборда
 */
public abstract class DashboardActivity implements AbstractDashboardActivity, Activity{

    @Event
    public void onInitDetails( AppEvents.InitDetails initDetails ) {
        this.initDetails = initDetails;
    }

    @Event
    public void onDashboardInit( AuthEvents.Success event ) {
        activeRecordsQuery = generateActiveRecordsQuery();
        newRecordsQuery = generateNewRecordsQuery();
        inactiveRecordsQuery = generateInactiveRecordsQuery();
    }

    @Event
    public void onCreateClicked( SectionEvents.Clicked event ) {
        if ( !UiConstants.ActionBarIdentity.DASHBOARD.equals( event.identity ) || policyService.hasPrivilegeFor( En_Privilege.ISSUE_CREATE )) {
            return;
        }

        fireEvent(new IssueEvents.Edit());
    }


    @Event
    public void onShow( DashboardEvents.Show event ) {
        initDetails.parent.clear();
        initDetails.parent.add( view.asWidget() );

        if ( policyService.hasPrivilegeFor( En_Privilege.ISSUE_CREATE ) ) {
            fireEvent(
                    new ActionBarEvents.Add(
                            lang.buttonCreate(), UiConstants.ActionBarIcons.CREATE, UiConstants.ActionBarIdentity.ISSUE ) );
        }
        initWidgets();

    }

    @Event
    public void onChangeIssues( IssueEvents.ChangeModel event ) {
        initWidgets();
    }

    private void initWidgets(){
        fireEvent(
                new DashboardEvents.ShowTableBlock(
                        activeRecordsQuery, view.getActiveRecordsContainer(), lang.activeRecords()));
        fireEvent(
                new DashboardEvents.ShowTableBlock(
                        newRecordsQuery, view.getNewRecordsContainer(), lang.newRecords()));
        fireEvent(
                new DashboardEvents.ShowTableBlock(
                        inactiveRecordsQuery, view.getInactiveRecordsContainer(), lang.inactiveRecords(), true ));
    }


    private CaseQuery generateActiveRecordsQuery(){
        CaseQuery query = new CaseQuery(En_CaseType.CRM_SUPPORT, null, En_SortField.last_update, En_SortDir.DESC);
        query.setStates( issueStates.getActiveStates() );
        query.setManagerId( policyService.getProfile().getId() );

        return query;
    }

    private CaseQuery generateNewRecordsQuery(){
        CaseQuery query = new CaseQuery(En_CaseType.CRM_SUPPORT, null, En_SortField.last_update, En_SortDir.DESC);
        query.setStates(Collections.singletonList(En_CaseState.CREATED));
        query.setManagerId(-1L);

        return query;
    }

    private CaseQuery generateInactiveRecordsQuery(){
        CaseQuery query = new CaseQuery(En_CaseType.CRM_SUPPORT, null, En_SortField.last_update, En_SortDir.DESC);
        List<En_CaseState> inactiveStates = new ArrayList<>(issueStates.getInactiveStates());
        inactiveStates.remove(En_CaseState.VERIFIED);
        query.setStates(inactiveStates);
        query.setManagerId( policyService.getProfile().getId() );

        return query;
    }

    @Inject
    AbstractDashboardView view;

    @Inject
    IssueStates issueStates;

    @Inject
    Lang lang;

    @Inject
    PolicyService policyService;

    private AppEvents.InitDetails initDetails;

    private CaseQuery activeRecordsQuery;
    private CaseQuery newRecordsQuery;
    private CaseQuery inactiveRecordsQuery;
}
