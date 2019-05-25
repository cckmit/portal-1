package ru.protei.portal.ui.employee.client.activity.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.core.model.struct.WorkerEntryFacade;
import ru.protei.portal.core.model.view.EmployeeShortView;
import ru.protei.portal.core.model.view.WorkerEntryShortView;
import ru.protei.portal.ui.common.client.animation.PlateListAnimation;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.common.PeriodicTaskService;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.EmployeeControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.employee.client.activity.filter.AbstractEmployeeFilterActivity;
import ru.protei.portal.ui.employee.client.activity.filter.AbstractEmployeeFilterView;
import ru.protei.portal.ui.employee.client.activity.item.AbstractEmployeeItemActivity;
import ru.protei.portal.ui.employee.client.activity.item.AbstractEmployeeItemView;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * Активность списка сотрудников
 */
public abstract class EmployeeListActivity implements AbstractEmployeeListActivity,
        AbstractEmployeeItemActivity, AbstractEmployeeFilterActivity, Activity {

    @PostConstruct
    public void init() {
        view.setActivity( this );
        filterView.setActivity( this );
        view.getFilterContainer().add( filterView.asWidget() );
    }

    @Event
    public void onAuthSuccess ( AuthEvents.Success event ) {
        filterView.resetFilter();
    }

    @Event
    public void onInitDetails( AppEvents.InitDetails event ) {
        this.init = event;
    }

    @Event
    public void onShow( EmployeeEvents.Show event ) {
        init.parent.clear();
        init.parent.add( view.asWidget() );

        requestEmployees();
    }

    @Override
    public void onFilterChanged() {
        requestEmployees();
    }

    @Override
    public void onPreviewClicked( AbstractEmployeeItemView itemView ) {
        EmployeeShortView value = itemViewToModel.get( itemView );
        if ( value == null ) {
            return;
        }

        fireEvent( new EmployeeEvents.ShowPreview( itemView.getPreviewContainer(), value ) );
        animation.showPreview( itemView, ( IsWidget ) itemView.getPreviewContainer() );
    }

    private void requestEmployees() {

        if ( fillViewHandler != null ) {
            fillViewHandler.cancel();
        }

        view.getChildContainer().clear();
        itemViewToModel.clear();

        view.showLoader( true );
        marker = new Date().getTime();

        employeeService.getEmployees( makeQuery(), new FluentCallback< List< EmployeeShortView > >()
                .withMarkedSuccess( marker, ( m, result ) -> {
                    if ( marker == m ) {
                        fillViewHandler = taskService.startPeriodicTask( result, fillViewer, 50, 50 );
                        view.showLoader( false );
                    }
                } ) );
    }

    private EmployeeQuery makeQuery() {
        return new EmployeeQuery( false, false, true,
                null,
                filterView.searchPattern().getValue(),
                filterView.workPhone().getValue(),
                filterView.mobilePhone().getValue(),
                filterView.ipAddress().getValue(),
                filterView.sortField().getValue(),
                filterView.sortDir().getValue()? En_SortDir.ASC: En_SortDir.DESC );
    }

    private AbstractEmployeeItemView makeView( EmployeeShortView employee ) {
        AbstractEmployeeItemView itemView = factory.get();
        itemView.setActivity( this );

        itemView.setName( employee.getDisplayName() );
        itemView.setBirthday( DateFormatter.formatDateMonth( employee.getBirthday() ) );

        PlainContactInfoFacade infoFacade = new PlainContactInfoFacade( employee.getContactInfo() );
        itemView.setPhone( infoFacade.publicPhonesAsString() );
        itemView.setEmail( infoFacade.publicEmailsAsString() );

        WorkerEntryFacade entryFacade = new WorkerEntryFacade( employee.getWorkerEntries() );
        WorkerEntryShortView mainEntry = entryFacade.getMainEntry();
        if ( mainEntry != null ) {
            itemView.setDepartment( mainEntry.getDepartmentName() );
            itemView.setPosition( mainEntry.getPositionName() );
        }

        itemView.setPhoto(UiConstants.LOAD_AVATAR_URL + employee.getId() + ".jpg" );

        return itemView;
    }

    Consumer< EmployeeShortView > fillViewer = new Consumer< EmployeeShortView >() {
        @Override
        public void accept( EmployeeShortView employee ) {
            AbstractEmployeeItemView itemView = makeView( employee );

            itemViewToModel.put( itemView, employee );
            view.getChildContainer().add( itemView.asWidget() );
        }
    };

    @Inject
    PlateListAnimation animation;

    @Inject
    AbstractEmployeeListView view;

    @Inject
    AbstractEmployeeFilterView filterView;

    @Inject
    Provider< AbstractEmployeeItemView > factory;

    @Inject
    PeriodicTaskService taskService;

    @Inject
    EmployeeControllerAsync employeeService;

    @Inject
    Lang lang;

    private long marker;
    private PeriodicTaskService.PeriodicTaskHandler fillViewHandler;
    private AppEvents.InitDetails init;
    private Map< AbstractEmployeeItemView, EmployeeShortView > itemViewToModel = new HashMap<>();
    private static final Logger log = Logger.getLogger(EmployeeListActivity.class.getName());
}
