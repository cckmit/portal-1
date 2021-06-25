package ru.protei.portal.ui.common.client.events.clone;

import com.google.gwt.event.shared.GwtEvent;

public class CloneEvent extends GwtEvent<CloneHandler> {
    private static Type<CloneHandler> TYPE;
    public Long id;

    public static <T> void fire(HasCloneHandlers source, Long id) {
        if(TYPE != null) {
            source.fireEvent(new CloneEvent(id));
        }
    }

    public static Type<CloneHandler> getType() {
        if(TYPE == null) {
            TYPE = new Type();
        }

        return TYPE;
    }

    public CloneEvent(Long id){
        this.id = id;
    }

    @Override
    public Type<CloneHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(CloneHandler handler) {
        handler.onClone(this);
    }
}
