package ru.protei.portal.ui.crm.client.activity.dashboard;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.ui.common.client.common.IssueStates;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.IssueEvents;
import ru.protei.portal.ui.common.client.service.IssueServiceAsync;
import ru.protei.portal.ui.common.shared.model.Profile;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.crm.client.events.DashboardEvents;

import java.util.*;
import java.util.function.LongConsumer;
import java.util.stream.Collectors;

/**
 * Created by bondarenko on 01.12.16.
 */
public abstract class DashboardActivity implements AbstractDashboardActivity, Activity{

    @Inject
    public void onInit(){
        view.setActivity(this);

        Set<En_ImportanceLevel> importanceLevels = Arrays.stream(En_ImportanceLevel.values()).collect(Collectors.toSet());
        view.getActiveRecordsImportance().setValue(importanceLevels);
        view.getNewRecordsImportance().setValue(importanceLevels);
        view.getInactiveRecordsImportance().setValue(importanceLevels);
    }

    @Event
    public void onAuthSuccess( AuthEvents.Success event ) {
        profile = event.profile;

        activeRecordsQuery = generateActiveRecordsQuery();
        newRecordsQuery = generateNewRecordsQuery();
        inactiveRecordsQuery = generateInactiveRecordsQuery();
    }

    @Event
    public void onChangeIssues( IssueEvents.ChangeModel event ) {
        onShow(null);
    }

    @Event
    public void onShow( DashboardEvents.Show event ) {
        initDetails.parent.clear();
        initDetails.parent.add( view.asWidget() );

        initActiveRecords();
        initNewRecords();
        initInactiveRecords();
    }

    @Event
    public void onInitDetails( AppEvents.InitDetails initDetails ) {
        this.initDetails = initDetails;
    }

    @Override
    public void updateActiveRecordsImportance(Set<En_ImportanceLevel> importanceLevels) {
        updateRecordsImportance(activeRecordsQuery, importanceLevels);
        initActiveRecords();
    }

    @Override
    public void updateNewRecordsImportance(Set<En_ImportanceLevel> importanceLevels) {
        updateRecordsImportance(newRecordsQuery, importanceLevels);
        initNewRecords();
    }

    @Override
    public void updateInactiveRecordsImportance(Set<En_ImportanceLevel> importanceLevels) {
        updateRecordsImportance(inactiveRecordsQuery, importanceLevels);
        initInactiveRecords();
    }

    private void updateRecordsImportance(CaseQuery query, Set<En_ImportanceLevel> importanceLevels){
        query.setImportanceIds(
                importanceLevels
                        .stream()
                        .map(En_ImportanceLevel::getId)
                        .collect(Collectors.toList())
        );
    }

    private void initTable(CaseQuery query, LongConsumer issueCountSetter, IssueEvents.ShowCustom event){
        issueService.getIssuesCount(query, new RequestCallback<Long>() {
            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onSuccess(Long aLong) {
                issueCountSetter.accept(aLong);
            }
        });

        fireEvent(event);
    }

    private void initActiveRecords(){
        initTable(activeRecordsQuery, view::setActiveRecordsCount, new IssueEvents.ShowCustom(activeRecordsQuery, view.getActiveRecordsContainer()));
    }

    private void initNewRecords(){
        initTable(newRecordsQuery, view::setNewRecordsCount, new IssueEvents.ShowCustom(newRecordsQuery, view.getNewRecordsContainer()));
    }

    private void initInactiveRecords(){
        view.showInactiveRecordsLoader(true);
        initTable(inactiveRecordsQuery, view::setInactiveRecordsCount,
                new IssueEvents.ShowCustom(
                        inactiveRecordsQuery,
                        view.getInactiveRecordsContainer(),
                        () -> view.showInactiveRecordsLoader(false)
                )
        );
    }

    private CaseQuery generateActiveRecordsQuery(){
        CaseQuery query = new CaseQuery(En_CaseType.CRM_SUPPORT, null, En_SortField.creation_date, En_SortDir.DESC);
        query.setManagerId(profile.getId());
        query.setStates(issueStates.getActiveStates());

        return query;
    }

    private CaseQuery generateNewRecordsQuery(){
        CaseQuery query = new CaseQuery(En_CaseType.CRM_SUPPORT, null, En_SortField.creation_date, En_SortDir.ASC);
        query.setStates(Collections.singletonList(En_CaseState.CREATED));
        query.setManagerId(-1L);

        return query;
    }

    private CaseQuery generateInactiveRecordsQuery(){
        CaseQuery query = new CaseQuery(En_CaseType.CRM_SUPPORT, null, En_SortField.last_update, En_SortDir.ASC);
        query.setManagerId(profile.getId());

        List<En_CaseState> inactiveStates = new ArrayList<>(issueStates.getInactiveStates());
        inactiveStates.remove(En_CaseState.VERIFIED);

        query.setStates(inactiveStates);

        return query;
    }

    @Inject
    AbstractDashboardView view;

    @Inject
    IssueStates issueStates;

    @Inject
    IssueServiceAsync issueService;

    private AppEvents.InitDetails initDetails;
    private Profile profile;

    private CaseQuery activeRecordsQuery;
    private CaseQuery newRecordsQuery;
    private CaseQuery inactiveRecordsQuery;
}
