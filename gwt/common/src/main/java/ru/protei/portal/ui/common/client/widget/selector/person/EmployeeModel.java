package ru.protei.portal.ui.common.client.widget.selector.person;

import com.google.gwt.core.client.GWT;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.EmployeeControllerAsync;
import ru.protei.portal.ui.common.client.util.SimpleProfiler;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorModel;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Модель контактов домашней компании
 */
public abstract class EmployeeModel implements Activity, SelectorModel<PersonShortView> {

    @Event
    public void onInit( AuthEvents.Success event ) {
        myId = event.profile.getId();

//        refreshOptions( );
    }
//
//    @Event
//    public void onEmployeeListChanged( PersonEvents.ChangeEmployeeModel event ) {
//        log.info( "onEmployeeListChanged(): EmployeeModel " );
//        refreshOptions();
//    }

    @Override
    public void onSelectorLoad( SelectorWithModel<PersonShortView> selector ) {
        log.info( "onSelectorLoad(): EmployeeModel" );
        if ( selector == null ) {
            return;
        }
        log.info( "onSelectorLoad(): subscribers count="+CollectionUtils.size(subscribers) );
        subscribers.add( selector );
        if(!CollectionUtils.isEmpty( list )){
            selector.clearOptions();
            selector.fillOptions( list );
            return;
        }
        if ( selector.getValues() == null || selector.getValues().isEmpty() ) {
            refreshOptions();
        }
    }

    @Override
    public void onSelectorUnload( SelectorWithModel<PersonShortView> selector ) {
        if ( selector == null ) {
            return;
        }
        selector.clearOptions();
        subscribers.remove( selector );
        log.info( "onSelectorUnload(): subscribers count="+CollectionUtils.size(subscribers) );
    }

    public void subscribe( SelectorWithModel< PersonShortView > selector ) {
        subscribers.add( selector );
        selector.fillOptions( list );
    }

    private void notifySubscribers() {
        sp.push();
        for ( SelectorWithModel< PersonShortView > selector : subscribers ) {
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
    private boolean requested;

    private static final Logger log = Logger.getLogger( EmployeeModel.class.getName() );
    private void refreshOptions() {
        if(requested) return;
        requested = true;
        sp.start( "start" );
        employeeService.getEmployeeViewList( new EmployeeQuery( false, false, true, null, null, En_SortField.person_full_name, En_SortDir.ASC ),
                new RequestCallback< List< PersonShortView > >() {
            @Override
            public void onError( Throwable throwable ) {
                requested = false;
                fireEvent( new NotifyEvents.Show( lang.errGetList(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess( List< PersonShortView > options ) {
                requested = false;
                sp.check( "success" );
                int value = options.indexOf( new PersonShortView("", myId, false ) );
                if ( value > 0 ) {
                    options.add(0, options.remove(value));
                }

                list.clear();
                list.addAll( options );
                notifySubscribers();
                sp.check( "fillOptions refreshValue" );
                sp.stop( "done" );
            }
        } );
    }

    @Inject
    EmployeeControllerAsync employeeService;

    @Inject
    Lang lang;

    private List< PersonShortView > list = new ArrayList<>();

    Set<SelectorWithModel< PersonShortView >> subscribers = new HashSet<>();

    Long myId;
}
