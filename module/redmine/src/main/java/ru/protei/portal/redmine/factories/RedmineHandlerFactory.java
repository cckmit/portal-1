package ru.protei.portal.redmine.factories;

import ru.protei.portal.redmine.handlers.RedmineEventHandler;

public interface RedmineHandlerFactory {
    RedmineEventHandler createHandler();
}
