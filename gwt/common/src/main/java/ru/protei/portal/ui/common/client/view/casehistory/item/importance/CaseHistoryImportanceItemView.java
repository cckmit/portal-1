package ru.protei.portal.ui.common.client.view.casehistory.item.importance;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;

import static com.google.gwt.dom.client.Style.TextDecoration.LINE_THROUGH;
import static com.google.gwt.dom.client.Style.TextDecoration.NONE;
import static ru.protei.portal.ui.common.client.util.ColorUtils.makeContrastColor;

public class CaseHistoryImportanceItemView extends Composite {
    public CaseHistoryImportanceItemView() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void setName(String name) {
        this.name.setInnerText(name);
        icon.setInnerText(name.substring(0, 1).toUpperCase());
    }

    public void setColor(String color) {
        icon.getStyle().setBackgroundColor(color);
        icon.getStyle().setColor(makeContrastColor(color));
    }

    @UiField
    SpanElement icon;

    @UiField
    SpanElement name;

    interface CaseHistoryImportanceItemViewUiBinder extends UiBinder<HTMLPanel, CaseHistoryImportanceItemView> {}
    private static CaseHistoryImportanceItemViewUiBinder ourUiBinder = GWT.create(CaseHistoryImportanceItemViewUiBinder.class);
}
