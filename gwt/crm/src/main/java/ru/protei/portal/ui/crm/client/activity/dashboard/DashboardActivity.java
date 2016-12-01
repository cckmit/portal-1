package ru.protei.portal.ui.crm.client.activity.dashboard;

import com.google.inject.Inject;
import org.springframework.beans.factory.annotation.Autowired;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.IssueEvents;
import ru.protei.portal.ui.common.server.service.SessionService;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.crm.client.events.DashboardEvents;
import ru.protei.portal.ui.issue.client.service.IssueServiceAsync;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by bondarenko on 01.12.16.
 */
public abstract class DashboardActivity implements AbstractDashboardActivity, Activity{

    @Event
    public void onAuthSuccess( AuthEvents.Success event ) {
        onShow(null);
    }

    @Event
    public void onShow( DashboardEvents.Show event ) {
        initDetails.parent.clear();
        initDetails.parent.add( view.asWidget() );

        generateActiveRecords();
//        fireEvent(new IssueEvents.ShowCustom(generateNewRecordsQuery(), view.getNewRecordsContainer()));
//        fireEvent(new IssueEvents.ShowCustom(generateCompletedRecordsQuery(), view.getCompletedRecordsContainer()));

    }

    @Event
    public void onInitDetails( AppEvents.InitDetails initDetails ) {
        this.initDetails = initDetails;
    }


    private void generateActiveRecords(){
        CaseQuery query = new CaseQuery(En_CaseType.CRM_SUPPORT, null, En_SortField.creation_date, En_SortDir.DESC);
        query.setManagerId(sessionService.getUserSessionDescriptor(request).getPerson().getId());

        issueService.getStateList(new RequestCallback<List<En_CaseState>>() {
            @Override
            public void onError(Throwable throwable) {
            }

            @Override
            public void onSuccess(List<En_CaseState> en_caseStates) {
                en_caseStates.remove(En_CaseState.DONE);
                en_caseStates.remove(En_CaseState.TEST_CUST);
                en_caseStates.remove(En_CaseState.VERIFIED);

                query.setStates(en_caseStates);

                fireEvent(new IssueEvents.ShowCustom(query, view.getActiveRecordsContainer()));

            }
        });
    }

    private CaseQuery generateNewRecordsQuery(){
        return null;
    }

    private CaseQuery generateCompletedRecordsQuery(){
        return null;
    }



    @Inject
    AbstractDashboardView view;

    @Inject
    IssueServiceAsync issueService;

    @Autowired
    SessionService sessionService;

    @Autowired
    HttpServletRequest request;

    private AppEvents.InitDetails initDetails;
}
