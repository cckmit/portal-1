package ru.protei.portal.ui.common.client.widget.selector.event;

import com.google.gwt.event.shared.GwtEvent;

public class SelectorItemSelectEvent extends GwtEvent<SelectorItemSelectHandler> {
    private static Type<SelectorItemSelectHandler> TYPE;

    public static void fire(HasSelectorItemSelectHandlers source) {
        if (TYPE != null) {
            source.fireEvent(new SelectorItemSelectEvent());
        }
    }

    public static Type<SelectorItemSelectHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<>();
        }
        return TYPE;
    }

    public final Type<SelectorItemSelectHandler> getAssociatedType() {
        return TYPE;
    }

    public String toDebugString() {
        return super.toDebugString();
    }

    protected void dispatch(SelectorItemSelectHandler handler) {
        handler.onSelectorItemSelect(this);
    }
}
