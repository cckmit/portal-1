package ru.protei.portal.ui.crm.client.activity.dashboardblocks.table;

import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.ui.common.client.events.DashboardEvents;
import ru.protei.portal.ui.common.client.events.IssueEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.IssueServiceAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by bondarenko on 01.12.16.
 */
public abstract class DashboardTableActivity implements AbstractDashboardTableActivity, Activity {

    @Event
    public void onShow( DashboardEvents.ShowTableBlock event ) {
        AbstractDashboardTableView table = createTableView();

        event.parent.clear();
        event.parent.add(table.asWidget());
        isLoaderShow = event.isLoaderShow;
        query = event.query;

        view.getImportance().setValue(importanceLevels);
        updateSection();
    }

    private void updateSection(){
        updateRecordsCount();
        requestRecords();
    }

    @Override
    public void onItemClicked( CaseObject value ) {
        fireEvent(new IssueEvents.Edit(value.getId(), null));
    }

    private void requestRecords() {
        issueService.getIssues( query, new RequestCallback<List<CaseObject>>() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent( new NotifyEvents.Show( lang.errGetList(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess( List<CaseObject> caseObjects ) {
                view.putRecords(caseObjects);
                if(isLoaderShow)
                    view.showLoader(false);
            }
        } );
    }


    private AbstractDashboardTableView createTableView(){
        AbstractDashboardTableView table = tableProvider.get();
        table.setActivity(this);
        return table;
    }

    private void updateRecordsCount(){
        issueService.getIssuesCount(query, new RequestCallback<Long>() {
            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onSuccess(Long aLong) {
                view.setRecordsCount(aLong.intValue());
            }
        });
    }

    @Override
    public void updateImportance(Set<En_ImportanceLevel> importanceLevels) {
        query.setImportanceIds(
                importanceLevels
                        .stream()
                        .map(En_ImportanceLevel::getId)
                        .collect(Collectors.toList())
        );

        updateSection();
    }

    @Inject
    Lang lang;

    @Inject
    IssueServiceAsync issueService;

    @Inject
    Provider<AbstractDashboardTableView> tableProvider;

    @Inject
    AbstractDashboardTableView view;

    private CaseQuery query;
    private boolean isLoaderShow;
    private Set<En_ImportanceLevel> importanceLevels = Arrays.stream(En_ImportanceLevel.values()).collect(Collectors.toSet());

}
