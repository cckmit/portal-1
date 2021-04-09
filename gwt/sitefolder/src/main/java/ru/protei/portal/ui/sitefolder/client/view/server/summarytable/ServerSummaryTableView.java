package ru.protei.portal.ui.sitefolder.client.view.server.summarytable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Server;
import ru.protei.portal.core.model.ent.ServerGroup;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.columns.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.table.GroupedTableWidget;
import ru.protei.portal.ui.sitefolder.client.activity.server.summarytable.AbstractServerSummaryTableActivity;
import ru.protei.portal.ui.sitefolder.client.activity.server.summarytable.AbstractServerSummaryTableView;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ServerSummaryTableView extends Composite implements AbstractServerSummaryTableView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        initTable();
    }

    @Override
    public void setActivity(AbstractServerSummaryTableActivity activity) {
        copyClickColumn.setCopyHandler(activity);
        editClickColumn.setEditHandler(activity);
        removeClickColumn.setRemoveHandler(activity);
        columns.forEach(c -> {
            c.setHandler(activity);
            c.setColumnProvider(columnProvider);
        });
        appsColumn.setActionHandler(activity::onOpenAppsClicked);
        table.setGroupFunctions(activity);
    }

    @Override
    public void setAnimation(TableAnimation animation) {
        animation.setContainers(tableContainer, previewContainer, filterContainer);
        columnProvider.setChangeSelectionIfSelectedPredicate(server -> animation.isPreviewShow());
    }

    @Override
    public void clearRecords() {
        table.clearRows();
    }

    @Override
    public void addRecords(List<Server> servers) {
        table.addRecords(servers);
    }

    @Override
    public void updateRow(Server item) {
        if (item != null) {
            table.updateRow(item);
        }
    }

    @Override
    public HasWidgets getPreviewContainer() {
        return previewContainer;
    }

    @Override
    public HasWidgets getFilterContainer() {
        return filterContainer;
    }

    @Override
    public HasWidgets getPagerContainer() {
        return pagerContainer;
    }

    @Override
    public void clearSelection() {
        columnProvider.removeSelection();
    }

    private void initTable() {
        copyClickColumn.setEnabledPredicate(v -> policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_CREATE) );
        editClickColumn.setEnabledPredicate(v -> policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_EDIT) );
        removeClickColumn.setEnabledPredicate(v -> policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_REMOVE) );

        columns.add(nameColumn);
        columns.add(platformColumn);
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
    HTMLPanel tableContainer;
    @UiField
    HTMLPanel previewContainer;
    @UiField
    HTMLPanel filterContainer;
    @UiField
    HTMLPanel pagerContainer;

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
    private ClickColumn<Server> nameColumn = new ClickColumn<Server>() {
        @Override
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.setClassName("server-name");
            columnHeader.setInnerText(lang.siteFolderName());
        }
        @Override
        public void fillColumnValue(Element cell, Server value) {
            Element element = DOM.createDiv();
            element.setInnerText(value.getName());
            cell.appendChild(element);
        }
    };

    private ClickColumn<Server> ip = new ClickColumn<Server>() {
        @Override
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.addClassName("server-ip column-hidable");
            columnHeader.setInnerText(lang.siteFolderIP());
        }
        @Override
        public void fillColumnValue(Element cell, Server value) {
            Element element = DOM.createDiv();

            element.addClassName("column-hidable");
            element.setInnerText(value.getIp());

            cell.appendChild(element);
        }
    };

    private ClickColumn<Server> accessParams = new ClickColumn<Server>() {
        @Override
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.addClassName("server-access-params column-hidable");
            columnHeader.setInnerText(lang.serverAccessParamsColumn());
        }
        @Override
        public void fillColumnValue(Element cell, Server value) {
            Element element = DOM.createDiv();

            element.addClassName("column-hidable");
            element.setInnerText(value.getParams());

            cell.appendChild(element);
        }
    };
    private ClickColumn<Server> platformColumn = new ClickColumn<Server>() {
        @Override
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.setClassName("server-platform");
            columnHeader.setInnerText(lang.siteFolderPlatform());
        }

        @Override
        public void fillColumnValue(Element cell, Server value) {
            Element element = DOM.createDiv();

            element.setInnerText(value.getPlatform() == null ? "?" : value.getPlatform().getName());

            cell.appendChild(element);
        }
    };
    private ClickColumn<Server> appsColumn = new ClickColumn<Server>() {
        @Override
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.setClassName("server-apps");
            columnHeader.setInnerText(lang.siteFolderApps());
        }

        @Override
        public void fillColumnValue(Element cell, Server value) {
            Element element = DOM.createDiv();

            element.setInnerText((value.getApplicationsCount() == null ? "0" : String.valueOf(value.getApplicationsCount())) + " " +lang.amountShort());
            AnchorElement a = DOM.createAnchor().cast();
            a.setHref("#");
            a.addClassName("fa fa-share cell-inline-icon");
            a.setTitle(lang.siteFolderApps());
            element.appendChild(a);

            cell.appendChild(element);
        }
    };
    private Collection<ClickColumn<Server>> columns = new LinkedList<>();
    private ClickColumnProvider<Server> columnProvider = new ClickColumnProvider<>();

    interface SiteFolderServerTableViewUiBinder extends UiBinder<HTMLPanel, ServerSummaryTableView> {}
    private static SiteFolderServerTableViewUiBinder ourUiBinder = GWT.create(SiteFolderServerTableViewUiBinder.class);
}
