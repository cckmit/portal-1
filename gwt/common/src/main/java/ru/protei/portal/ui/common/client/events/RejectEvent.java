package ru.protei.portal.ui.common.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class RejectEvent extends GwtEvent<RejectHandler> {
    private static Type<RejectHandler> TYPE;

    public static void fire(HasRejectHandlers source) {
        if (TYPE != null) {
            source.fireEvent(new RejectEvent());
        }
    }

    public static Type<RejectHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<>();
        }

        return TYPE;
    }

    @Override
    public Type<RejectHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(RejectHandler handler) {
        handler.onReject(this);
    }
}
