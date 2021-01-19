package ru.protei.portal.ui.common.client.view.casehistory.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.activity.casehistory.item.AbstractCaseHistoryItemActivity;
import ru.protei.portal.ui.common.client.activity.casehistory.item.AbstractCaseHistoryItemView;

import static ru.protei.portal.test.client.DebugIds.CASE_HISTORY.ITEM.*;
import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;
import static ru.protei.portal.ui.common.client.util.ColorUtils.makeContrastColor;
import static ru.protei.portal.ui.common.client.util.ColorUtils.makeSafeColor;

/**
 * Один комментарий
 */
public class CaseHistoryItemView
        extends Composite
        implements AbstractCaseHistoryItemView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        setTestAttributes();
    }

    @Override
    public void setActivity(AbstractCaseHistoryItemActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasVisibility addedValueContainerVisibility() {
        return addedValueContainer;
    }

    @Override
    public HasVisibility removedValueContainerVisibility() {
        return removedValueContainer;
    }

    @Override
    public HasVisibility changeContainerVisibility() {
        return changedValueContainer;
    }

    @Override
    public void setChangeInfoMessage(String changeInfoMessage) {
        this.changeInfoMessage.setInnerText(changeInfoMessage);
    }

    @Override
    public void setInitiator(String initiator) {
        this.initiator.setInnerText(initiator);
    }

    @Override
    public void setAddedValue(String addedValue, String title) {
        this.addedValue.setInnerHTML(addedValue);
        this.addedValue.setTitle(title);
    }

    @Override
    public void setRemovedValue(String removedValue, String title) {
        this.removedValue.setInnerHTML(removedValue);
        this.removedValue.setTitle(title);
    }

    @Override
    public void setOldValue(String oldValue, String title) {
        this.oldValue.setInnerHTML(oldValue);
        this.oldValue.setTitle(title);
    }

    @Override
    public void setNewValue(String newValue, String title) {
        this.newValue.setInnerHTML(newValue);
        this.newValue.setTitle(title);
    }

    @Override
    public void setDate(String date) {
        this.date.setInnerText(date);
    }

    @Override
    public void setPhoto(String photoUrl) {
        this.photo.setSrc(photoUrl);
    }

    @Override
    public void setAddedValueColor(String addedValueColor) {
        addedValue.addClassName("colored-history-item");

        String backgroundColor = makeSafeColor(addedValueColor);
        String foregroundColor = makeContrastColor(backgroundColor);

        addedValue.getStyle().setProperty("backgroundColor", backgroundColor);
        addedValue.getStyle().setProperty("color", foregroundColor);
    }

    @Override
    public void setChangedValueColors(String oldValueColor, String newValueColor) {
        oldValue.addClassName("colored-history-item");
        newValue.addClassName("colored-history-item");

        String backgroundOldColor = makeSafeColor(oldValueColor);
        String foregroundOldColor = makeContrastColor(backgroundOldColor);

        String backgroundNewColor = makeSafeColor(newValueColor);
        String foregroundNewColor = makeContrastColor(backgroundNewColor);

        oldValue.getStyle().setProperty("backgroundColor", backgroundOldColor);
        oldValue.getStyle().setProperty("color", foregroundOldColor);

        newValue.getStyle().setProperty("backgroundColor", backgroundNewColor);
        newValue.getStyle().setProperty("color", foregroundNewColor);
    }

    private void setTestAttributes() {
        addedValue.setAttribute(DEBUG_ID_ATTRIBUTE, ADDED_VALUE);
        removedValue.setAttribute(DEBUG_ID_ATTRIBUTE, REMOVED_VALUE);
        oldValue.setAttribute(DEBUG_ID_ATTRIBUTE, OLD_VALUE);
        newValue.setAttribute(DEBUG_ID_ATTRIBUTE, NEW_VALUE);
        date.setAttribute(DEBUG_ID_ATTRIBUTE, CREATE_DATE);
        changeInfoMessage.setAttribute(DEBUG_ID_ATTRIBUTE, HISTORY_TYPE);
        initiator.setAttribute(DEBUG_ID_ATTRIBUTE, INITIATOR);
    }

    @UiField
    HTMLPanel addedValueContainer;

    @UiField
    SpanElement addedValue;

    @UiField
    HTMLPanel changedValueContainer;

    @UiField
    SpanElement date;

    @UiField
    ImageElement photo;

    @UiField
    SpanElement initiator;

    @UiField
    SpanElement changeInfoMessage;

    @UiField
    SpanElement oldValue;

    @UiField
    SpanElement newValue;

    @UiField
    HTMLPanel removedValueContainer;

    @UiField
    Element removedValue;

    private AbstractCaseHistoryItemActivity activity;

    private static CaseHistoryUiBinder ourUiBinder = GWT.create(CaseHistoryUiBinder.class);
    interface CaseHistoryUiBinder extends UiBinder<HTMLPanel, CaseHistoryItemView> {}
}
