package ru.protei.portal.ui.employee.client.activity.list;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.view.EmployeeShortView;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerActivity;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerView;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.EmployeeEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.EmployeeControllerAsync;
import ru.protei.portal.ui.common.client.widget.viewtype.ViewType;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class EmployeeTableActivity implements AbstractEmployeeTableActivity, AbstractPagerActivity, Activity {
    @PostConstruct
    public void init() {
        view.setActivity( this );
        view.setAnimation( animation );
        pagerView.setActivity( this );
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails event) {
        this.init = event;
    }

    @Event
    public void onShow( EmployeeEvents.ShowDefinite event ) {
        if(event.viewType != ViewType.TABLE) {
            return;
        }

        view.getFilterContainer().clear();
        view.getPagerContainer().clear();
        init.parent.clear();

        init.parent.add( view.asWidget() );
        view.getPagerContainer().add( pagerView.asWidget() );
        view.getFilterContainer().add(event.filter);

        this.query = event.query;
        this.preScroll = event.preScroll;

        loadTable();
    }

    @Event
    public void onFilterChange( EmployeeEvents.UpdateData event ) {
        if(event.viewType != ViewType.TABLE)
            return;

        this.query = event.query;
        loadTable();
    }

    @Event
    public void onUpdate(EmployeeEvents.UpdateDefinite event) {
        if(event.viewType != ViewType.TABLE)
            return;

        if (!model.contains(event.id)) {
            fireEvent(new EmployeeEvents.Show(true));
            return;
        }

        employeeService.getEmployeeWithChangedHiddenCompanyNames(event.id, new FluentCallback<EmployeeShortView>()
                .withSuccess(employee -> {

                    if (employee.getCurrentAbsence() == null && query.getAbsent()) {
                        view.removeRow(employee);
                        model.remove(employee.getId());
                        animation.closeDetails();
                        return;
                    }

                    view.updateRow(employee);
                }));

    }

    @Override
    public void onPageSelected(int page) {
        view.scrollTo(page);
    }

    @Override
    public void onPageChanged(int page) {
        pagerView.setCurrentPage(page);
    }

    @Override
    public void onItemClicked(EmployeeShortView value) {
        persistScroll();
        showPreview(value);
    }

    @Override
    public void onEditClicked(EmployeeShortView value) {
        persistScroll();
        fireEvent(new EmployeeEvents.Edit(value.getId()));
    }

    private void showPreview (EmployeeShortView value ) {

        if ( value == null ) {
            animation.closeDetails();
        } else {
            animation.showDetails();
            fireEvent(new EmployeeEvents.ShowPreview(view.getPreviewContainer(), value,true));
        }
    }

    @Override
    public void loadData( int offset, int limit, AsyncCallback<List<EmployeeShortView>> asyncCallback ) {
        boolean isFirstChunk = offset == 0;
        query.setOffset(offset);
        query.setLimit(limit);
        query.setSortField(En_SortField.person_full_name);
        query.setSortDir(En_SortDir.ASC);

        employeeService.getEmployeesWithChangedHiddenCompanyNames(query, new FluentCallback<SearchResult<EmployeeShortView>>()
                .withError(throwable -> {
                    fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                    asyncCallback.onFailure(throwable);
                })
                .withSuccess(sr -> {

                    asyncCallback.onSuccess(sr.getResults());

                    model.clear();
                    model.addAll(sr.getResults().stream().map(EmployeeShortView::getId).collect(Collectors.toSet()));

                    if (isFirstChunk) {
                        view.setTotalRecords(sr.getTotalCount());
                        pagerView.setTotalPages(view.getPageCount());
                        pagerView.setTotalCount(sr.getTotalCount());
                        restoreScroll();
                    }
                }));
    }

    private void loadTable() {
        animation.closeDetails();
        view.clearRecords();
        view.triggerTableLoad();
    }

    private void persistScroll() {
        scrollTo = Window.getScrollTop();
    }

    private void restoreScroll() {
        if (!preScroll) {
            view.clearSelection();
            return;
        }

        Window.scrollTo(0, scrollTo);
        preScroll = false;
        scrollTo = 0;
    }

    @Inject
    AbstractEmployeeTableView view;
    @Inject
    TableAnimation animation;
    @Inject
    Lang lang;
    @Inject
    AbstractPagerView pagerView;
    @Inject
    EmployeeControllerAsync employeeService;

    private AppEvents.InitDetails init;
    private EmployeeQuery query;

    private Integer scrollTo = 0;
    private Boolean preScroll = false;
    private Set<Long> model = new HashSet<>();
}
