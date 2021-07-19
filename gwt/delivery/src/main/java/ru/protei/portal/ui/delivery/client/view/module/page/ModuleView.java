package ru.protei.portal.ui.delivery.client.view.module.page;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.TableWidget;
import ru.brainworm.factory.widget.table.client.helper.SelectionColumn;
import ru.protei.portal.core.model.ent.Module;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.delivery.client.activity.kit.page.AbstractKitActivity;
import ru.protei.portal.ui.delivery.client.activity.kit.page.AbstractModuleView;
import ru.protei.portal.ui.delivery.client.view.module.column.ModuleColumn;

import java.util.List;

public class ModuleView extends Composite implements AbstractModuleView {

    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
        initTable();
    }

    private void initTable() {
        ModuleColumn infoColumn = new ModuleColumn();
        table.addColumn(selectionColumn.header, selectionColumn.values);
        table.addColumn(infoColumn.header, infoColumn.values);
    }

    @Override
    public void setActivity(AbstractKitActivity activity) {
        this.activity = activity;
    }

    @Override
    public void clearModules() {
        table.clearRows();
    }

    @Override
    public void putModules(List<Module> modules) {
        modules.forEach(module -> {
            table.addRow(module, module.getParentModuleId() != null ? "child-row" : null);
        });
    }

    @UiField
    Lang lang;

    @UiField
    TableWidget<Module> table;
    @UiField
    Anchor addButton;
    @UiField
    Anchor copyButton;
    @UiField
    Anchor stateButton;
    @UiField
    Anchor deleteButton;
    @UiField
    Anchor reloadButton;

    private AbstractKitActivity activity;

    private SelectionColumn<Module> selectionColumn = new SelectionColumn<>();

    private static ModuleView.ModuleViewUiBinder ourUiBinder = GWT.create(ModuleView.ModuleViewUiBinder.class);
    interface ModuleViewUiBinder extends UiBinder<HTMLPanel, ModuleView> {}
}
