package ru.protei.portal.ui.delivery.client.view.kit.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CommentOrHistoryType;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.En_CommentOrHistoryTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.module.ModuleStateFormSelector;
import ru.protei.portal.ui.common.client.widget.tab.multi.MultiTabWidget;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.delivery.client.activity.kit.edit.AbstractDeliveryKitEditActivity;
import ru.protei.portal.ui.delivery.client.activity.kit.edit.AbstractDeliveryKitEditView;

import static java.util.Collections.singletonList;
import static ru.protei.portal.core.model.dict.En_CommentOrHistoryType.COMMENT;
import static ru.protei.portal.core.model.dict.En_CommentOrHistoryType.HISTORY;
import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;

public class DeliveryKitEditView extends Composite implements AbstractDeliveryKitEditView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));

        multiTabWidget.setTabToNameRenderer(type -> commentOrHistoryTypeLang.getName(type));
        multiTabWidget.addTabs(singletonList(HISTORY));
        multiTabWidget.setOnTabClickHandler(selectedTabs -> activity.onSelectedTabsChanged(selectedTabs));

        ensureDebugIds();
    }

    @Override
    public void setActivity(AbstractDeliveryKitEditActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasWidgets getModulesContainer() {
        return modulesContainer;
    }

    @Override
    public void setCreatedBy(String value) {
        this.createdBy.setInnerHTML( value );
    }

    @Override
    public HasWidgets getItemsContainer() {
        return multiTabWidget.getContainer();
    }

    @Override
    public MultiTabWidget<En_CommentOrHistoryType> getMultiTabWidget() {
        return multiTabWidget;
    }

    @Override
    public void setStateEnabled(boolean isEnabled) {
        state.setEnabled(isEnabled);
    }

    @Override
    public void setNameEnabled(boolean isEnabled) {
        name.setEnabled(isEnabled);
    }

    @Override
    public void setSerialNumber(String serialNumber) {
        this.serialNumber.setInnerText( lang.deliveryKit() + serialNumber );
    }

    @Override
    public TakesValue<CaseState> state() {
        return state;
    }

    @Override
    public TakesValue<String> name() {
        return name;
    }

    @Override
    public HasEnabled saveButtonEnabled() {
        return null;
    }

    @UiHandler("backButton")
    public void onCancelClicked(ClickEvent event) {
        if (activity != null) {
            activity.onBackClicked();
        }
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        backButton.ensureDebugId(DebugIds.DELIVERY.BACK_BUTTON);
        multiTabWidget.setTabNameDebugId(COMMENT, DebugIds.DELIVERY.TAB_COMMENT);
        multiTabWidget.setTabNameDebugId(HISTORY, DebugIds.DELIVERY.TAB_HISTORY);
        state.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.DELIVERY.KIT.STATE);
        name.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.DELIVERY.KIT.NAME);
    }

    @UiField
    Lang lang;
    @UiField
    HeadingElement serialNumber;
    @Inject
    @UiField(provided = true)
    ModuleStateFormSelector state;
    @UiField
    ValidableTextBox name;
    @UiField
    HTMLPanel modulesContainer;
    @UiField
    Button backButton;
    @UiField
    Element createdBy;
    @UiField
    MultiTabWidget<En_CommentOrHistoryType> multiTabWidget;
    @Inject
    En_CommentOrHistoryTypeLang commentOrHistoryTypeLang;

    private AbstractDeliveryKitEditActivity activity;

    interface ViewUiBinder extends UiBinder<HTMLPanel, DeliveryKitEditView> {}
    private static DeliveryKitEditView.ViewUiBinder ourUiBinder = GWT.create(DeliveryKitEditView.ViewUiBinder.class);
}
