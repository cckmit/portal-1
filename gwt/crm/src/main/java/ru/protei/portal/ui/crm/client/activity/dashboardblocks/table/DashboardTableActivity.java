package ru.protei.portal.ui.crm.client.activity.dashboardblocks.table;

import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.ui.common.client.events.DashboardEvents;
import ru.protei.portal.ui.common.client.events.IssueEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.IssueControllerAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Блок дашборда: таблица кейсов
 */
public abstract class DashboardTableActivity implements AbstractDashboardTableActivity, Activity {

    @Event
    public void onShow( DashboardEvents.ShowTableBlock event ) {
        AbstractDashboardTableView view = createTableView();

        event.parent.clear();
        event.parent.add(view.asWidget());


        DashboardTableModel model = new DashboardTableModel(view, event.query, event.isLoaderShow);
        viewToModel.put(view, model);

        view.getImportance().setValue(IMPORTANCE_LEVELS);
        view.setSectionName(event.sectionName);

        updateSection(model);
    }

    @Override
    public void onItemClicked( CaseShortView value ) {
        fireEvent(new IssueEvents.Edit(value.getCaseNumber(), null));
    }

    @Override
    public void updateImportance(AbstractDashboardTableView view, Set<En_ImportanceLevel> importanceLevels) {
        DashboardTableModel model = viewToModel.get(view);

        if(model == null)
            return;

        List<Integer> importanceIds =
                importanceLevels
                        .stream()
                        .map(En_ImportanceLevel::getId)
                        .collect(Collectors.toList());

        if(model.query.getImportanceIds() != null && model.query.getImportanceIds().equals(importanceIds))
            return;

        model.query.setImportanceIds(importanceIds);

        updateSection(model);
    }

    @Override
    public void removeView(AbstractDashboardTableView view) {
        viewToModel.remove(view);
    }

    @Override
    public void onFastOpenClicked(AbstractDashboardTableView view) {
        if (viewToModel.containsKey(view)) {
            CaseQuery query = new CaseQuery(viewToModel.get(view).query);
            query.setFrom(null);
            query.setTo(null);
            fireEvent(new IssueEvents.Show(query));
        }
    }

    private void updateSection(DashboardTableModel model){
        model.view.clearRecords();
        updateRecordsCount(model);
        requestRecords(model);
    }


    private void requestRecords(DashboardTableModel model) {
        if(model.isLoaderShow)
            model.view.showLoader(true);

        issueService.getIssues( model.query, new RequestCallback<List<CaseShortView>>() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent( new NotifyEvents.Show( lang.errGetList(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess( List<CaseShortView> caseObjects ) {
                model.view.putRecords(caseObjects);
                if(model.isLoaderShow)
                    model.view.showLoader(false);
            }
        } );
    }


    private AbstractDashboardTableView createTableView(){
        AbstractDashboardTableView table = tableProvider.get();
        table.setActivity(this);
        return table;
    }

    private void updateRecordsCount(DashboardTableModel model){
        issueService.getIssuesCount(model.query, new RequestCallback<Long>() {
            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onSuccess(Long aLong) {
                model.view.setRecordsCount(aLong.intValue());
            }
        });
    }



    @Inject
    Lang lang;

    @Inject
    IssueControllerAsync issueService;

    @Inject
    Provider<AbstractDashboardTableView> tableProvider;

    private final Set<En_ImportanceLevel> IMPORTANCE_LEVELS = Arrays.stream(En_ImportanceLevel.values()).collect(Collectors.toSet());
    private Map<AbstractDashboardTableView, DashboardTableModel> viewToModel = new HashMap<>();

}
