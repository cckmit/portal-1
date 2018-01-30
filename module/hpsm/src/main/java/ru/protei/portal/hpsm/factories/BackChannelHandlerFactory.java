package ru.protei.portal.hpsm.factories;

import ru.protei.portal.core.event.CompleteCaseEvent;
import ru.protei.portal.hpsm.handlers.BackChannelEventHandler;
import ru.protei.portal.hpsm.struct.HpsmMessage;

/**
 * Created by michael on 15.05.17.
 */
public interface BackChannelHandlerFactory {

    BackChannelEventHandler createHandler (HpsmMessage currentState, CompleteCaseEvent event);

}
