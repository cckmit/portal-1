package ru.protei.portal.ui.common.client.view.casehistory.item.tag;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;

import static ru.protei.portal.ui.common.client.util.ColorUtils.makeContrastColor;
import static ru.protei.portal.ui.common.client.util.ColorUtils.makeSafeColor;

public class CaseHistoryTagItemView extends Composite {
    public CaseHistoryTagItemView() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void setName(String name) {
        this.name.setInnerText(name);
    }

    public void setColor(String color) {
        String backgroundColor = makeSafeColor(color);

        name.getStyle().setBackgroundColor(backgroundColor);
        name.getStyle().setColor(makeContrastColor(backgroundColor));
    }

    @UiField
    SpanElement name;

    interface CaseHistoryTagItemViewUiBinder extends UiBinder<HTMLPanel, CaseHistoryTagItemView> {}
    private static CaseHistoryTagItemViewUiBinder ourUiBinder = GWT.create(CaseHistoryTagItemViewUiBinder.class);
}
