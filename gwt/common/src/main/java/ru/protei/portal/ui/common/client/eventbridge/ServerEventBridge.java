package ru.protei.portal.ui.common.client.eventbridge;


import com.google.gwt.user.client.rpc.AsyncCallback;
import de.novanic.eventservice.client.event.RemoteEventService;
import de.novanic.eventservice.client.event.RemoteEventServiceFactory;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.domain.DomainFactory;
import de.novanic.eventservice.client.event.listener.RemoteEventListener;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.service.pushevent.ClientEventService;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.AuthEvents;

import java.util.logging.Logger;


/**
 * Мост между серверными событиями и клиентскими
 */
public abstract class ServerEventBridge
        implements Activity {
    /**
     * В этот момент пользователь уже залогинен, следовательно сессия валидна - можем
     * регистрировать слушатель серверных событий
     */
    @Event
    public void onAuthSuccess( AuthEvents.Success event ) {
        RemoteEventServiceFactory factory = RemoteEventServiceFactory.getInstance();
        remoteEventBus = factory.getRemoteEventService();
        remoteEventBus.addListener( serverDomain, new RemoteEventListener() {
            @Override
            public void apply( de.novanic.eventservice.client.event.Event event ) {
                dispatchEvent( event );
            }
        }, new StartListenCallback() );

        remoteEventBus.addListener( DomainFactory.USER_SPECIFIC_DOMAIN, new RemoteEventListener() {
            @Override
            public void apply( de.novanic.eventservice.client.event.Event anEvent ) {
                dispatchEvent( anEvent );
            }
        } );
    }

    /**
     * В этот момент пользователь уже разлогинен и нам надо убирать всех слушателей
     */
    @Event
    public void onAuthShow( AuthEvents.Show event ) {
        if (remoteEventBus != null) {
            remoteEventBus.removeListeners( serverDomain );
            remoteEventBus.removeListeners( DomainFactory.USER_SPECIFIC_DOMAIN );

            remoteEventBus = null;
        }
    }

    /**
     * Диспатчер внешних событий во внутренние
     *
     * @param event
     */
    private void dispatchEvent( de.novanic.eventservice.client.event.Event event ) {

        log.info( "dispatchEvent(): SubscribersChangedServerEvents.Event dispatched." + event );
        fireEvent( event );

    }

    class StartListenCallback implements AsyncCallback<Void> {
        boolean isStarted = false;

        @Override
        public void onFailure( Throwable caught ) {
            //do nothing
        }

        @Override
        public void onSuccess( Void result ) {
            if (!isStarted) {
                isStarted = true;
                fireEvent( new AppEvents.ServerEventBridgeConnected() );
            }
        }
    }


    Domain serverDomain = DomainFactory.getDomain( ClientEventService.SERVER_DOMAIN );
    RemoteEventService remoteEventBus;

    private static final Logger log = Logger.getLogger( "client_event" );
}
