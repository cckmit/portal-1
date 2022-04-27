package ru.protei.portal.ui.common.client.view.casehistory.item.simple;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;

public class CaseHistorySimpleItemView extends Composite {
    public CaseHistorySimpleItemView() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void setName(String name) {
        this.name.setInnerText(name);
    }

    @UiField
    SpanElement name;
    @UiField
    HTMLPanel root;

    interface CaseHistoryLinkItemViewUiBinder extends UiBinder<HTMLPanel, CaseHistorySimpleItemView> {}
    private static CaseHistoryLinkItemViewUiBinder ourUiBinder = GWT.create(CaseHistoryLinkItemViewUiBinder.class);
}
