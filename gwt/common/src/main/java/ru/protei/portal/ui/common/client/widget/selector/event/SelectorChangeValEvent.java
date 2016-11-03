package ru.protei.portal.ui.common.client.widget.selector.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Событие изменения поля ввода селектора (search field change)
 */
public class SelectorChangeValEvent extends GwtEvent<SelectorChangeValHandler> {
    private static Type<SelectorChangeValHandler> TYPE;

    public static <T> void fire( HasSelectorChangeValHandlers source, String searchText) {
        if(TYPE != null) {
            source.fireEvent( new SelectorChangeValEvent(searchText) );
        }
    }

    public static Type<SelectorChangeValHandler> getType() {
        if(TYPE == null) {
            TYPE = new Type();
        }
        return TYPE;
    }

    protected SelectorChangeValEvent(String searchText) {
        this.searchText = searchText;
    }

    public String getValue() {
        return searchText;
    }

    public final Type<SelectorChangeValHandler> getAssociatedType() {
        return TYPE;
    }

    public String toDebugString() {
        return super.toDebugString() ;
    }

    protected void dispatch(SelectorChangeValHandler handler) {
        handler.onChange(this);
    }

    private String searchText;
}
