package ru.protei.portal.ui.delivery.client.view.delivery.module.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CommentOrHistoryType;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.En_CommentOrHistoryTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.tab.multi.MultiTabWidget;
import ru.protei.portal.ui.delivery.client.activity.delivery.module.edit.AbstractModuleEditActivity;
import ru.protei.portal.ui.delivery.client.activity.delivery.module.edit.AbstractModuleEditView;

import java.util.Arrays;

import static ru.protei.portal.core.model.dict.En_CommentOrHistoryType.COMMENT;
import static ru.protei.portal.core.model.dict.En_CommentOrHistoryType.HISTORY;

public class ModuleEditView extends Composite implements AbstractModuleEditView {

    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));

        multiTabWidget.setTabToNameRenderer(type -> commentOrHistoryTypeLang.getName(type));
        multiTabWidget.addTabs(Arrays.asList(COMMENT, HISTORY));
        multiTabWidget.setOnTabClickHandler(selectedTabs -> activity.onSelectedTabsChanged(selectedTabs));

        ensureDebugIds();
    }

    @Override
    public void setActivity(AbstractModuleEditActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasWidgets getNameContainer() {
        return nameContainer;
    }

    @Override
    public HasWidgets getMetaContainer() {
        return metaContainer;
    }

    @Override
    public HasVisibility showEditViewButtonVisibility() {
        return showEditViewButton;
    }

    @Override
    public HasVisibility nameAndDescriptionEditButtonVisibility() {
        return nameAndDescriptionEditButton;
    }

    @Override
    public void setCreatedBy(String value) {
        createdBy.setInnerHTML( value );
    }

    @Override
    public void setModuleNumber( String serialNumber ) {
        this.serialNumber.setInnerText(serialNumber);
    }

    @Override
    public HasVisibility backButtonVisibility() {
        return backButton;
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        nameAndDescriptionEditButton.ensureDebugId(DebugIds.DELIVERY.KIT.MODULE.EDIT_NAME_AND_DESCRIPTION_BUTTON);
        multiTabWidget.setTabNameDebugId(COMMENT, DebugIds.DELIVERY.KIT.MODULE.TAB_COMMENT);
        multiTabWidget.setTabNameDebugId(HISTORY, DebugIds.DELIVERY.KIT.MODULE.TAB_HISTORY);
    }

    @Override
    public MultiTabWidget<En_CommentOrHistoryType> getMultiTabWidget() {
        return multiTabWidget;
    }

    @Override
    public HasWidgets getItemsContainer() {
        return multiTabWidget.getContainer();
    }

    @UiHandler("backButton")
    public void onBackClicked(ClickEvent event) {
        event.preventDefault();
        event.stopPropagation();
        if (activity != null) {
            activity.onBackClicked();
        }
    }

    @UiHandler("showEditViewButton")
    public void onShowEditViewModeButtonClick(ClickEvent event) {
        event.preventDefault();
        event.stopPropagation();
        if (activity != null) {
            activity.onOpenEditViewClicked();
        }
    }

    @UiHandler("nameAndDescriptionEditButton")
    public void onNameAndDescriptionEditButtonClicked(ClickEvent event) {
        event.preventDefault();
        event.stopPropagation();
        if (activity != null) {
            activity.onNameAndDescriptionEditClicked();
        }
    }

    @UiField
    Lang lang;
    @UiField
    HTMLPanel root;
    @UiField
    Anchor backButton;
    @UiField
    Anchor showEditViewButton;
    @UiField
    Anchor nameAndDescriptionEditButton;

    @UiField
    SpanElement serialNumber;
    @UiField
    HTMLPanel nameContainer;
    @UiField
    Element createdBy;
    @UiField
    MultiTabWidget<En_CommentOrHistoryType> multiTabWidget;
    @UiField
    HTMLPanel metaContainer;
    @Inject
    En_CommentOrHistoryTypeLang commentOrHistoryTypeLang;

    private AbstractModuleEditActivity activity;

    private static ModuleEditView.ModuleViewUiBinder ourUiBinder = GWT.create(ModuleEditView.ModuleViewUiBinder.class);
    interface ModuleViewUiBinder extends UiBinder<HTMLPanel, ModuleEditView> {}
}
