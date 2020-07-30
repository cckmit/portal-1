package ru.protei.portal.core.service.pushevent;

import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.domain.DomainFactory;
import de.novanic.eventservice.service.EventExecutorService;
import de.novanic.eventservice.service.EventExecutorServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

/**
 * Реализация бина, отвечающего за отправку сообщений в клиента
 */
public class ClientEventServiceImpl implements ClientEventService {
    @PostConstruct
    public void onInit() {
        serverDomain = DomainFactory.getDomain( ClientEventService.SERVER_DOMAIN );

        EventExecutorServiceFactory factory = EventExecutorServiceFactory.getInstance();
        eventService = factory.getEventExecutorService( ClientEventService.CLIENT_DOMAIN );
    }

    @Override
    public void fireEvent( Event event ) {
        log.info( "fireEvent(): {}", event );
        eventService.addEvent( serverDomain, event );
    }

    @Override
    public void fireEvent( String clientId, Event event ) {
        log.info( "Fire event {} -> {}", event.toString(), clientId );

        EventExecutorServiceFactory factory = EventExecutorServiceFactory.getInstance();
        EventExecutorService clientEventService = factory.getEventExecutorService( clientId );

        clientEventService.addEventUserSpecific( event );
    }

    Domain serverDomain;
    EventExecutorService eventService;
    private final static Logger log = LoggerFactory.getLogger( ClientEventServiceImpl.class );

}
