package ru.protei.portal.ui.common.client.common;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.text.shared.AbstractRenderer;
import com.google.gwt.text.shared.Renderer;
import ru.protei.portal.core.model.struct.Money;

public class MoneyRenderer extends AbstractRenderer<Money> {
    private static MoneyRenderer INSTANCE;

    public static Renderer<Money> getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MoneyRenderer();
        }
        return INSTANCE;
    }

    public String render(Money value) {
        if (value == null) {
            return "";
        }

        double dValue = ((double) value.getFull()) / 100;
        return NumberFormat.getDecimalFormat().format(dValue);
    }
}
