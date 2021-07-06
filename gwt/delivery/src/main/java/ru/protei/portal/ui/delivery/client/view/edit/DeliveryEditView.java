package ru.protei.portal.ui.delivery.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CommentOrHistoryType;
import ru.protei.portal.core.model.ent.Kit;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.events.EditEvent;
import ru.protei.portal.ui.common.client.events.InputEvent;
import ru.protei.portal.ui.common.client.events.clone.CloneEvent;
import ru.protei.portal.ui.common.client.lang.En_CommentOrHistoryTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.cleanablesearchbox.CleanableSearchBox;
import ru.protei.portal.ui.common.client.widget.delivery.optionlist.kit.KitList;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;
import ru.protei.portal.ui.common.client.widget.tab.multi.MultiTabWidget;
import ru.protei.portal.ui.delivery.client.activity.edit.AbstractDeliveryEditActivity;
import ru.protei.portal.ui.delivery.client.activity.edit.AbstractDeliveryEditView;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

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
    public void fillKits(List<Kit> kitSet) {
        kits.fillOptions(kitSet);
    }

    @Override
    public HasValue<String> searchKitPattern() {
        return search;
    }

    @Override
    public void setKitFilter(Selector.SelectorFilter<Kit> filter) {
        kits.refreshValueByFilter(filter);
    }

    @Override
    public Set<Kit> getSelectedKits() {
        return kits.getValue();
    }

    @Override
    public HasVisibility nameAndDescriptionEditButtonVisibility() {
        return nameAndDescriptionEditButton;
    }

    @Override
    public HasVisibility addKitsButtonVisibility() {
        return addKitsButton;
    }

    @Override
    public void setCreatedBy(String value) {
        this.createdBy.setInnerHTML( value );
    }

    @UiHandler("search")
    public void onSearchChanged(InputEvent event) {
        if ( activity != null ) {
            activity.onSearchKitChanged();
        }
    }

    @UiHandler("kits")
    public void onKitEditClicked(EditEvent event) {
        if ( activity != null ) {
            activity.onKitEditClicked(event.id, event.text);
        }
    }

    @UiHandler("kits")
    public void onKitCloneClicked(CloneEvent event) {
        if ( activity != null ) {
            activity.onKitCloneClicked(event.id);
        }
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

    @UiHandler("removeKitsButton")
    public void onRemoveKitsButtonClicked(ClickEvent event) {
        if (activity != null) {
            activity.onRemoveKitsButtonClicked(kits.getValue());
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

        backButton.ensureDebugId(DebugIds.DELIVERY.BACK_BUTTON);
        showEditViewButton.ensureDebugId(DebugIds.DELIVERY.SHOW_EDIT_BUTTON);
        nameAndDescriptionEditButton.ensureDebugId(DebugIds.DELIVERY.EDIT_NAME_AND_DESCRIPTION_BUTTON);
        addKitsButton.ensureDebugId(DebugIds.DELIVERY.ADD_KITS_BUTTON);
        multiTabWidget.setTabNameDebugId(COMMENT, DebugIds.DELIVERY.TAB_COMMENT);
        multiTabWidget.setTabNameDebugId(HISTORY, DebugIds.DELIVERY.TAB_HISTORY);
    }

    @UiField
    HTMLPanel root;
    @UiField
    HTMLPanel nameContainer;
    @UiField
    CleanableSearchBox search;
    @Inject
    @UiField(provided = true)
    KitList kits;
    @UiField
    HTMLPanel metaContainer;
    @UiField
    Button backButton;
    @UiField
    Button showEditViewButton;
    @UiField
    Button removeKitsButton;
    @UiField
    Button nameAndDescriptionEditButton;
    @UiField
    Button addKitsButton;
    @UiField
    Lang lang;
    @UiField
    MultiTabWidget<En_CommentOrHistoryType> multiTabWidget;
    @UiField
    Element createdBy;
    @Inject
    En_CommentOrHistoryTypeLang commentOrHistoryTypeLang;

    private AbstractDeliveryEditActivity activity;

    interface ViewUiBinder extends UiBinder<HTMLPanel, DeliveryEditView> {}
    private static ViewUiBinder ourUiBinder = GWT.create(ViewUiBinder.class);
}
