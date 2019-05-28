package ru.protei.portal.ui.sitefolder.client.view.server.table;

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
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Server;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.columns.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.sitefolder.client.activity.server.table.AbstractServerTableActivity;
import ru.protei.portal.ui.sitefolder.client.activity.server.table.AbstractServerTableView;

import java.util.Collection;
import java.util.LinkedList;

public class ServerTableView extends Composite implements AbstractServerTableView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        initTable();
    }

    @Override
    public void setActivity(AbstractServerTableActivity activity) {
        copyClickColumn.setCopyHandler(activity);
        editClickColumn.setEditHandler(activity);
        removeClickColumn.setRemoveHandler(activity);
        table.setLoadHandler(activity);
        table.setPagerListener(activity);
        columns.forEach(c -> {
            c.setHandler(activity);
            c.setColumnProvider(columnProvider);
        });
        appsColumn.setActionHandler(activity::onOpenAppsClicked);
    }

    @Override
    public void setAnimation(TableAnimation animation) {
        animation.setContainers(tableContainer, previewContainer, filterContainer);
    }

    @Override
    public void clearRecords() {
        table.clearCache();
        table.clearRows();
    }

    @Override
    public void triggerTableLoad() {
        table.setTotalRecords(table.getPageSize());
    }

    @Override
    public void setTotalRecords(int totalRecords) {
        table.setTotalRecords(totalRecords);
    }

    @Override
    public int getPageCount() {
        return table.getPageCount();
    }

    @Override
    public void scrollTo(int page) {
        table.scrollToPage(page);
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


    private void initTable() {
        copyClickColumn.setPrivilege(En_Privilege.SITE_FOLDER_CREATE);
        editClickColumn.setPrivilege(En_Privilege.SITE_FOLDER_EDIT);
        removeClickColumn.setPrivilege(En_Privilege.SITE_FOLDER_REMOVE);

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
    InfiniteTableWidget<Server> table;
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
    private CopyClickColumn<Server> copyClickColumn;
    @Inject
    private EditClickColumn<Server> editClickColumn;
    @Inject
    private RemoveClickColumn<Server> removeClickColumn;
    private ClickColumn<Server> nameColumn = new ClickColumn<Server>() {
        @Override
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.setInnerText(lang.siteFolderName());
        }
        @Override
        public void fillColumnValue(Element cell, Server value) {
            cell.setInnerText(value.getName());
        }
    };

    private ClickColumn<Server> ip = new ClickColumn<Server>() {
        @Override
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.addClassName("column-hidable");
            columnHeader.setInnerText(lang.siteFolderIP());
        }
        @Override
        public void fillColumnValue(Element cell, Server value) {
            cell.addClassName("column-hidable");
            cell.setInnerText(value.getIp());
        }
    };

    private ClickColumn<Server> accessParams = new ClickColumn<Server>() {
        @Override
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.addClassName("column-hidable");
            columnHeader.setInnerText(lang.serverAccessParamsColumn());
        }
        @Override
        public void fillColumnValue(Element cell, Server value) {
            cell.addClassName("column-hidable");
            cell.setInnerText(value.getParams());
        }
    };
    private ClickColumn<Server> platformColumn = new ClickColumn<Server>() {
        @Override
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.setInnerText(lang.siteFolderPlatform());
        }

        @Override
        public void fillColumnValue(Element cell, Server value) {
            cell.setInnerText(value.getPlatform() == null ? "?" : value.getPlatform().getName());
        }
    };
    private ClickColumn<Server> appsColumn = new ClickColumn<Server>() {
        @Override
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.setInnerText(lang.siteFolderApps());
        }

        @Override
        public void fillColumnValue(Element cell, Server value) {
            cell.setInnerText((value.getApplicationsCount() == null ? "0" : String.valueOf(value.getApplicationsCount())) + " " +lang.amountShort());
            AnchorElement a = DOM.createAnchor().cast();
            a.setHref("#");
            a.addClassName("fa fa-share cell-inline-icon");
            a.setTitle(lang.siteFolderApps());
            cell.appendChild(a);
        }
    };
    private Collection<ClickColumn<Server>> columns = new LinkedList<>();
    private ClickColumnProvider<Server> columnProvider = new ClickColumnProvider<>();

    interface SiteFolderServerTableViewUiBinder extends UiBinder<HTMLPanel, ServerTableView> {}
    private static SiteFolderServerTableViewUiBinder ourUiBinder = GWT.create(SiteFolderServerTableViewUiBinder.class);
}
