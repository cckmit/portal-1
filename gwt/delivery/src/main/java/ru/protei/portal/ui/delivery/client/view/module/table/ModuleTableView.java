package ru.protei.portal.ui.delivery.client.view.module.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.TableWidget;
import ru.brainworm.factory.widget.table.client.helper.SelectionColumn;
import ru.protei.portal.core.model.ent.Module;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.delivery.client.activity.kit.page.AbstractKitActivity;
import ru.protei.portal.ui.delivery.client.activity.kit.page.AbstractModuleTableView;
import ru.protei.portal.ui.delivery.client.view.module.column.ModuleColumn;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ModuleTableView extends Composite implements AbstractModuleTableView {

    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
    }

    private void initTable() {
        ModuleColumn infoColumn = new ModuleColumn(lang);
        table.addColumn(selectionColumn.header, selectionColumn.values);
        table.addColumn(infoColumn.header, infoColumn.values);
        infoColumn.setHandler(activity);
        infoColumn.setColumnProvider(columnProvider);
        selectionColumn.setSelectionHandler((module, handler) -> {
            if (activity != null) {
                activity.onCheckModuleClicked(this);
            }
        });
    }

    @Override
    public void setActivity(AbstractKitActivity activity) {
        this.activity = activity;
        initTable();
    }

    @Override
    public Set<Module> getSelectedModules() {
        return selectionColumn.selectedValues;
    }

    @Override
    public void clearSelectedRows() {
        selectionColumn.clear();
        columnProvider.removeSelection();
        setDeleteEnabled(false);
    }

    @Override
    public void clearModules() {
        table.clearRows();
    }

    @Override
    public void fillTable(Map<Module, List<Module>> modules) {
        for (Map.Entry<Module, List<Module>> entry : modules.entrySet()) {
            Module parentRow = entry.getKey();
            table.addRow(parentRow);

            List<Module> childRows = entry.getValue();
            childRows.forEach(row -> {
                table.addChildRow(parentRow, row);
                table.addRowStyle(row, "child-row");
            });
        }
    }

    @Override
    public void updateRow(Module item) {
        if(item != null)
            table.updateRow(item);
    }

    @UiHandler("addButton")
    public void onClickAddButton(ClickEvent event) {
        event.preventDefault();
        event.stopPropagation();
        if (activity != null) {
            activity.onAddModuleClicked();
        }
    }

    @UiHandler("copyButton")
    public void onCopyClicked(ClickEvent event) {
        event.preventDefault();
        event.stopPropagation();
    }

    @UiHandler("stateButton")
    public void onStateClicked(ClickEvent event) {
        event.preventDefault();
        event.stopPropagation();
    }

    @UiHandler("deleteButton")
    public void onRemoveClicked(ClickEvent event) {
        event.preventDefault();
        event.stopPropagation();
        if (activity != null) {
            activity.onRemoveModuleClicked(this);
        }
    }

    @UiHandler("reloadButton")
    public void onReloadClicked(ClickEvent event) {
        event.preventDefault();
        event.stopPropagation();
    }

    @Override
    public void setDeleteEnabled(boolean isEnabled) {
        if (isEnabled) {
            deleteButton.getElement().removeClassName(REMOVING_DISABLED);
        } else {
            deleteButton.getElement().addClassName(REMOVING_DISABLED);
        }
    }

    @Override
    public boolean hasSelectedModules() {
        return getSelectedModules().size() > 0;
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        addButton.ensureDebugId(DebugIds.DELIVERY.KIT.MODULE.ADD_BUTTON);
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
    private ClickColumnProvider<Module> columnProvider = new ClickColumnProvider<>();

    private static ModuleTableView.ModuleViewUiBinder ourUiBinder = GWT.create(ModuleTableView.ModuleViewUiBinder.class);
    interface ModuleViewUiBinder extends UiBinder<HTMLPanel, ModuleTableView> {}

    private static final String REMOVING_DISABLED = "module-removing-disabled";
}
