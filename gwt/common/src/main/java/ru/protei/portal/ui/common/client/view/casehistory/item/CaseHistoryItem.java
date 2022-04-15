package ru.protei.portal.ui.common.client.view.casehistory.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;

import static ru.protei.portal.test.client.DebugIds.CASE_HISTORY.ITEM.*;
import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;
import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.HIDE;

public class CaseHistoryItem extends Composite {
    public CaseHistoryItem() {
        initWidget(ourUiBinder.createAndBindUi(this));
        setTestAttributes();
    }

    public HasVisibility addedValueContainerVisibility() {
        return addedValueContainer;
    }

    public HasVisibility removedValueContainerVisibility() {
        return removedValueContainer;
    }

    public HasVisibility changeContainerVisibility() {
        return changedValueContainer;
    }

    public void setHistoryType(String historyType) {
        this.historyType.setInnerText(historyType);
    }

    public void setLinkedHistoryType(String linkName, String href) {
        historyTypeLink.setText(linkName);
        historyTypeLink.setHref(href);
        historyTypeLink.setVisible(true);
    }

    public HasWidgets addedValueContainer() {
        return addedValueContainer;
    }

    public HasVisibility oldValueContainerVisibility() {
        return oldValueContainer;
    }

    public void setChangeValueIconVisible(boolean isVisible) {
        if (isVisible){
            changedValueIcon.removeClassName(HIDE);
        } else {
            changedValueIcon.addClassName(HIDE);
        }
    }

    public HasWidgets oldValueContainer() {
        return oldValueContainer;
    }

    public HasWidgets newValueContainer() {
        return newValueContainer;
    }

    public HasWidgets removedValueContainer() {
        return removedValueContainer;
    }

    private void setTestAttributes() {
        addedValueContainer.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, ADDED_VALUE);
        removedValueContainer.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, REMOVED_VALUE);
        oldValueContainer.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, OLD_VALUE);
        newValueContainer.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, NEW_VALUE);
        historyType.setAttribute(DEBUG_ID_ATTRIBUTE, HISTORY_TYPE);
    }

    @UiField
    HTMLPanel addedValueContainer;

    @UiField
    HTMLPanel changedValueContainer;

    @UiField
    Element changedValueIcon;

    @UiField
    SpanElement historyType;

    @UiField
    Anchor historyTypeLink;

    @UiField
    HTMLPanel oldValueContainer;

    @UiField
    HTMLPanel newValueContainer;

    @UiField
    HTMLPanel removedValueContainer;

    interface CaseHistoryItemUiBinder extends UiBinder<HTMLPanel, CaseHistoryItem> {}
    private static CaseHistoryItemUiBinder ourUiBinder = GWT.create(CaseHistoryItemUiBinder.class);
}
