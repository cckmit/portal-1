package ru.protei.portal.ui.delivery.client.view.kit.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.delivery.client.activity.kit.table.AbstractKitTableActivity;
import ru.protei.portal.ui.delivery.client.activity.kit.table.AbstractKitTableView;

public class KitTableView extends Composite implements AbstractKitTableView {

    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractKitTableActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasWidgets getKitTableContainer() {
        return kitTableContainer;
    }

    @Override
    public HasWidgets getModuleTableContainer() {
        return moduleTableContainer;
    }

    @Override
    public HasWidgets getModuleEditContainer() {
        return moduleEditContainer;
    }

    @UiField
    Lang lang;
    @UiField
    HTMLPanel kitTableContainer;
    @UiField
    HTMLPanel moduleTableContainer;
    @UiField
    HTMLPanel moduleEditContainer;

    private AbstractKitTableActivity activity;

    private static KitTableView.TableViewUiBinder ourUiBinder = GWT.create(KitTableView.TableViewUiBinder.class);
    interface TableViewUiBinder extends UiBinder<HTMLPanel, KitTableView> {}
}
