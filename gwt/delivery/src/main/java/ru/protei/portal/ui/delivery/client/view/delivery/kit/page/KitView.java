package ru.protei.portal.ui.delivery.client.view.delivery.kit.page;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.Kit;
import ru.protei.portal.ui.common.client.events.EditEvent;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.delivery.optionlist.kit.KitList;
import ru.protei.portal.ui.delivery.client.activity.delivery.kit.handler.KitActionsHandler;
import ru.protei.portal.ui.delivery.client.activity.delivery.kit.page.AbstractKitActivity;
import ru.protei.portal.ui.delivery.client.activity.delivery.kit.page.AbstractKitView;
import ru.protei.portal.ui.delivery.client.view.delivery.kit.actionmenu.KitMenu;

import java.util.List;
import java.util.Set;

public class KitView extends Composite implements AbstractKitView {

    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setHandler(KitActionsHandler handler) {
        kitsMenu.setHandler(handler);
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

    @Override
    public void setKitsActionsEnabled(boolean hasEditPrivileges) {
        kitsMenu.setActionsEnabled(hasEditPrivileges);
    }

    @Override
    public Set<Kit> getKitsSelected() {
        return kits.getValue();
    }

    @UiHandler("kits")
    public void onKitEditClicked(EditEvent event) {
        if ( activity != null ) {
            activity.onKitClicked(event.id);
        }
    }

    @UiField
    Lang lang;
    @Inject
    @UiField(provided = true)
    KitList kits;
    @Inject
    @UiField(provided = true)
    KitMenu kitsMenu;
    @UiField
    HTMLPanel modulesContainer;
    @UiField
    HTMLPanel moduleEditContainer;

    private AbstractKitActivity activity;

    private static KitView.KitViewUiBinder ourUiBinder = GWT.create(KitView.KitViewUiBinder.class);
    interface KitViewUiBinder extends UiBinder<HTMLPanel, KitView> {}
}
