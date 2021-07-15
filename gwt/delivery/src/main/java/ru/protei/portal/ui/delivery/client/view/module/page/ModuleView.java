package ru.protei.portal.ui.delivery.client.view.module.page;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.delivery.client.activity.kit.page.AbstractKitActivity;
import ru.protei.portal.ui.delivery.client.activity.kit.page.AbstractModuleView;

public class ModuleView extends Composite implements AbstractModuleView {

    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractKitActivity activity) {
        this.activity = activity;
    }

    @UiField
    Lang lang;

    private AbstractKitActivity activity;

    private static ModuleView.ModuleViewUiBinder ourUiBinder = GWT.create(ModuleView.ModuleViewUiBinder.class);
    interface ModuleViewUiBinder extends UiBinder<HTMLPanel, ModuleView> {}
}
