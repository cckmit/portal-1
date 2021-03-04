package ru.protei.portal.ui.common.client.view.casehistory.item.casestate;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;

public class CaseHistoryStateItemView extends Composite {
    public CaseHistoryStateItemView() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void setName(String name) {
        this.name.setInnerText(name);
    }

    public void setColor(String color) {
        icon.getStyle().setColor(color);
    }

    @UiField
    Element icon;

    @UiField
    SpanElement name;

    interface CaseHistoryStateItemViewUiBinder extends UiBinder<HTMLPanel, CaseHistoryStateItemView> {}
    private static CaseHistoryStateItemViewUiBinder ourUiBinder = GWT.create(CaseHistoryStateItemViewUiBinder.class);
}
