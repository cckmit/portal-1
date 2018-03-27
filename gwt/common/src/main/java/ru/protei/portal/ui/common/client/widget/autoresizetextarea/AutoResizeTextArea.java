package ru.protei.portal.ui.common.client.widget.autoresizetextarea;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.TextArea;

import java.util.ArrayList;
import java.util.List;

public class AutoResizeTextArea extends TextArea {

    private final String newLineSymbol = "\n";
    private int minRows = 5;
    private int maxRows = 20;
    private int extraRows = 2;

    /**
     * Устанавливает минимальное количество строк, которое будет отображаться
     * @param rows Количество строк
     */
    public void setMinRows(String rows) {
        try {
            minRows = Integer.parseInt(rows);
        } catch (NumberFormatException ignore) {
            /* ignore */
        }
    }

    /**
     * Устанавливает максимальное количество строк, которое будет отображаться
     * @param rows Количество строк
     */
    public void setMaxRows(String rows) {
        try {
            maxRows = Integer.parseInt(rows);
        } catch (NumberFormatException ignore) {
            /* ignore */
        }
    }

    /**
     * Устанавливает количество пустых строк, которое будет отображаться в конце
     * @param rows Количество строк
     */
    public void setExtraRows(String rows) {
        try {
            extraRows = Integer.parseInt(rows);
        } catch (NumberFormatException ignore) {
            /* ignore */
        }
    }

    @Override
    protected void onAttach() {
        super.onAttach();

        getElement().getStyle().setProperty("height", "auto");
        getElement().getStyle().setProperty("maxHeight", "none");

        reg.add(addKeyUpHandler(event -> requestResize()));
        reg.add(addChangeHandler(event -> requestResize()));
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        if (reg != null) {
            for (HandlerRegistration r : reg) {
                if (r != null) {
                    r.removeHandler();
                }
            }
            reg.clear();
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
    }

    @Override
    public void setText(String text) {
        super.setText(text);
        requestResize();
    }

    private void requestResize() {
        String value = getValue();
        int lines = 0;
        if (value != null) {
            int i = value.indexOf(newLineSymbol);
            while (i != -1) {
                lines++;
                i = value.indexOf(newLineSymbol, i + 1);
            }
            lines += extraRows;
        }
        if (lines < minRows) {
            lines = minRows;
        }
        if (lines > maxRows) {
            lines = maxRows;
        }
        setVisibleLines(lines);
    }

    private List<HandlerRegistration> reg = new ArrayList<>();
}
