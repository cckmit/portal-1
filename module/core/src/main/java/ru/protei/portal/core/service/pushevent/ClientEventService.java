package ru.protei.portal.core.service.pushevent;

import de.novanic.eventservice.client.event.Event;

/**
 * Бин, отвечающий за отправку сообщений в клиента
 */
public interface ClientEventService {

    /**
     * Отправляет событие всем клиентам
     * @param event    событие
     */
    void fireEvent( Event event );

    /**
     * Отправляет событие в конкретный клиент
     */
    void fireEvent( String clientId, Event event );

    String SERVER_DOMAIN = "server";
    String CLIENT_DOMAIN = "client";
}
