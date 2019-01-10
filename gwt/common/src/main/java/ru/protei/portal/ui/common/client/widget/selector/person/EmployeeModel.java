package ru.protei.portal.ui.common.client.widget.selector.person;

import com.google.gwt.core.client.GWT;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.PersonEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.EmployeeControllerAsync;
import ru.protei.portal.ui.common.client.util.SimpleProfiler;
import ru.protei.portal.ui.common.client.widget.selector.base.HasSelectableValues;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorModel;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Модель контактов домашней компании
 */
public abstract class EmployeeModel implements Activity, SelectorModel<PersonShortView> {

    @Event
    public void onInit( AuthEvents.Success event ) {
        myId = event.profile.getId();

//        refreshOptions( );
        for (ModelSelector< PersonShortView > subscriber : subscribers) {
            subscriber.clearOptions();
        }
    }

    @Event
    public void onEmployeeListChanged( PersonEvents.ChangeEmployeeModel event ) {
        log.info( "onEmployeeListChanged(): EmployeeModel " );
        refreshOptions();
    }

    @Override
    public void onSelectorLoad( HasSelectableValues<PersonShortView> selector ) {
        log.info( "onSelectorLoad(): EmployeeModel" );
        if ( selector == null ) {
            return;
        }
        if ( selector.getValues() == null || selector.getValues().isEmpty() ) {
            refreshOptions();
        }
    }

    public void subscribe( ModelSelector< PersonShortView > selector ) {
        subscribers.add( selector );
        selector.fillOptions( list );
    }

    private void notifySubscribers() {
        sp.push();
        for ( ModelSelector< PersonShortView > selector : subscribers ) {
            selector.fillOptions( list );
//            sp.check( "fillOptions" );
            selector.refreshValue();
            sp.check( "fillOptions refreshValue" );
        }
        sp.pop();
        sp.check( "notifySubscribers" );
    }

SimpleProfiler sp = new SimpleProfiler( SimpleProfiler.ON, ( message, currentTime ) -> {
    GWT.log("EmployeeModel "+ message+ " t: " + currentTime);});

    private static final Logger log = Logger.getLogger( EmployeeModel.class.getName() );
    private void refreshOptions() {
//        long start = System.currentTimeMillis();
//        log.info( "refreshOptions(): EmployeeModel start " );
        sp.start( "start" );
        employeeService.getEmployeeViewList( new EmployeeQuery( false, false, true, null, null, En_SortField.person_full_name, En_SortDir.ASC ),
                new RequestCallback< List< PersonShortView > >() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent( new NotifyEvents.Show( lang.errGetList(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess( List< PersonShortView > options ) {
                sp.check( "success" );
//        log.info( "refreshOptions(): EmployeeModel success " + (start - System.currentTimeMillis() ) );
                int value = options.indexOf( new PersonShortView("", myId, false ) );
                if ( value > 0 ) {
                    options.add(0, options.remove(value));
                }

                list.clear();
                list.addAll( options );
                notifySubscribers();
//        log.info( "refreshOptions(): EmployeeModel done " + (start - System.currentTimeMillis() ) );
                sp.stop( "done" );
            }
        } );
    }

    @Inject
    EmployeeControllerAsync employeeService;

    @Inject
    Lang lang;

    private List< PersonShortView > list = new ArrayList<>();

    List< ModelSelector< PersonShortView > > subscribers = new ArrayList<>();

    Long myId;
}
