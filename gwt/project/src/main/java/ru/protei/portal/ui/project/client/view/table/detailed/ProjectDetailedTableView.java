package ru.protei.portal.ui.project.client.view.table.detailed;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.TableWidget;
import ru.protei.portal.core.model.struct.ProjectInfo;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.columns.StaticColumn;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.lang.En_CustomerTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.project.client.activity.table.detailed.AbstractProjectDetailedTableActivity;
import ru.protei.portal.ui.project.client.activity.table.detailed.AbstractProjectDetailedTableView;
import ru.protei.portal.ui.project.client.view.table.columns.InfoColumn;
import ru.protei.portal.ui.project.client.view.table.columns.ManagersColumn;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectDetailedTableView extends Composite implements AbstractProjectDetailedTableView {
    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        initTable();
    }

    @Override
    public void setActivity(AbstractProjectDetailedTableActivity activity) {
        this.activity = activity;
        created.setHandler(activity);
        created.setColumnProvider(columnProvider);
        info.setHandler(activity);
        info.setColumnProvider(columnProvider);
        products.setHandler(activity);
        products.setColumnProvider(columnProvider);
        customerType.setHandler(activity);
        customerType.setColumnProvider(columnProvider);
        managers.setHandler(activity);
        managers.setColumnProvider(columnProvider);
    }

    @Override
    public void addRecords(List< ProjectInfo > projects) {
        projects.forEach(project -> table.addRow(project));
    }

    @Override
    public void clearRecords() {
        table.clearRows();
    }

    private void initTable() {

        created = new ClickColumn< ProjectInfo>() {
            @Override
            protected void fillColumnHeader(Element columnHeader) {
                columnHeader.addClassName("created");
                columnHeader.setInnerText(lang.issueReportsInfo());
            }

            @Override
            public void fillColumnValue(Element cell, ProjectInfo value) {
                cell.addClassName("created");
                Date created = value == null ? null : value.getCreated();
                cell.setInnerText(created == null ? "" : DateFormatter.formatDateTime(created));
            }
        };

        customerType = new ClickColumn<ProjectInfo>() {
            @Override
            protected void fillColumnHeader(Element columnHeader) {
                columnHeader.addClassName("customer-type");
                columnHeader.setInnerText(lang.issueReportsInfo());
            }

            @Override
            public void fillColumnValue(Element cell, ProjectInfo value) {
                cell.addClassName("customer-type");
                cell.setInnerText(customerTypeLang.getName(value.getCustomerType()));
            }
        };

        products = new ClickColumn<ProjectInfo>() {
            @Override
            protected void fillColumnHeader(Element columnHeader) {
                columnHeader.addClassName("products");
                columnHeader.setInnerText(lang.issueReportsInfo());
            }

            @Override
            public void fillColumnValue(Element cell, ProjectInfo value) {
                cell.addClassName("products");
                cell.setInnerText(value.getProducts().stream().map(product -> product.getName()).collect( Collectors.joining(",")));
            }
        };
        table.addColumn(created.header, created.values);
        table.addColumn(info.header, info.values);
        table.addColumn(products.header, products.values);
        table.addColumn(customerType.header, customerType.values);
        table.addColumn(managers.header, managers.values);
    }

    @UiField
    TableWidget table;

    @Inject
    @UiField
    Lang lang;

    @Inject
    En_CustomerTypeLang customerTypeLang;

    @Inject
    InfoColumn info;

    @Inject
    ManagersColumn managers;

    ClickColumn<ProjectInfo> created;
    ClickColumn<ProjectInfo> customerType;
    ClickColumn<ProjectInfo> products;

    ClickColumnProvider<ProjectInfo> columnProvider = new ClickColumnProvider<>();

    AbstractProjectDetailedTableActivity activity;

    private static ProjectDetailedTableViewUiBinder ourUiBinder = GWT.create( ProjectDetailedTableViewUiBinder.class );
    interface ProjectDetailedTableViewUiBinder extends UiBinder< HTMLPanel, ProjectDetailedTableView > {}
}