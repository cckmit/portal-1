package ru.protei.portal.ui.sitefolder.client.view.platform.table.concise;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.AbstractColumn;
import ru.brainworm.factory.widget.table.client.TableWidget;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Platform;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.columns.RemoveClickColumn;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.sitefolder.client.activity.plaform.table.concise.AbstractPlatformConciseTableActivity;
import ru.protei.portal.ui.sitefolder.client.activity.plaform.table.concise.AbstractPlatformConciseTableView;

import java.util.ArrayList;
import java.util.List;

import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;

public class PlatformConciseTableView extends Composite implements AbstractPlatformConciseTableView {

    @Inject
    public void onInit(EditClickColumn<Platform> editClickColumn, RemoveClickColumn<Platform> removeClickColumn) {
        initWidget(ourUiBinder.createAndBindUi(this));
        this.editClickColumn = editClickColumn;
        this.removeClickColumn = removeClickColumn;
        initTable();
        setTestAttributes();
    }

    @Override
    public void setActivity(AbstractPlatformConciseTableActivity activity) {
        this.activity = activity;

        editClickColumn.setHandler(activity);
        editClickColumn.setEditHandler(activity);
        editClickColumn.setColumnProvider(columnProvider);
        editClickColumn.setEnabledPredicate(v -> policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_EDIT) );

        removeClickColumn.setHandler(activity);
        removeClickColumn.setRemoveHandler(activity);
        removeClickColumn.setColumnProvider(columnProvider);
        removeClickColumn.setEnabledPredicate(v -> policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_REMOVE) );

        columns.forEach(clickColumn -> {
            clickColumn.setHandler(activity);
            clickColumn.setColumnProvider(columnProvider);
        });
    }

    @Override
    public void clearRecords() {
        table.clearRows();
    }

    @Override
    public void setData(List<Platform> platforms) {
        for (Platform platform : platforms) {
            table.addRow(platform);
        }
    }

    private void initTable() {

        columns.add(name);
        columns.add(manager);
        columns.add(servers);

        table.addColumn(name.header, name.values);
        table.addColumn(manager.header, manager.values);
        table.addColumn(servers.header, servers.values);
        editColumn = table.addColumn(editClickColumn.header, editClickColumn.values);
        removeColumn = table.addColumn(removeClickColumn.header, removeClickColumn.values);
    }

    private void setTestAttributes() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.CONCISE_TABLE.PLATFORM);
    }

    @UiField
    TableWidget<Platform> table;

    @UiField
    HTMLPanel tableContainer;

    @Inject
    @UiField
    Lang lang;

    @Inject
    PolicyService policyService;

    ClickColumn<Platform> name = new ClickColumn<Platform>() {
        @Override
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.setInnerText(lang.siteFolderName());
        }
        @Override
        public void fillColumnValue(Element cell, Platform value) {
            cell.setInnerText(value.getName());
        }
    };

    ClickColumn<Platform> manager = new ClickColumn<Platform>() {
        @Override
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.setInnerText(lang.siteFolderManager());
        }
        @Override
        public void fillColumnValue(Element cell, Platform value) {
            cell.setInnerText(value.getManager() == null ? "" : value.getManager().getDisplayShortName());
        }
    };

    ClickColumn<Platform> servers = new ClickColumn<Platform>() {
        @Override
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.setInnerText(lang.siteFolderServers());
        }
        @Override
        public void fillColumnValue(Element cell, Platform value) {
            cell.setInnerText((value.getServersCount() == null ? "0" : String.valueOf(value.getServersCount())) + " " + lang.amountShort());
        }
    };


    ClickColumnProvider<Platform> columnProvider = new ClickColumnProvider<>();
    EditClickColumn<Platform> editClickColumn;
    RemoveClickColumn<Platform> removeClickColumn;
    List<ClickColumn> columns = new ArrayList<>();

    AbstractColumn editColumn;
    AbstractColumn removeColumn;
    AbstractPlatformConciseTableActivity activity;

    private static PlatformConciseTableViewUiBinder ourUiBinder = GWT.create(PlatformConciseTableViewUiBinder.class);
    interface PlatformConciseTableViewUiBinder extends UiBinder<HTMLPanel, PlatformConciseTableView> {}
}
