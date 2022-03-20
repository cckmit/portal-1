package ru.protei.portal.ui.employee.client.activity.list;

import com.google.gwt.i18n.client.TimeZone;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.core.model.struct.WorkerEntryFacade;
import ru.protei.portal.core.model.view.EmployeeShortView;
import ru.protei.portal.core.model.view.WorkerEntryShortView;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerActivity;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.common.EmailRender;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.EmployeeEvents;
import ru.protei.portal.ui.common.client.util.AvatarUtils;
import ru.protei.portal.ui.common.client.service.EmployeeControllerAsync;
import ru.protei.portal.ui.common.client.util.LinkUtils;
import ru.protei.portal.ui.common.client.widget.viewtype.ViewType;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.employee.client.activity.item.AbstractEmployeeItemActivity;
import ru.protei.portal.ui.employee.client.activity.item.AbstractEmployeeItemView;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;

import static ru.protei.portal.ui.common.client.util.PaginationUtils.PAGE_SIZE;
import static ru.protei.portal.ui.common.client.util.PaginationUtils.getTotalPages;

/**
 * Активность списка сотрудников
 */
public abstract class EmployeeListActivity implements AbstractEmployeeListActivity,
        AbstractEmployeeItemActivity, AbstractPagerActivity, Activity {

    @PostConstruct
    public void init() {
        view.setActivity( this );
        pagerView.setActivity( this );
    }

    @Event
    public void onInitDetails( AppEvents.InitDetails event ) {
        this.init = event;
    }

    @Event
    public void onShow(  EmployeeEvents.ShowDefinite event ) {
        if(event.viewType != ViewType.LIST)
            return;

        view.getFilterContainer().clear();
        view.getPagerContainer().clear();
        init.parent.clear();

        init.parent.add(view.asWidget());
        view.getPagerContainer().add(pagerView.asWidget());
        view.getFilterContainer().add(event.filter);

        this.query = event.query;

        requestEmployees(0);
    }

    @Event
    public void onFilterChange(EmployeeEvents.UpdateData event) {
        if(event.viewType != ViewType.LIST)
            return;

        this.query = event.query;

        requestEmployees(0);
    }

    @Event
    public void onUpdate(EmployeeEvents.UpdateDefinite event) {
        if(event.viewType != ViewType.LIST)
            return;

        employeeService.getEmployee(event.id, new FluentCallback<EmployeeShortView>()
                .withSuccess(employee -> {

                    AbstractEmployeeItemView itemView = modelToItemView.get(employee);

                    if (itemView == null && query.getAbsent()) {
                        fireEvent(new EmployeeEvents.Show());
                        return;
                    }

                    if (itemView == null) {
                        return;
                    }

                    if (employee.getCurrentAbsence() == null && query.getAbsent()) {
                        view.getChildContainer().remove(itemView.asWidget());
                        modelToItemView.remove(employee);
                        return;
                    }

                    itemView.setCurrentAbsence(employee.getCurrentAbsence());
                }));
    }

    @Override
    public void onPageSelected( int page ) {
        pagerView.setCurrentPage( page );
        requestEmployees( page );
    }

    private void requestEmployees( int page ) {

        view.getChildContainer().clear();
        view.showLoader( true );
        modelToItemView.clear();

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
                        r.getResults().forEach( fillViewer );
                        view.showLoader( false );
                    }
                } ) );
    }

    private AbstractEmployeeItemView makeView( EmployeeShortView employee ) {
        AbstractEmployeeItemView itemView = factory.get();
        itemView.setActivity( this );

        itemView.setName( employee.getDisplayName(), LinkUtils.makePreviewLink(EmployeeShortView.class, employee.getId()) );
        if (policyService.hasPrivilegeFor(En_Privilege.EMPLOYEE_EDIT)) {
            itemView.setEditIcon(LinkUtils.makeEditLink(EmployeeShortView.class, employee.getId()));
        }

        TimeZone timeZone = null;
        if (employee.getTimezoneOffset() != null){
            timeZone = TimeZone.createTimeZone(employee.getTimezoneOffset());
        }
        itemView.setBirthday( DateFormatter.formatDateMonth( employee.getBirthday(), timeZone ) );

        PlainContactInfoFacade infoFacade = new PlainContactInfoFacade( employee.getContactInfo() );
        itemView.setPhone( infoFacade.publicPhonesAsFormattedString(true) );
        itemView.setEmail( EmailRender.renderToHtml(infoFacade.publicEmailsStream()) );

        WorkerEntryFacade entryFacade = new WorkerEntryFacade( employee.getWorkerEntries() );
        WorkerEntryShortView mainEntry = entryFacade.getMainEntry();
        if ( mainEntry != null ) {
            if ( mainEntry.getDepartmentParentName() != null ) {
                itemView.setDepartmentParent( mainEntry.getDepartmentParentName() );
                itemView.setDepartment( mainEntry.getDepartmentName() );
            } else {
                itemView.setDepartmentParent( mainEntry.getDepartmentName() );
            }

            itemView.setPosition( mainEntry.getPositionName() );
            itemView.setCompany( mainEntry.getCompanyName() );
        }
        itemView.setPhoto(AvatarUtils.getPhotoUrl(employee.getId()));
        itemView.setIP(employee.getIpAddress());
        if(employee.isFired()) {
            itemView.setFireDate(DateFormatter.formatDateOnly(employee.getFireDate()));
        }
        if(policyService.hasPrivilegeFor(En_Privilege.ABSENCE_VIEW)) {
            itemView.setCurrentAbsence(employee.getCurrentAbsence());
        }
        return itemView;
    }

    Consumer< EmployeeShortView > fillViewer = new Consumer< EmployeeShortView >() {
        @Override
        public void accept( EmployeeShortView employee ) {
            AbstractEmployeeItemView itemView = makeView( employee );
            modelToItemView.put( employee, itemView );
            view.getChildContainer().add( itemView.asWidget() );
        }
    };

    @Inject
    AbstractEmployeeListView view;
    @Inject
    Provider< AbstractEmployeeItemView > factory;
    @Inject
    EmployeeControllerAsync employeeService;
    @Inject
    AbstractPagerView pagerView;
    @Inject
    PolicyService policyService;

    private long marker;
    private AppEvents.InitDetails init;
    private EmployeeQuery query;
    private Map< EmployeeShortView, AbstractEmployeeItemView > modelToItemView = new HashMap<>();
    private static final Logger log = Logger.getLogger(EmployeeListActivity.class.getName());
}
