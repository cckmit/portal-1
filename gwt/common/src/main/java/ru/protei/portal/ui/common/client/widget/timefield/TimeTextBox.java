package ru.protei.portal.ui.common.client.widget.timefield;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.events.AddEvent;
import ru.protei.portal.ui.common.client.events.AddHandler;
import ru.protei.portal.ui.common.client.events.HasAddHandlers;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;

public class TimeTextBox extends ValidableTextBox implements HasTime, KeyUpHandler, HasAddHandlers {

    public TimeTextBox() {
        super();
        addKeyUpHandler(this);
    }

    @Inject
    public void init(Lang lang) {
        workTimeFormatter = new WorkTimeFormatter(lang);
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        getElement().setAttribute("autocapitalize", "off");
        getElement().setAttribute("autocorrect", "off");
        getElement().setAttribute("autocomplete", "off");
        getElement().setAttribute("placeholder", workTimeFormatter.getPlaceholder());
        setRegexp(workTimeFormatter.getPattern());
    }

    @Override
    public void setTime(Long minutes) {
        setValue( minutes == null ? "" : workTimeFormatter.asString(minutes));
    }

    @Override
    public Long getTime() {
        String value = getValue();
        if ( value.isEmpty() || !isValid()) {
            return null;
        }

        return workTimeFormatter.asTime(value);
    }

    @Override
    public void onKeyUp(KeyUpEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER && event.isControlKeyDown()) {
            event.preventDefault();
            AddEvent.fire(this);
        }
    }

    @Override
    public HandlerRegistration addAddHandler(AddHandler handler) {
        return addHandler(handler, AddEvent.getType());
    }

    WorkTimeFormatter workTimeFormatter;
}
