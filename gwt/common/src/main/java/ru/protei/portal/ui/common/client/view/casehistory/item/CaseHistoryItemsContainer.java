package ru.protei.portal.ui.common.client.view.casehistory.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;

import static ru.protei.portal.test.client.DebugIds.CASE_HISTORY.ITEM.*;
import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;

/**
 * Один комментарий
 */
public class CaseHistoryItemsContainer
        extends Composite {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        setTestAttributes();
    }

    public void initWithoutInitiatorMode() {
        root.addStyleName("without-initiator");
    }

    public void setInitiator(String initiator) {
        this.initiator.getElement().setInnerText(initiator);
    }

    public HasWidgets itemsContainer() {
        return itemsContainer;
    }

    public void setDate(String date) {
        this.date.getElement().setInnerText(date);
    }

    private void setTestAttributes() {
        date.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, CREATE_DATE);
        initiator.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, INITIATOR);
    }

    @UiField
    HTMLPanel root;

    @UiField
    HTMLPanel date;

    @UiField
    HTMLPanel initiator;

    @UiField
    HTMLPanel historyIconContainer;

    @UiField
    HTMLPanel itemsContainer;

    private static CaseHistoryUiBinder ourUiBinder = GWT.create(CaseHistoryUiBinder.class);
    interface CaseHistoryUiBinder extends UiBinder<HTMLPanel, CaseHistoryItemsContainer> {}
}
