package ru.protei.portal.ui.ipreservation.client.view.reservedip.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.AbstractColumn;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.ReservedIp;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.columns.*;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.ipreservation.client.activity.reservedip.table.AbstractReservedIpTableActivity;
import ru.protei.portal.ui.ipreservation.client.activity.reservedip.table.AbstractReservedIpTableView;
import ru.protei.portal.ui.ipreservation.client.view.reservedip.table.columns.AddressColumn;
import ru.protei.portal.ui.ipreservation.client.view.reservedip.table.columns.LastCheckColumn;

import java.util.ArrayList;
import java.util.List;

/**
 * Представление таблицы зарезервированных IP
 */
public class ReservedIpTableView extends Composite implements AbstractReservedIpTableView {

    @Inject
    public void onInit(EditClickColumn<ReservedIp> editClickColumn,
                       RefreshClickColumn<ReservedIp> refreshClickColumn,
                       RemoveClickColumn<ReservedIp> removeClickColumn) {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        this.editClickColumn = editClickColumn;
        this.refreshClickColumn = refreshClickColumn;
        this.removeClickColumn = removeClickColumn;
        initTable();
    }

    @Override
    public void setActivity( AbstractReservedIpTableActivity activity ) {
        this.activity = activity;

        editClickColumn.setEditHandler( activity );
        removeClickColumn.setRemoveHandler( activity );
        refreshClickColumn.setRefreshHandler( activity );

        columns.forEach(clickColumn -> {
            clickColumn.setHandler( activity );
            clickColumn.setColumnProvider( columnProvider );
        });

        table.setLoadHandler(activity);
        table.setPagerListener( activity );
    }

    @Override
    public void clearRecords() {
        table.clearCache();
        table.clearRows();
    }

    @Override
    public void clearSelection() {
        columnProvider.setSelectedValue(null);
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
    public void triggerTableLoad() {
        table.setTotalRecords(table.getPageSize());
    }

    @Override
    public void setTotalRecords(int totalRecords) {
        table.setTotalRecords(totalRecords);
    }

    @Override
    public int getPageCount() { return table.getPageCount(); }

    @Override
    public void scrollTo(int page) { table.scrollToPage(page); }

    @Override
    public HTMLPanel getFilterContainer () { return filterContainer; }

    @Override
    public HTMLPanel getPreviewContainer () { return previewContainer; }

    @Override
    public HasWidgets getPagerContainer() { return pagerContainer; }

    @Override
    public void hideElements() {
        hideComment.setVisibility( false );
        hideLastCheck.setVisibility(false);
    }

    @Override
    public void showElements() {
        hideComment.setVisibility( true );
        hideLastCheck.setVisibility( true );
    }

    private void initTable () {
        editClickColumn.setEnabledPredicate(v -> policyService.hasPrivilegeFor(En_Privilege.RESERVED_IP_EDIT) );
        refreshClickColumn.setEnabledPredicate(v -> policyService.hasPrivilegeFor(En_Privilege.RESERVED_IP_VIEW) );
        removeClickColumn.setEnabledPredicate(v -> policyService.hasPrivilegeFor(En_Privilege.RESERVED_IP_REMOVE) );

        address = new AddressColumn( lang );
        lastCheck = new LastCheckColumn( lang );

        ClickColumn<ReservedIp> usePeriod = new ClickColumn<ReservedIp>() {
            @Override
            protected void fillColumnHeader(Element columnHeader) {
                columnHeader.addClassName( "ip-use-range" );
                columnHeader.setInnerText(lang.reservedIpUsePeriod());
            }

            @Override
            public void fillColumnValue(Element cell, ReservedIp value) {
                cell.addClassName( "ip-use-range" );
                String reserved = value == null ? null : DateFormatter.formatDateOnly(value.getReserveDate());
                String released = value == null ? null :
                        ( value.getReleaseDate() == null ?
                                lang.reservedIpForever() :
                                DateFormatter.formatDateOnly(value.getReleaseDate()));
                cell.setInnerText(reserved + " - " + released);
            }
        };

        ClickColumn<ReservedIp> ipOwner = new ClickColumn<ReservedIp>() {
            @Override
            protected void fillColumnHeader(Element columnHeader) {
                columnHeader.setInnerText(lang.reservedIpOwner());
            }

            @Override
            public void fillColumnValue(Element cell, ReservedIp value) {
                cell.setInnerText(value.getOwnerShortName());
            }
        };

        ClickColumn<ReservedIp> comment = new ClickColumn<ReservedIp>() {
            @Override
            protected void fillColumnHeader(Element columnHeader) {
                columnHeader.addClassName( "ip-comment" );
                columnHeader.setInnerText(lang.comment());
            }

            @Override
            public void fillColumnValue(Element cell, ReservedIp value) {
                cell.addClassName( "ip-comment" );

                if ( value == null ) { return; }

                cell.setInnerText( StringUtils.isBlank(value.getComment()) ?
                                   "" : value.getComment() );
            }
        };

        columns.add(address);
        columns.add(usePeriod);
        columns.add(ipOwner);
        columns.add(comment);
        columns.add(lastCheck);
        columns.add(editClickColumn);
        columns.add(refreshClickColumn);
        columns.add(removeClickColumn);

        table.addColumn(address.header, address.values);
        table.addColumn(usePeriod.header, usePeriod.values);
        table.addColumn(ipOwner.header, ipOwner.values);
        hideComment = table.addColumn( comment.header, comment.values );
        hideLastCheck = table.addColumn( lastCheck.header, lastCheck.values );
        table.addColumn(editClickColumn.header, editClickColumn.values);
        table.addColumn(refreshClickColumn.header, refreshClickColumn.values);
        table.addColumn(removeClickColumn.header, removeClickColumn.values);
    }

    @UiField
    InfiniteTableWidget<ReservedIp> table;
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
    EditClickColumn<ReservedIp> editClickColumn;
    @Inject
    RefreshClickColumn<ReservedIp> refreshClickColumn;
    @Inject
    RemoveClickColumn<ReservedIp> removeClickColumn;

    AddressColumn address;
    LastCheckColumn lastCheck;

    AbstractColumn hideComment;
    AbstractColumn hideLastCheck;

    private AbstractReservedIpTableActivity activity;

    private List<ClickColumn> columns = new ArrayList<>();
    private ClickColumnProvider<ReservedIp> columnProvider = new ClickColumnProvider<>();

    private static ReservedIpTableViewUiBinder ourUiBinder = GWT.create( ReservedIpTableViewUiBinder.class );
    interface ReservedIpTableViewUiBinder extends UiBinder< HTMLPanel, ReservedIpTableView> {}
}