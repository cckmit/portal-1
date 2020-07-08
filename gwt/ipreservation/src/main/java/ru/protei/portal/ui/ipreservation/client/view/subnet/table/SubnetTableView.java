package ru.protei.portal.ui.ipreservation.client.view.subnet.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.TableWidget;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Subnet;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.columns.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.ipreservation.client.activity.subnet.table.AbstractSubnetTableActivity;
import ru.protei.portal.ui.ipreservation.client.activity.subnet.table.AbstractSubnetTableView;
import ru.protei.portal.ui.ipreservation.client.view.subnet.table.columns.AddressColumn;

import java.util.ArrayList;
import java.util.List;

/**
 * Представление таблицы подсетей
 */
public class SubnetTableView extends Composite implements AbstractSubnetTableView {

    @Inject
    public void onInit(EditClickColumn<Subnet> editClickColumn,
                       RefreshClickColumn<Subnet> refreshClickColumn,
                       RemoveClickColumn<Subnet> removeClickColumn) {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        this.editClickColumn = editClickColumn;
        this.refreshClickColumn = refreshClickColumn;
        this.removeClickColumn = removeClickColumn;
        initTable();
    }

    @Override
    public void setActivity( AbstractSubnetTableActivity activity ) {
        this.activity = activity;

        editClickColumn.setEditHandler( activity );
        removeClickColumn.setRemoveHandler( activity );
        refreshClickColumn.setRefreshHandler( activity );

        columns.forEach(clickColumn -> {
            clickColumn.setHandler( activity );
            clickColumn.setColumnProvider( columnProvider );
        });
    }

    @Override
    public void clearRecords() {
        table.clearRows();
    }

    @Override
    public void clearSelection() {
        columnProvider.setSelectedValue(null);
    }

    @Override
    public void addRow(Subnet row) {
        table.addRow(row);
    }

    @Override
    public void updateRow(Subnet subnet) {
        table.updateRow(subnet);
    }

    @Override
    public void setAnimation ( TableAnimation animation ) {
        animation.setContainers( tableContainer, previewContainer, filterContainer );
        animation.setStyles("col-md-12", "col-md-9", "col-md-3", "col-md-8", "col-md-4");
    }

    @Override
    public HTMLPanel getFilterContainer () { return filterContainer; }

    @Override
    public HTMLPanel getPreviewContainer () { return previewContainer; }

    private void initTable () {
        editClickColumn.setEnabledPredicate(v -> policyService.hasPrivilegeFor(En_Privilege.SUBNET_EDIT) );
        refreshClickColumn.setEnabledPredicate(v -> policyService.hasPrivilegeFor(En_Privilege.SUBNET_VIEW) );
        removeClickColumn.setEnabledPredicate(v -> policyService.hasPrivilegeFor(En_Privilege.SUBNET_REMOVE) );

        ClickColumn<Subnet> local = new ClickColumn<Subnet>() {
            @Override
            protected void fillColumnHeader(Element columnHeader) {
            }
            @Override
            public void fillColumnValue(Element cell, Subnet value) {
                Element lock = DOM.createElement( "i" );
                lock.addClassName( value.isLocal() ?
                        "fas fa-network-wired fa-lg text-primary" :
                        "fas fa-network-wired fa-lg text-warning-dark" );
                cell.appendChild( lock );
            }
        };

        address = new AddressColumn( lang );

        ClickColumn<Subnet> creator = new ClickColumn<Subnet>() {
            @Override
            protected void fillColumnHeader(Element columnHeader) {
                columnHeader.addClassName("creator");
                columnHeader.setInnerText(lang.reservedIpCreateBy());
            }
            @Override
            public void fillColumnValue(Element cell, Subnet value) {
                cell.addClassName("creator");
                cell.setInnerText(value.getCreator());
            }
        };

        ClickColumn<Subnet> comment = new ClickColumn<Subnet>() {
            @Override
            protected void fillColumnHeader(Element columnHeader) {
                columnHeader.addClassName("comment");
                columnHeader.setInnerText(lang.comment());
            }
            @Override
            public void fillColumnValue(Element cell, Subnet value) {
                cell.addClassName("comment");
                cell.setInnerText(value.getComment());
            }
        };

        ClickColumn<Subnet> state = new ClickColumn<Subnet>() {
            @Override
            protected void fillColumnHeader(Element columnHeader) {
                columnHeader.addClassName("ip-count");
                columnHeader.setInnerText(lang.reservedIpState());
            }
            @Override
            public void fillColumnValue(Element cell, Subnet value) {
                cell.addClassName("ip-count");

                Element use = DOM.createElement( "span" );
                use.addClassName("text-danger");
                use.setInnerText(String.valueOf(value.getReservedIPs()));
                cell.appendChild( use );

                Element separator = DOM.createElement( "span" );
                separator.setInnerText(" | ");
                cell.appendChild(separator);

                Element free = DOM.createElement( "span" );
                free.addClassName("text-success");
                free.setInnerText(String.valueOf(value.getFreeIps()));
                cell.appendChild( free );
            }
        };

        columns.add(local);
        columns.add(address);
        columns.add(creator);
        columns.add(comment);
        columns.add(state);
        columns.add(editClickColumn);
        columns.add(refreshClickColumn);
        columns.add(removeClickColumn);

        columns.forEach(clickColumn -> {
            table.addColumn(clickColumn.header, clickColumn.values);
        });
    }

    @UiField
    TableWidget<Subnet> table;
    @UiField
    HTMLPanel tableContainer;
    @UiField
    HTMLPanel previewContainer;
    @UiField
    HTMLPanel filterContainer;
    @UiField
    Lang lang;

    @Inject
    PolicyService policyService;

    @Inject
    EditClickColumn<Subnet> editClickColumn;
    @Inject
    RefreshClickColumn<Subnet> refreshClickColumn;
    @Inject
    RemoveClickColumn<Subnet> removeClickColumn;

    AddressColumn address;

    private AbstractSubnetTableActivity activity;

    private List<ClickColumn> columns = new ArrayList<>();
    private ClickColumnProvider<Subnet> columnProvider = new ClickColumnProvider<>();

    private static SubnetTableViewUiBinder ourUiBinder = GWT.create( SubnetTableViewUiBinder.class );
    interface SubnetTableViewUiBinder extends UiBinder< HTMLPanel, SubnetTableView> {}
}