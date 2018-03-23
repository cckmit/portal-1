package ru.protei.portal.ui.common.client.widget.autoresizetextarea;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.TextArea;

import java.util.ArrayList;
import java.util.List;

public class AutoResizeTextArea extends TextArea {

    private final String newLineSymbol = "\n";
    private final int numberOfExtraLines = 2;
    private int minRows = 5;
    private int maxRows = 20;

    @Override
    protected void onAttach() {
        super.onAttach();

        this.getElement().getStyle().setProperty("height", "auto");
        this.getElement().getStyle().setProperty("maxHeight", "none");

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

    public void setMinRows(String rows) {
        try {
            minRows = Integer.parseInt(rows);
        } catch (NumberFormatException ignore) {
            /* ignore */
        }
    }

    public void setMaxRows(String rows) {
        try {
            maxRows = Integer.parseInt(rows);
        } catch (NumberFormatException ignore) {
            /* ignore */
        }
    }

    public void requestResize() {
        String value = getValue();
        int lines = 0;
        if (value != null) {
            int i = value.indexOf(newLineSymbol);
            while (i != -1) {
                lines++;
                i = value.indexOf(newLineSymbol, i + 1);
            }
            lines += numberOfExtraLines;
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
