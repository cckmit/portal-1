package ru.protei.portal.ui.common.client.widget.autoresizetextarea;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.widget.imagepastetextarea.ImagePasteTextArea;

public class AutoResizeTextArea extends ImagePasteTextArea implements KeyUpHandler, HasAddHandlers {

    private final static String NEW_LINE_SYMBOL = "\n";
    private final static int INDEX_NOT_FOUND = -1;
    private final static int TEXTAREA_VALUE_CHANGE_EVENTS = Event.ONKEYUP | Event.ONCHANGE | Event.ONPASTE;
    private int minRows = 5;
    private int maxRows = 20;
    private int extraRows = 2;

    public AutoResizeTextArea() {
        super();
        addKeyUpHandler(this);
    }

    /**
     * Устанавливает минимальное количество строк, которое будет отображаться
     * @param rows Количество строк
     */
    public void setMinRows(int rows) {
        minRows = rows;
    }

    /**
     * Устанавливает максимальное количество строк, которое будет отображаться
     * @param rows Количество строк
     */
    public void setMaxRows(int rows) {
        maxRows = rows;
    }

    /**
     * Устанавливает количество пустых строк, которое будет отображаться в конце
     * @param rows Количество строк
     */
    public void setExtraRows(int rows) {
        extraRows = rows;
    }

    @Override
    protected void onAttach() {
        super.onAttach();

        getElement().getStyle().setProperty("height", "auto");
        getElement().getStyle().setProperty("maxHeight", "none");

        sinkEvents(TEXTAREA_VALUE_CHANGE_EVENTS);
    }

    @Override
    public void onBrowserEvent(Event event){
        super.onBrowserEvent(event);
        if ((DOM.eventGetType(event) & TEXTAREA_VALUE_CHANGE_EVENTS) != 0) {
            requestResize();
            fireValueChanged();
        }
    }

    @Override
    public void setValue(String value) {
        super.setValue(value);
        requestResize();
    }

    @Override
    public void setValue(String value, boolean fireEvents) {
        super.setValue(value, fireEvents);
        requestResize();
        if (fireEvents) {
            fireValueChanged();
        }
    }

    @Override
    public void setText(String text) {
        super.setText(text);
        requestResize();
    }

    public HandlerRegistration addInputHandler (InputHandler handler) {
        return addDomHandler(handler, InputEvent.getType());
    }

    public void setPlaceholder(String placeholder ) {
        getElement().setAttribute("placeholder", placeholder);
    }

    private void requestResize() {
        setVisibleLines(countLines());
    }

    private int countLines() {
        String value = getValue();
        int lines = 0;
        if (value != null) {
            int i = value.indexOf(NEW_LINE_SYMBOL);
            while (i != INDEX_NOT_FOUND) {
                lines++;
                i = value.indexOf(NEW_LINE_SYMBOL, i + 1);
            }
            lines += extraRows;
        }
        return lines;
    }

    private void fireValueChanged() {
        ValueChangeEvent.fire(this, getValue());
    }

    @Override
    public void setVisibleLines(int lines) {
        if (lines < minRows) {
            lines = minRows;
        }
        if (lines > maxRows) {
            lines = maxRows;
        }
        super.setVisibleLines(lines);
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

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }
}
