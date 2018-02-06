package ru.protei.portal.redmine.factories;

import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.redmine.handlers.BackchannelEventHandler;

public class BackchannelHandlerFactoryImpl implements BackchannelHandlerFactory {

    @Override
    public BackchannelEventHandler createHandler(AssembledCaseEvent event) {
        return new CreateIssueHandler();
    }

    public class CreateIssueHandler implements BackchannelEventHandler {
        @Override
        public void handle() {

        }
    }

    public class UpdateIssueHandler implements BackchannelEventHandler {
        @Override
        public void handle() {

        }
    }

    public class DeleteIssueHandler implements BackchannelEventHandler {
        @Override
        public void handle() {

        }
    }
}
