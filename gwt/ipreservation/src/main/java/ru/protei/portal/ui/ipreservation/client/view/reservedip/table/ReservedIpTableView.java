package ru.protei.portal.ui.ipreservation.client.view.reservedip.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.AbstractColumn;
import ru.brainworm.factory.widget.table.client.TableWidget;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.ReservedIp;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.columns.*;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.ipreservation.client.activity.reservedip.table.AbstractReservedIpTableActivity;
import ru.protei.portal.ui.ipreservation.client.activity.reservedip.table.AbstractReservedIpTableView;

import java.util.ArrayList;
import java.util.List;

/**
 * Представление таблицы зарезервированных IP
 */
public class ReservedIpTableView extends Composite implements AbstractReservedIpTableView {

    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        initTable();
    }

    @Override
    public void setActivity( AbstractReservedIpTableActivity activity ) {
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
        usePeriod.setHandler( activity );
        usePeriod.setColumnProvider( columnProvider );
        owner.setHandler( activity );
        owner.setColumnProvider( columnProvider );
        comment.setHandler( activity );
        comment.setColumnProvider( columnProvider );
        lastCheck.setHandler( activity );
        lastCheck.setColumnProvider( columnProvider );
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
    public void addRow(ReservedIp row) {
        table.addRow(row);
    }

    @Override
    public void updateRow(ReservedIp reservedIp) {
        table.updateRow(reservedIp);
    }

    @Override
    public void setAnimation ( TableAnimation animation ) {
        animation.setContainers( tableContainer, previewContainer, filterContainer );
        animation.setStyles("col-md-12", "col-md-9", "col-md-3", "col-md-6", "col-md-6");
    }

    @Override
    public HTMLPanel getFilterContainer () { return filterContainer; }

    @Override
    public HTMLPanel getPreviewContainer () { return previewContainer; }

/*    @Override
    public void addSeparator( String text ) {
        Element elem = DOM.createDiv();
        SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
        safeHtmlBuilder.appendHtmlConstant( "<b>" ).appendEscapedLines( text ).appendHtmlConstant( "</b>" );
        elem.setInnerSafeHtml( safeHtmlBuilder.toSafeHtml() );
        table.addCustomRow( elem, "subnet", null );
    }*/

    @Override
    public void hideElements() {
        hideComment.setVisibility( false );
        hideOwner.setVisibility( false );
    }

    @Override
    public void showElements() {
        hideComment.setVisibility( true );
        hideOwner.setVisibility( true );
    }


    private void initTable () {
        editClickColumn.setEnabledPredicate(v -> policyService.hasPrivilegeFor(En_Privilege.RESERVED_IP_EDIT) );
        refreshClickColumn.setEnabledPredicate(v -> policyService.hasPrivilegeFor(En_Privilege.RESERVED_IP_VIEW) );
        removeClickColumn.setEnabledPredicate(v -> policyService.hasPrivilegeFor(En_Privilege.RESERVED_IP_REMOVE) );

        table.addColumn(address.header, address.values);
        table.addColumn(usePeriod.header, usePeriod.values);
        hideOwner = table.addColumn( owner.header, owner.values );
        hideComment = table.addColumn( comment.header, comment.values );
        table.addColumn(lastCheck.header, lastCheck.values);
        table.addColumn(editClickColumn.header, editClickColumn.values);
        table.addColumn(refreshClickColumn.header, refreshClickColumn.values);
        table.addColumn(removeClickColumn.header, removeClickColumn.values);
    }

    private ClickColumn<ReservedIp> address = new ClickColumn<ReservedIp>() {
        @Override
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.setInnerText(lang.address());
        }

        @Override
        public void fillColumnValue(Element cell, ReservedIp value) {
            if ( value == null ) { return; }

            cell.addClassName( "number" );
            com.google.gwt.dom.client.Element divElement = DOM.createDiv();

            com.google.gwt.dom.client.Element ipElement = DOM.createElement( "p" );
            ipElement.addClassName( "number-size" );
            ipElement.setInnerText( value.getIpAddress() );
            divElement.appendChild( ipElement );

            if (value.getMacAddress() != null) {
                com.google.gwt.dom.client.Element macElement = DOM.createElement("p");
                macElement.setInnerText( "[" + value.getMacAddress() + "]");
                divElement.appendChild(macElement);
            }
            cell.appendChild( divElement );
        }
    };

    private ClickColumn<ReservedIp> usePeriod = new ClickColumn<ReservedIp>() {
        @Override
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.setInnerText(lang.reservedIpUsePeriod());
        }

        @Override
        public void fillColumnValue(Element cell, ReservedIp value) {
            String reserved = value == null ? null : DateFormatter.formatDateOnly(value.getReserveDate());
            String released = value == null ? null :
                    ( value.getReleaseDate() == null ?
                            lang.reservedIpForever() :
                            DateFormatter.formatDateOnly(value.getReleaseDate()));
            cell.setInnerText(reserved + " - " + released);
        }
    };

    private ClickColumn<ReservedIp> lastCheck = new ClickColumn<ReservedIp>() {
        @Override
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.setInnerText(lang.reservedIpCheck());
        }

        @Override
        public void fillColumnValue(Element cell, ReservedIp value) {
            if ( value == null ) { return; }

            com.google.gwt.dom.client.Element divElement = DOM.createDiv();

            if (value.getLastCheckDate() != null) {
                com.google.gwt.dom.client.Element checkDateElement = DOM.createElement("p");
                checkDateElement.setInnerText(DateFormatter.formatDateTime(value.getLastCheckDate()));
                divElement.appendChild(checkDateElement);
            }
            if (value.getLastCheckInfo() != null) {

                com.google.gwt.dom.client.Element checkInfoElement = DOM.createElement("p");
                checkInfoElement.setInnerText( value.getLastCheckInfo() );
                divElement.appendChild(checkInfoElement);
            }

            cell.appendChild( divElement );
        }
    };

    private ClickColumn<ReservedIp> owner = new ClickColumn<ReservedIp>() {
        @Override
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.setInnerText(lang.reservedIpOwner());
        }

        @Override
        public void fillColumnValue(Element cell, ReservedIp value) { cell.setInnerText(value.getOwner().getDisplayName()); }
    };

    private ClickColumn<ReservedIp> comment = new ClickColumn<ReservedIp>() {
        @Override
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.setInnerText(lang.comment());
        }

        @Override
        public void fillColumnValue(Element cell, ReservedIp value) {
            cell.setInnerText(value.getComment());
        }
    };

    @UiField
    TableWidget<ReservedIp> table;
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
    EditClickColumn<ReservedIp> editClickColumn;
    @Inject
    RefreshClickColumn<ReservedIp> refreshClickColumn;
    @Inject
    RemoveClickColumn<ReservedIp> removeClickColumn;

    AbstractColumn hideComment;
    AbstractColumn hideOwner;

    private AbstractReservedIpTableActivity activity;

    private List<ClickColumn> columns = new ArrayList<>();
    private ClickColumnProvider<ReservedIp> columnProvider = new ClickColumnProvider<>();

    private static ReservedIpTableViewUiBinder ourUiBinder = GWT.create( ReservedIpTableViewUiBinder.class );
    interface ReservedIpTableViewUiBinder extends UiBinder< HTMLPanel, ReservedIpTableView> {}
}