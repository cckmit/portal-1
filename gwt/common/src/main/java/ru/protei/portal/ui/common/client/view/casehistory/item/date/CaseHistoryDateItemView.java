package ru.protei.portal.ui.common.client.view.casehistory.item.date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;


public class CaseHistoryDateItemView extends Composite {
    public CaseHistoryDateItemView() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void setDate(String date) {
        this.date.setInnerText(date);
    }

    @UiField
    SpanElement date;

    interface CaseHistoryDateItemViewUiBinder extends UiBinder<HTMLPanel, CaseHistoryDateItemView> {}
    private static CaseHistoryDateItemViewUiBinder ourUiBinder = GWT.create(CaseHistoryDateItemViewUiBinder.class);
}
