package ru.protei.portal.ui.common.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class CorrectDecimalNumbersEvent extends GwtEvent<CorrectDecimalNumbersHandler> {

    private static GwtEvent.Type<CorrectDecimalNumbersHandler> TYPE;

    public static void fire(HasCorrectDecimalNumbersHandlers source) {
        if (TYPE != null) {
            source.fireEvent(new CorrectDecimalNumbersEvent());
        }
    }

    public static GwtEvent.Type<CorrectDecimalNumbersHandler> getType() {
        if (TYPE == null) {
            TYPE = new GwtEvent.Type<>();
        }

        return TYPE;
    }

    @Override
    public final Type<CorrectDecimalNumbersHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(CorrectDecimalNumbersHandler handler) {
        handler.onCorrect(this);
    }

}
