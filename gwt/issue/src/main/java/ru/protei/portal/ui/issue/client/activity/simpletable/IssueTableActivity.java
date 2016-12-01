package ru.protei.portal.ui.issue.client.activity.simpletable;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.ui.common.client.events.IssueEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.issue.client.service.IssueServiceAsync;

import java.util.List;

/**
 * Created by bondarenko on 01.12.16.
 */
public abstract class IssueTableActivity implements AbstractIssueTableActivity, Activity {


    @PostConstruct
    public void onInit() {
        view.setActivity( this );
    }

    @Event
    public void onShow( IssueEvents.ShowCustom event ) {

        //this.fireEvent( new AppEvents.InitPanelName( lang.dashboard() ) );
//        initDetails.parent.clear();
//        initDetails.parent.add( view.asWidget() );

        event.parent.clear();
        event.parent.add(view.asWidget());

        view.clearRecords();
        requestIssues(event.query);
    }

//    @Event
//    public void onInitDetails( AppEvents.InitDetails initDetails ) {
//        this.initDetails = initDetails;
//    }

    @Override
    public void onItemClicked( CaseObject value ) {
        fireEvent(new IssueEvents.Edit(value.getId(), null));
    }

    @Override
    public void onEditClicked( CaseObject value ) {
        fireEvent(new IssueEvents.Edit(value.getId(), null));
    }

    public void requestIssues(CaseQuery query) {
        issueService.getIssues( query, new RequestCallback<List<CaseObject>>() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent( new NotifyEvents.Show( lang.errGetList(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess( List<CaseObject> caseObjects ) {
                view.putRecords(caseObjects);
            }
        } );
    }


//    private CaseQuery getQuery() {
//        CaseQuery query = new CaseQuery(En_CaseType.CRM_SUPPORT, null, En_SortField.creation_date, En_SortDir.DESC);
//
//
//        issueService.getStateList(new RequestCallback<List<En_CaseState>>() {
//            @Override
//            public void onError(Throwable throwable) {}
//
//            @Override
//            public void onSuccess(List<En_CaseState> en_caseStates) {
//                en_caseStates.remove(En_CaseState.DONE);
//                en_caseStates.remove(En_CaseState.TEST_CUST);
//                en_caseStates.remove(En_CaseState.VERIFIED);
//
//                query.setStates(en_caseStates);
//            }
//        });
//        query.setManagerId(  );
//
//        if(filterView.states().getValue() != null)
//            query.setStateIds(
//                    filterView.states().getValue()
//                            .stream()
//                            .map( En_CaseState::getId )
//                            .collect( Collectors.toList() ));
//
//        if(filterView.importances().getValue() != null)
//            query.setImportanceIds(
//                    filterView.importances().getValue()
//                            .stream()
//                            .map( En_ImportanceLevel::getId )
//                            .collect( Collectors.toList() ));
//
//        DateInterval interval = filterView.dateRange().getValue();
//
//        if(interval != null) {
//            query.setFrom( interval == null ? null : interval.from );
//            query.setTo( interval == null ? null : interval.to );
//        }
//
//        return query;
//    }

    @Inject
    Lang lang;

    @Inject
    AbstractIssueTableView view;

    @Inject
    IssueServiceAsync issueService;

//    private AppEvents.InitDetails initDetails;

}
