package ru.protei.portal.ui.crm.client.activity.dashboard;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.IssueStates;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;

import java.util.ArrayList;
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
    public void onCreateClicked( ActionBarEvents.Clicked event ) {
        if ( !UiConstants.ActionBarIdentity.DASHBOARD.equals( event.identity ) || !policyService.hasPrivilegeFor( En_Privilege.ISSUE_CREATE )) {
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
                            lang.buttonCreate(), UiConstants.ActionBarIcons.CREATE, UiConstants.ActionBarIdentity.DASHBOARD ) );
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
        List<Long> productIds = null;
        if (policyService.getProfile() != null){
            productIds = new ArrayList<>();
            productIds.add( policyService.getProfile().getId() );
        }
        query.setManagerIds( productIds );

        return query;
    }

    private CaseQuery generateNewRecordsQuery(){
        CaseQuery query = new CaseQuery(En_CaseType.CRM_SUPPORT, null, En_SortField.last_update, En_SortDir.DESC);
        query.setStates(issueStates.getActiveStates());
        query.setWithoutManager( true );

        return query;
    }

    private CaseQuery generateInactiveRecordsQuery(){
        CaseQuery query = new CaseQuery(En_CaseType.CRM_SUPPORT, null, En_SortField.last_update, En_SortDir.DESC);
        List<En_CaseState> inactiveStates = new ArrayList<>(issueStates.getInactiveStates());
        query.setStates(inactiveStates);
        List<Long> productIds = null;
        if (policyService.getProfile() != null){
            productIds = new ArrayList<>();
            productIds.add( policyService.getProfile().getId() );
        }
        query.setManagerIds( productIds );

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
