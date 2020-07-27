package ru.protei.portal.core.model.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 *
 */
public class CaseCommentClientStEvent {

    public static class Event
            extends GwtEvent<Handler>
            implements de.novanic.eventservice.client.event.Event {

        public Event() {
        }

        @Override
        public Type<Handler> getAssociatedType() {
            return TYPE;
        }

        @Override
        protected void dispatch( Handler eventHandler ) {
            eventHandler.onSubscribersChanged( personId, caseObjectId );
        }


        public Long getCaseObjectId() {
            return caseObjectId;
        }


        public Long getPersonId() {
            return personId;
        }

        private Long caseObjectId;
        //    private CaseComment caseComment;
        private Long personId;

        @Override
        public String toString() {
            return "CaseCommentClientEvent{" +
                    "caseObjectId=" + caseObjectId +
                    ", personId=" + personId +
//                ", newCaseComment=" + asString( caseComment ) +
                    '}';
        }

    }

    public static interface Handler extends EventHandler {
        void onSubscribersChanged( Long personId, Long caseObjectId );
    }

    public static GwtEvent.Type<Handler> TYPE = new GwtEvent.Type<Handler>();

}
