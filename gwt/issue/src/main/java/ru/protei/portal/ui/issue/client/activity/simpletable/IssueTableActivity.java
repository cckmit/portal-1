package ru.protei.portal.ui.issue.client.activity.simpletable;

import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.ui.common.client.events.IssueEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.IssueServiceAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.List;

/**
 * Created by bondarenko on 01.12.16.
 */
public abstract class IssueTableActivity implements AbstractIssueTableActivity, Activity {

    @Event
    public void onShow( IssueEvents.ShowCustom event ) {
        AbstractIssueTableView table = createTableView();

        event.parent.clear();
        event.parent.add(table.asWidget());

        requestIssues(event.query, table);
    }

    @Override
    public void onItemClicked( CaseObject value ) {
        fireEvent(new IssueEvents.Edit(value.getId(), null));
    }

    public void requestIssues(CaseQuery query, AbstractIssueTableView tableView) {
        issueService.getIssues( query, new RequestCallback<List<CaseObject>>() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent( new NotifyEvents.Show( lang.errGetList(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess( List<CaseObject> caseObjects ) {
                tableView.putRecords(caseObjects);
            }
        } );
    }

    private AbstractIssueTableView createTableView(){
        AbstractIssueTableView table = tableProvider.get();
        table.setActivity(this);
        return table;
    }

    @Inject
    Lang lang;

    @Inject
    IssueServiceAsync issueService;

    @Inject
    Provider<AbstractIssueTableView> tableProvider;

}
