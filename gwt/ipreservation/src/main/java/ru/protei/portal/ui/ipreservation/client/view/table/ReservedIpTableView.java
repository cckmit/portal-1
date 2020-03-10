package ru.protei.portal.ui.ipreservation.client.view.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.TableWidget;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.ReservedIp;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.columns.*;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.ipreservation.client.activity.table.AbstractReservedIpTableActivity;
import ru.protei.portal.ui.ipreservation.client.activity.table.AbstractReservedIpTableView;

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
        editClickColumn.setEditHandler( activity );

        removeClickColumn.setHandler( activity );
        removeClickColumn.setRemoveHandler( activity );

        refreshClickColumn.setHandler( activity );
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

    private void initTable () {
        editClickColumn.setEnabledPredicate(v -> policyService.hasPrivilegeFor(En_Privilege.RESERVED_IP_EDIT) );
        refreshClickColumn.setEnabledPredicate(v -> policyService.hasPrivilegeFor(En_Privilege.RESERVED_IP_VIEW) );
        removeClickColumn.setEnabledPredicate(v -> policyService.hasPrivilegeFor(En_Privilege.RESERVED_IP_REMOVE) );

        columns.add(address);
        columns.add(owner);
        columns.add(usePeriod);
        columns.add(comment);
        columns.add(lastCheck);
        columns.add(refreshClickColumn);
        columns.add(editClickColumn);
        columns.add(removeClickColumn);

        columns.forEach(c -> table.addColumn(c.header, c.values));
    }

    private ClickColumn<ReservedIp> address = new ClickColumn<ReservedIp>() {
        @Override
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.setInnerText(lang.address());
        }

        @Override
        public void fillColumnValue(Element cell, ReservedIp value) {
            cell.setInnerText(value.getIpAddress() +
                    (value.getIpAddress() != null ? " [" + value.getMacAddress() + "]" : "")
            );
        }
    };

    private ClickColumn<ReservedIp> usePeriod = new ClickColumn<ReservedIp>() {
        @Override
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.setInnerText(lang.reservedIpUsePeriod());
        }

        @Override
        public void fillColumnValue(Element cell, ReservedIp value) {
            String reserved = value == null ? null : DateFormatter.formatDateTime(value.getReserveDate());
            String released = value == null ? null :
                    ( value.getReleaseDate() == null ?
                            lang.reservedIpForever() :
                            DateFormatter.formatDateTime(value.getReleaseDate()));
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
            String lastCheckDate = value == null || value.getLastCheckDate() == null ? null :
                    DateFormatter.formatDateTime(value.getLastCheckDate());
            cell.setInnerText(lastCheckDate + "<br>" + value.getLastCheckInfo());
        }
    };

    private ClickColumn<ReservedIp> owner = new ClickColumn<ReservedIp>() {
        @Override
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.setInnerText(lang.reservedIpOwner());
        }

        @Override
        public void fillColumnValue(Element cell, ReservedIp value) { cell.setInnerText(value.getOwner().getName()); }
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

    private AbstractReservedIpTableActivity activity;

    private List<ClickColumn> columns = new ArrayList<>();
    private ClickColumnProvider<ReservedIp> columnProvider = new ClickColumnProvider<>();

    private static ReservedIpTableViewUiBinder ourUiBinder = GWT.create( ReservedIpTableViewUiBinder.class );
    interface ReservedIpTableViewUiBinder extends UiBinder< HTMLPanel, ReservedIpTableView> {}
}