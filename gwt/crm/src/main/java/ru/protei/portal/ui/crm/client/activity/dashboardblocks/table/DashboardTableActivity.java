package ru.protei.portal.ui.crm.client.activity.dashboardblocks.table;

import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.core.model.view.PersonShortView;
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
        AbstractDashboardTableView view = createTableView(event.debugId);

        event.parent.clear();
        event.parent.add(view.asWidget());


        DashboardTableModel model = new DashboardTableModel(view, event.query, event.isLoaderShow, event.daysLimit);
        viewToModel.put(view, model);

        view.getImportance().setValue(IMPORTANCE_LEVELS);
        view.setSectionName(event.sectionName);
        view.getSearch().setValue(model.query.getSearchCasenoString());
        view.toggleSearchIndicator(model.query.getSearchCasenoString() != null && !model.query.getSearchCasenoString().isEmpty());
        view.toggleInitiatorsIndicator(model.query.getInitiatorIds() != null && model.query.getInitiatorIds().size() > 0);

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

    @Override
    public void onSearchChanged(AbstractDashboardTableView view, String search) {
        DashboardTableModel model = viewToModel.get(view);

        if (model == null) {
            return;
        }

        model.query.setSearchCasenoString(search);
        view.toggleSearchIndicator(search != null && !search.isEmpty());

        updateSection(model);
    }

    @Override
    public void onInitiatorSelected(AbstractDashboardTableView view, PersonShortView person) {
        DashboardTableModel model = viewToModel.get(view);

        if (model == null) {
            return;
        }

        model.query.setInitiatorIds(person == null ? null : Collections.singletonList(person.getId()));
        view.toggleInitiatorsIndicator(person != null);

        updateSection(model);
    }

    private void updateSection(DashboardTableModel model){
        model.view.clearRecords();
        updateRecordsCount(model);
        requestRecords(model);
    }


    private void requestRecords(DashboardTableModel model) {
        if(model.isLoaderShow)
            model.view.showLoader(true);

        if (model.daysLimit != null) {
            Date to = new Date();
            Date from = new Date(to.getTime() - (MILLISECONDS_PER_DAY * model.daysLimit));
            model.query.setFrom(from);
            model.query.setTo(to);
        }

        issueService.getIssues( model.query, new RequestCallback<List<CaseShortView>>() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent( new NotifyEvents.Show( lang.errGetList(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess( List<CaseShortView> caseObjects ) {
                model.view.putRecords(caseObjects);
                model.view.putPersons(caseObjects.stream()
                        .filter(caseObject -> caseObject.getInitiatorId() != null && caseObject.getInitiatorShortName() != null)
                        .map(caseObject -> new PersonShortView(caseObject.getInitiatorShortName(), caseObject.getInitiatorId(), false))
                        .distinct()
                        .collect(Collectors.toList())
                );
                if(model.isLoaderShow)
                    model.view.showLoader(false);
            }
        } );
    }


    private AbstractDashboardTableView createTableView(String debugId){
        AbstractDashboardTableView table = tableProvider.get();
        table.setActivity(this);
        if (debugId != null) {
            table.setEnsureDebugId(debugId);
        }
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

    private final static Long MILLISECONDS_PER_DAY = 86400000L;
    private final Set<En_ImportanceLevel> IMPORTANCE_LEVELS = Arrays.stream(En_ImportanceLevel.values()).collect(Collectors.toSet());
    private Map<AbstractDashboardTableView, DashboardTableModel> viewToModel = new HashMap<>();

}
