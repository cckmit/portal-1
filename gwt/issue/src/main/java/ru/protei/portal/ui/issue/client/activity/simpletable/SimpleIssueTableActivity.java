package ru.protei.portal.ui.issue.client.activity.simpletable;

import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.ui.common.client.events.IssueEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.IssueServiceAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.List;

/**
 * Created by bondarenko on 01.12.16.
 */
public abstract class SimpleIssueTableActivity implements AbstractSimpleIssueTableActivity, Activity {

    @Event
    public void onShow( IssueEvents.ShowCustom event ) {
        AbstractSimpleIssueTableView table = createTableView();

        event.parent.clear();
        event.parent.add(table.asWidget());

        requestIssues(event.query, table, event.afterRequestAction);
    }

    @Override
    public void onItemClicked( CaseShortView value ) {
        fireEvent(new IssueEvents.Edit(value.getId(), null));
    }

    public void requestIssues(CaseQuery query, AbstractSimpleIssueTableView tableView, Runnable thanAction) {
        issueService.getIssues( query, new RequestCallback<List<CaseShortView>>() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent( new NotifyEvents.Show( lang.errGetList(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess( List<CaseShortView> caseObjects ) {
                tableView.putRecords(caseObjects);
                if(thanAction != null)
                    thanAction.run();
            }
        } );
    }

    private AbstractSimpleIssueTableView createTableView(){
        AbstractSimpleIssueTableView table = tableProvider.get();
        table.setActivity(this);
        return table;
    }

    @Inject
    Lang lang;

    @Inject
    IssueServiceAsync issueService;

    @Inject
    Provider<AbstractSimpleIssueTableView> tableProvider;

}
