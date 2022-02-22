package ru.protei.portal.ui.employee.client.activity.list;

import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.view.EmployeeShortView;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerActivity;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerView;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.EmployeeEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.EmployeeControllerAsync;
import ru.protei.portal.ui.common.client.widget.viewtype.ViewType;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.protei.portal.ui.common.client.util.PaginationUtils.PAGE_SIZE;
import static ru.protei.portal.ui.common.client.util.PaginationUtils.getTotalPages;

public abstract class EmployeeTableActivity implements AbstractEmployeeTableActivity, AbstractPagerActivity, Activity {
    @PostConstruct
    public void init() {
        view.setActivity(this);
        view.setAnimation(animation);
        pagerView.setActivity(this);
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails event) {
        this.init = event;
    }

    @Event
    public void onShow(EmployeeEvents.ShowDefinite event) {
        if(event.viewType != ViewType.TABLE) {
            return;
        }

        view.getFilterContainer().clear();
        view.getPagerContainer().clear();
        init.parent.clear();

        init.parent.add(view.asWidget());
        view.getPagerContainer().add(pagerView.asWidget());
        view.getFilterContainer().add(event.filter);

        this.query = event.query;
        this.preScroll = event.preScroll;

        requestEmployees(this.page);
    }

    @Event
    public void onFilterChange(EmployeeEvents.UpdateData event) {
        if(event.viewType != ViewType.TABLE)
            return;

        this.query = event.query;

        requestEmployees(0);
    }

    @Event
    public void onUpdate(EmployeeEvents.UpdateDefinite event) {
        if(event.viewType != ViewType.TABLE)
            return;

        if (!model.contains(event.id) && query.getAbsent()) {
            fireEvent(new EmployeeEvents.Show(true));
            return;
        }

        if (!model.contains(event.id)) {
            return;
        }

        employeeService.getEmployee(event.id, new FluentCallback<EmployeeShortView>()
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
        this.page = page;
        requestEmployees(this.page);
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

    private void showPreview(EmployeeShortView value) {
        if (value == null) {
            animation.closeDetails();
        } else {
            animation.showDetails();
            fireEvent(new EmployeeEvents.ShowPreview(view.getPreviewContainer(), value));
        }
    }

    private void requestEmployees( int page ) {

        view.clearRecords();
        animation.closeDetails();

        boolean isFirstChunk = page == 0;
        marker = new Date().getTime();

        query.setOffset( page*PAGE_SIZE );
        query.setLimit( PAGE_SIZE );

        employeeService.getEmployees( query, new FluentCallback< SearchResult< EmployeeShortView > >()
                .withMarkedSuccess( marker, ( m, r ) -> {
                    if ( marker == m ) {
                        if ( isFirstChunk ) {
                            pagerView.setTotalCount( r.getTotalCount() );
                            pagerView.setTotalPages( getTotalPages( r.getTotalCount() ) );
                        }
                        pagerView.setCurrentPage( page );
                        view.addRecords( r.getResults() );
                        updateModel( r.getResults() );
                        restoreScroll();
                    }
                } ) );
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

    private void updateModel(List<EmployeeShortView> result) {
        model.clear();
        model.addAll(result.stream().map(EmployeeShortView::getId).collect(Collectors.toSet()));
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

    private long marker;
    private int page = 0;

    private Set<Long> model = new HashSet<>();
}
