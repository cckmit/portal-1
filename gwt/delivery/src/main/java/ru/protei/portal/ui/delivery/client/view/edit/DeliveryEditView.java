package ru.protei.portal.ui.delivery.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CommentOrHistoryType;
import ru.protei.portal.core.model.ent.Kit;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.En_CommentOrHistoryTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.quickview.QuickView;
import ru.protei.portal.ui.common.client.widget.tab.multi.MultiTabWidget;
import ru.protei.portal.ui.delivery.client.activity.edit.AbstractDeliveryEditActivity;
import ru.protei.portal.ui.delivery.client.activity.edit.AbstractDeliveryEditView;
import ru.protei.portal.ui.delivery.client.widget.kit.view.list.DeliveryKitList;

import java.util.Arrays;
import java.util.List;

import static ru.protei.portal.core.model.dict.En_CommentOrHistoryType.COMMENT;
import static ru.protei.portal.core.model.dict.En_CommentOrHistoryType.HISTORY;

/**
 * Вид редактирования Поставки
 */
public class DeliveryEditView extends Composite implements AbstractDeliveryEditView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));

        multiTabWidget.setTabToNameRenderer(type -> commentOrHistoryTypeLang.getName(type));
        multiTabWidget.addTabs(Arrays.asList(COMMENT, HISTORY));
        multiTabWidget.setOnTabClickHandler(selectedTabs -> activity.onSelectedTabsChanged(selectedTabs));

        ensureDebugIds();
    }

    @Override
    public void setActivity(AbstractDeliveryEditActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasWidgets getNameContainer() {
        return nameContainer;
    }

    @Override
    public HasValue<List<Kit>> kits() {
        return kits;
    }

    @Override
    public void updateKitByProject(boolean isArmyProject) {
        kits.setArmyProject(isArmyProject);
    }

    @Override
    public HasWidgets getMetaContainer() {
        return metaContainer;
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
    public HasVisibility backButtonVisibility() {
        return backButton;
    }

    @Override
    public HasVisibility showEditViewButtonVisibility() {
        return showEditViewButton;
    }

    @Override
    public void setPreviewStyles(boolean isPreview) {
        root.removeStyleName("card-default");
        root.removeStyleName("card-transparent");
        root.removeStyleName("card-fixed");
        if (isPreview) {
            root.addStyleName("card-default");
            root.addStyleName("card-fixed");
        } else {
            root.addStyleName("card-transparent");
        }
    }

    @Override
    public HasVisibility nameAndDescriptionEditButtonVisibility() {
        return nameAndDescriptionEditButton;
    }

    @Override
    public HasEnabled refreshKitsSerialNumberEnabled() {
        return kits.getRefreshKitsSerialNumberButton();
    }

    @Override
    public void setKitsAddButtonEnabled(boolean isKitsAddButtonEnabled) {
        kits.setKitsAddButtonEnabled(isKitsAddButtonEnabled);
    }

    @Override
    public HasWidgets quickview() {
        return quickview;
    }

    @Override
    public void showQuickview(boolean isShow) {
        quickview.show(isShow);
    }

    @Override
    public HasVisibility addKitsButton() {
        return addKitsButton;
    }

    @UiHandler("showEditViewButton")
    public void onShowEditViewModeButtonClick(ClickEvent event) {
        if (activity != null) {
            activity.onOpenEditViewClicked();
        }
    }

    @UiHandler("backButton")
    public void onCancelClicked(ClickEvent event) {
        if (activity != null) {
            activity.onBackClicked();
        }
    }

    @UiHandler({"nameAndDescriptionEditButton"})
    public void onNameAndDescriptionEditButtonClicked(ClickEvent event) {
        if (activity != null) {
            activity.onNameAndDescriptionEditClicked();
        }
    }

    @UiHandler({"addKitsButton"})
    public void onAddKitsButtonClicked(ClickEvent event) {
        if (activity != null) {
            activity.onAddKitsButtonClicked();
        }
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        kits.setEnsureDebugId(DebugIds.DELIVERY.KITS);

        backButton.ensureDebugId(DebugIds.DELIVERY.BACK_BUTTON);
        showEditViewButton.ensureDebugId(DebugIds.DELIVERY.SHOW_EDIT_BUTTON);
        nameAndDescriptionEditButton.ensureDebugId(DebugIds.DELIVERY.NAME_AND_DESCRIPTION_EDIT_BUTTON);
        addKitsButton.ensureDebugId(DebugIds.DELIVERY.KIT.ADD_BUTTON);
    }

    @UiField
    HTMLPanel root;
    @UiField
    HTMLPanel nameContainer;
    @UiField
    HTMLPanel kitsContainer;
    @Inject
    @UiField(provided = true)
    DeliveryKitList kits;
    @UiField
    HTMLPanel metaContainer;
    @UiField
    Button backButton;
    @UiField
    Button showEditViewButton;
    @UiField
    Button nameAndDescriptionEditButton;
    @UiField
    Button addKitsButton;
    @UiField
    Lang lang;
    @UiField
    MultiTabWidget<En_CommentOrHistoryType> multiTabWidget;
    @UiField
    QuickView quickview;
    @Inject
    En_CommentOrHistoryTypeLang commentOrHistoryTypeLang;

    private AbstractDeliveryEditActivity activity;

    interface ViewUiBinder extends UiBinder<HTMLPanel, DeliveryEditView> {}
    private static ViewUiBinder ourUiBinder = GWT.create(ViewUiBinder.class);
}
