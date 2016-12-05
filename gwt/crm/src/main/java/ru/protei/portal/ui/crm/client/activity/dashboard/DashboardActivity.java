package ru.protei.portal.ui.crm.client.activity.dashboard;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.ui.common.client.common.IssueStates;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.IssueEvents;
import ru.protei.portal.ui.common.shared.model.Profile;
import ru.protei.portal.ui.crm.client.events.DashboardEvents;

import java.util.Collections;

/**
 * Created by bondarenko on 01.12.16.
 */
public abstract class DashboardActivity implements AbstractDashboardActivity, Activity{

    @Event
    public void onAuthSuccess( AuthEvents.Success event ) {
        profile = event.profile;
    }

    @Event
    public void onShow( DashboardEvents.Show event ) {
        initDetails.parent.clear();
        initDetails.parent.add( view.asWidget() );

        fireEvent(new IssueEvents.ShowCustom(generateActiveRecordsQuery(), view.getActiveRecordsContainer()));
        fireEvent(new IssueEvents.ShowCustom(generateNewRecordsQuery(), view.getNewRecordsContainer()));
        fireEvent(new IssueEvents.ShowCustom(generateInactiveRecordsQuery(), view.getInactiveRecordsContainer()));
    }

    @Event
    public void onInitDetails( AppEvents.InitDetails initDetails ) {
        this.initDetails = initDetails;
    }


    private CaseQuery generateActiveRecordsQuery(){
        CaseQuery query = new CaseQuery(En_CaseType.CRM_SUPPORT, null, En_SortField.creation_date, En_SortDir.DESC);
        query.setManagerId(profile.getId());
        query.setStates(issueStates.getActiveStates());
        query.setLimit(ISSUE_LIMIT);

        return query;
    }

    private CaseQuery generateNewRecordsQuery(){
        CaseQuery query = new CaseQuery(En_CaseType.CRM_SUPPORT, null, En_SortField.creation_date, En_SortDir.ASC);
        query.setStates(Collections.singletonList(En_CaseState.CREATED));
        query.setLimit(ISSUE_LIMIT);

        return query;
    }

    private CaseQuery generateInactiveRecordsQuery(){
        CaseQuery query = new CaseQuery(En_CaseType.CRM_SUPPORT, null, En_SortField.last_update, En_SortDir.ASC);
        query.setStates(issueStates.getInactiveStates());
        query.setLimit(ISSUE_LIMIT);

        return query;
    }



    @Inject
    AbstractDashboardView view;

    @Inject
    IssueStates issueStates;

    private AppEvents.InitDetails initDetails;
    private Profile profile;
    private final int ISSUE_LIMIT = 12;
}
