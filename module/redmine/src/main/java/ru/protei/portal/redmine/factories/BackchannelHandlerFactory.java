package ru.protei.portal.redmine.factories;

import ru.protei.portal.redmine.handlers.BackchannelEventHandler;

public interface BackchannelHandlerFactory {
    BackchannelEventHandler createHandler();
}
