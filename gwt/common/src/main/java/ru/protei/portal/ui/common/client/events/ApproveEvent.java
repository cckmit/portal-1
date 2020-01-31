package ru.protei.portal.ui.common.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class ApproveEvent extends GwtEvent<ApproveHandler> {
    private static Type<ApproveHandler> TYPE;

    public static <T> void fire( HasApproveHandlers source ) {
        if(TYPE != null) {
            source.fireEvent( new ApproveEvent() );
        }

    }

    public static Type<ApproveHandler> getType() {
        if(TYPE == null) {
            TYPE = new Type();
        }

        return TYPE;
    }

    protected ApproveEvent() {}

    public final Type<ApproveHandler> getAssociatedType() {
        return TYPE;
    }

    public String toDebugString() {
        return super.toDebugString() ;
    }

    protected void dispatch(ApproveHandler handler) {
        handler.onApprove( this );
    }
}
