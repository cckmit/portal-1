package ru.protei.portal.ui.delivery.client.view.kit.page;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.Kit;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.ui.common.client.events.EditEvent;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.delivery.optionlist.kit.KitList;
import ru.protei.portal.ui.common.client.widget.stringselectpopup.StringSelectPopup;
import ru.protei.portal.ui.delivery.client.activity.kit.handler.KitActionsHandler;
import ru.protei.portal.ui.delivery.client.activity.kit.page.AbstractKitActivity;
import ru.protei.portal.ui.delivery.client.activity.kit.page.AbstractKitView;
import ru.protei.portal.ui.delivery.client.view.kit.actionmenu.KitActionsPopup;

import java.util.ArrayList;
import java.util.List;

public class KitView extends Composite implements AbstractKitView {

    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setHandler(KitActionsHandler handler) {
        actionsPopup.setHandler(handler);
    }

    @Override
    public void setActivity(AbstractKitActivity activity) {
        this.activity = activity;
    }

    @Override
    public void fillKits(List<Kit> kitSet) {
        kits.fillOptions(kitSet);
    }


    @Override
    public HasWidgets getModulesContainer() {
        return modulesContainer;
    }

    @Override
    public HasWidgets getModuleEditContainer() {
        return moduleEditContainer;
    }

    @Override
    public void makeKitSelected(Long kitId) {
        kits.makeKitSelected(kitId);
    }

    @UiHandler("kits")
    public void onKitEditClicked(EditEvent event) {
        if ( activity != null ) {
            activity.onKitClicked(event.id);
        }
    }

    @UiHandler("reloadKitsBtn")
    public void onReloadKitsBtnClicked(ClickEvent event) {
        event.preventDefault();
        if ( activity != null ) {
            activity.onReloadKitsBtnClicked();
        }
    }

    @UiHandler("kitsActionsBtn")
    public void setKitsActionsBtnClick(ClickEvent event) {
        actionsPopup.showUnderRight(kitsActionsBtn, 200);
    }

    @UiField
    Lang lang;
    @Inject
    @UiField(provided = true)
    KitList kits;
    @UiField
    HTMLPanel modulesContainer;
    @UiField
    HTMLPanel moduleEditContainer;
    @UiField
    Anchor reloadKitsBtn;
    @UiField
    Anchor kitsActionsBtn;
    @Inject
    KitActionsPopup actionsPopup;

    private AbstractKitActivity activity;

    private static KitView.KitViewUiBinder ourUiBinder = GWT.create(KitView.KitViewUiBinder.class);
    interface KitViewUiBinder extends UiBinder<HTMLPanel, KitView> {}
}
