package ru.protei.portal.ui.common.client.widget.platelist.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by shagaleev on 10/21/16.
 */
public class AddEvent extends GwtEvent<AddHandler> {
    private static Type<AddHandler> TYPE;

    public static <T> void fire( HasAddHandlers source ) {
        if(TYPE != null) {
            source.fireEvent( new AddEvent() );
        }

    }

    public static Type<AddHandler> getType() {
        if(TYPE == null) {
            TYPE = new Type();
        }

        return TYPE;
    }

    protected AddEvent() {}

    public final Type<AddHandler> getAssociatedType() {
        return TYPE;
    }

    public String toDebugString() {
        return super.toDebugString() ;
    }

    protected void dispatch(AddHandler handler) {
        handler.onAdd( this );
    }
}
