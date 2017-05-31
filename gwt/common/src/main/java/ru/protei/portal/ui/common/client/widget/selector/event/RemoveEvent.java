package ru.protei.portal.ui.common.client.widget.selector.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Событие удаления
 */
public class RemoveEvent extends GwtEvent<RemoveHandler> {
    private static Type<RemoveHandler> TYPE;

    public static <T> void fire( HasRemoveHandlers source ) {
        if(TYPE != null) {
            source.fireEvent( new RemoveEvent() );
        }
    }

    public static Type<RemoveHandler> getType() {
        if(TYPE == null) {
            TYPE = new Type();
        }
        return TYPE;
    }


    public final Type<RemoveHandler> getAssociatedType() {
        return TYPE;
    }

    public String toDebugString() {
        return super.toDebugString() ;
    }

    protected void dispatch(RemoveHandler handler) {
        handler.onRemove(this);
    }

}
