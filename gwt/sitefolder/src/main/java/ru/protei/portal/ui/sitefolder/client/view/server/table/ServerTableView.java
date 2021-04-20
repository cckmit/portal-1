package ru.protei.portal.ui.sitefolder.client.view.server.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.Server;
import ru.protei.portal.core.model.ent.ServerGroup;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.columns.*;
import ru.protei.portal.ui.common.client.events.InputEvent;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.cleanablesearchbox.CleanableSearchBox;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.SortFieldSelector;
import ru.protei.portal.ui.common.client.widget.table.GroupedTableWidget;
import ru.protei.portal.ui.sitefolder.client.activity.server.table.AbstractServerTableActivity;
import ru.protei.portal.ui.sitefolder.client.activity.server.table.AbstractServerTableView;
import ru.protei.portal.ui.sitefolder.client.column.AccessParamsColumn;
import ru.protei.portal.ui.sitefolder.client.column.AppsColumn;
import ru.protei.portal.ui.sitefolder.client.column.IpColumn;
import ru.protei.portal.ui.sitefolder.client.column.NameColumn;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ServerTableView extends Composite implements AbstractServerTableView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        initTable();
    }

    @Override
    public void addRecords(List<Server> servers) {
        table.addRecords(servers);
    }

    @Override
    public void setActivity(AbstractServerTableActivity activity) {
        this.activity = activity;
        copyClickColumn.setCopyHandler(activity);
        editClickColumn.setEditHandler(activity);
        removeClickColumn.setRemoveHandler(activity);
        appsColumn.setActionHandler(activity::onOpenAppsClicked);
        table.setGroupFunctions(activity);
    }

    @Override
    public HasValue<String> nameOrIp() {
        return nameOrIp;
    }

    @Override
    public HasValue<En_SortField> sortField() {
        return sortField;
    }

    @Override
    public HasValue<Boolean> sortDir() {
        return sortDir;
    }

    @Override
    public void clearRecords() {
        table.clearRows();
    }

    @Override
    public HasVisibility createButtonVisibility() {
        return createButton;
    }

    @UiHandler("nameOrIp")
    public void onNameOrIpChanged(InputEvent event) {
        nameChangeTimer.schedule(200);
    }

    @UiHandler("sortField")
    public void onSortFieldChanged(ValueChangeEvent<En_SortField> event) {
        activity.onFilterChanged();
    }

    @UiHandler("sortDir")
    public void onSortDirChanged(ValueChangeEvent<Boolean> event) {
        activity.onFilterChanged();
    }

    @UiHandler("createButton")
    public void onCreateButtonClicked(ClickEvent event) {
        activity.onCreateClicked();
    }

    private void initTable() {
        copyClickColumn.setEnabledPredicate(v -> policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_CREATE) );
        editClickColumn.setEnabledPredicate(v -> policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_EDIT) );
        removeClickColumn.setEnabledPredicate(v -> policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_REMOVE) );

        nameColumn.setCursorAuto(true);
        ip.setCursorAuto(true);
        appsColumn.setCursorAuto(true);
        accessParams.setCursorAuto(true);

        columns.add(nameColumn);
        columns.add(ip);
        columns.add(appsColumn);
        columns.add(accessParams);
        columns.add(copyClickColumn);
        columns.add(editClickColumn);
        columns.add(removeClickColumn);

        columns.forEach(c -> table.addColumn(c.header, c.values));
    }

    @UiField
    GroupedTableWidget<Server, ServerGroup> table;

    @UiField
    CleanableSearchBox nameOrIp;

    @Inject
    @UiField( provided = true )
    SortFieldSelector sortField;

    @UiField
    ToggleButton sortDir;

    @UiField
    Button createButton;

    @Inject
    @UiField
    Lang lang;

    @Inject
    PolicyService policyService;

    @Inject
    private CopyClickColumn<Server> copyClickColumn;
    @Inject
    private EditClickColumn<Server> editClickColumn;
    @Inject
    private RemoveClickColumn<Server> removeClickColumn;
    @Inject
    private NameColumn nameColumn;
    @Inject
    private IpColumn ip;
    @Inject
    private AppsColumn appsColumn;
    @Inject
    private AccessParamsColumn accessParams;

    private Timer nameChangeTimer = new Timer() {
        @Override
        public void run() {
            activity.onFilterChanged();
        }
    };

    private AbstractServerTableActivity activity;


    private Collection<ClickColumn<Server>> columns = new LinkedList<>();
    private ClickColumnProvider<Server> columnProvider = new ClickColumnProvider<>();

    interface ServerTableViewUiBinder extends UiBinder<HTMLPanel, ServerTableView> {}
    private static ServerTableViewUiBinder ourUiBinder = GWT.create(ServerTableViewUiBinder.class);
}
