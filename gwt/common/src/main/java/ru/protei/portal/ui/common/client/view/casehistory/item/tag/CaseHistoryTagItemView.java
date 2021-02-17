package ru.protei.portal.ui.common.client.view.casehistory.item.tag;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import ru.protei.portal.ui.common.client.util.ColorUtils;

import static ru.protei.portal.ui.common.client.util.ColorUtils.makeContrastColor;
import static ru.protei.portal.ui.common.client.util.ColorUtils.makeSafeColor;

public class CaseHistoryTagItemView extends Composite {
    public CaseHistoryTagItemView() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void setName(String name) {
        this.name.setInnerText(name);
        this.icon.setInnerText(name.substring(0, 1).toUpperCase());
    }

    public void setColor(String color) {
        String safeColor = makeSafeColor(color);

        icon.getStyle().setBackgroundColor(safeColor);
        icon.getStyle().setColor(makeContrastColor(safeColor));
    }

    @UiField
    SpanElement icon;

    @UiField
    SpanElement name;

    interface CaseHistoryTagItemViewUiBinder extends UiBinder<HTMLPanel, CaseHistoryTagItemView> {}
    private static CaseHistoryTagItemViewUiBinder ourUiBinder = GWT.create(CaseHistoryTagItemViewUiBinder.class);
}
