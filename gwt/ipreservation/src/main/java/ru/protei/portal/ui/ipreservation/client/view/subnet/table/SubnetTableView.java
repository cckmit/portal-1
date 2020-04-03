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

import java.util.ArrayList;
import java.util.List;

/**
 * Представление таблицы подсетей
 */
public class SubnetTableView extends Composite implements AbstractSubnetTableView {

    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        initTable();
    }

    @Override
    public void setActivity( AbstractSubnetTableActivity activity ) {
        this.activity = activity;

        editClickColumn.setHandler( activity );
        editClickColumn.setColumnProvider( columnProvider );
        editClickColumn.setEditHandler( activity );

        removeClickColumn.setHandler( activity );
        removeClickColumn.setColumnProvider( columnProvider );
        removeClickColumn.setRemoveHandler( activity );

        refreshClickColumn.setHandler( activity );
        refreshClickColumn.setColumnProvider( columnProvider );
        refreshClickColumn.setRefreshHandler( activity );

        address.setHandler( activity );
        address.setColumnProvider( columnProvider );
        comment.setHandler( activity );
        comment.setColumnProvider( columnProvider );
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

        table.addColumn(address.header, address.values);
        table.addColumn(comment.header, comment.values);
        table.addColumn(editClickColumn.header, editClickColumn.values);
        table.addColumn(refreshClickColumn.header, refreshClickColumn.values);
        table.addColumn(removeClickColumn.header, removeClickColumn.values);
    }

    private ClickColumn<Subnet> address = new ClickColumn<Subnet>() {
        @Override
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.setInnerText(lang.address());
        }

        @Override
        public void fillColumnValue(Element cell, Subnet value) {
            if ( value == null ) { return; }

            cell.addClassName( "number" );
            com.google.gwt.dom.client.Element divElement = DOM.createDiv();

            com.google.gwt.dom.client.Element addrElement = DOM.createElement( "p" );
            addrElement.addClassName( "number-size" );
	        String address = value.getAddress();

            if (value.getMask() != null) {
                address += "." + value.getMask();
            }
            addrElement.setInnerText( address );
            divElement.appendChild( addrElement );
            cell.appendChild( divElement );
        }
    };

    private ClickColumn<Subnet> comment = new ClickColumn<Subnet>() {
        @Override
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.setInnerText(lang.comment());
        }

        @Override
        public void fillColumnValue(Element cell, Subnet value) {
            cell.setInnerText(value.getComment());
        }
    };

    private ClickColumn<Subnet> state = new ClickColumn<Subnet>() {
        @Override
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.setInnerText(lang.reservedIpState());
        }

        @Override
        public void fillColumnValue(Element cell, Subnet value) {
            cell.setInnerText("Registered : " + value.getRegisteredIPs() + "  "
                    + " Free : " + value.getFreeIps());
        }
    };

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

    private AbstractSubnetTableActivity activity;

    private List<ClickColumn> columns = new ArrayList<>();
    private ClickColumnProvider<Subnet> columnProvider = new ClickColumnProvider<>();

    private static SubnetTableViewUiBinder ourUiBinder = GWT.create( SubnetTableViewUiBinder.class );
    interface SubnetTableViewUiBinder extends UiBinder< HTMLPanel, SubnetTableView> {}
}