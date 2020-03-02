package ru.protei.portal.ui.ipreservation.client.view.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.Column;
import ru.brainworm.factory.widget.table.client.TableWidget;
import ru.protei.portal.core.model.dict.En_DevUnitPersonRoleType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.ReservedIp;
import ru.protei.portal.core.model.struct.Project;
import ru.protei.portal.core.model.view.PersonProjectMemberView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.columns.*;
import ru.protei.portal.ui.common.client.lang.En_CustomerTypeLang;
import ru.protei.portal.ui.common.client.lang.En_RegionStateLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.ipreservation.client.activity.table.AbstractReservedIpTableActivity;
import ru.protei.portal.ui.ipreservation.client.activity.table.AbstractReservedIpTableView;
import ru.protei.portal.ui.project.client.activity.table.AbstractProjectTableActivity;
import ru.protei.portal.ui.project.client.activity.table.AbstractProjectTableView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * Представление таблицы зарезервированных IP
 */
public class ReservedIpTableView extends Composite implements AbstractReservedIpTableView {

    @Inject
    public void onInit(EditClickColumn<ReservedIp> editClickColumn, RemoveClickColumn<ReservedIp> removeClickColumn) {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        this.editClickColumn = editClickColumn;
        this.removeClickColumn = removeClickColumn;
        initTable();
    }

    @Override
    public void setActivity( AbstractReservedIpTableActivity activity ) {
        this.activity = activity;

        editClickColumn.setEditHandler( activity );
        removeClickColumn.setRemoveHandler( activity );

        columns.forEach(clickColumn -> {
            clickColumn.setHandler( activity );
            clickColumn.setColumnProvider( columnProvider );
        });
    }
    
    @Override
    public void setAnimation ( TableAnimation animation ) {
        animation.setContainers( tableContainer, previewContainer, filterContainer );
    }

    @Override
    public HasWidgets getPreviewContainer () { return previewContainer; }

    @Override
    public HasWidgets getFilterContainer () { return filterContainer; }

    @Override
    public void clearSelection() {
        columnProvider.setSelectedValue(null);
    }

    @Override
    public void clearRecords() {
        table.clearRows();
    }

    @Override
    public void addSeparator( String text ) {
        Element elem = DOM.createDiv();
        SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
        safeHtmlBuilder.appendHtmlConstant( "<b>" ).appendEscapedLines( text ).appendHtmlConstant( "</b>" );
        elem.setInnerSafeHtml( safeHtmlBuilder.toSafeHtml() );
        table.addCustomRow( elem, "subnet", null );
    }

    private void initTable () {
        editClickColumn.setEnabledPredicate(v -> policyService.hasPrivilegeFor(En_Privilege.RESERVED_IP_EDIT) );
        columns.add(editClickColumn);

        removeClickColumn.setEnabledPredicate(v -> policyService.hasPrivilegeFor(En_Privilege.PROJECT_REMOVE) && !v.isDeleted() );
        columns.add(removeClickColumn);

/*        DynamicColumn<Project> numberColumn = new DynamicColumn<>(lang.projectDirection(), "number",
                value -> {
                    StringBuilder content = new StringBuilder();
                    content.append("<b>").append(value.getId()).append("</b>");

                    if (value.getProductDirection() != null) {
                        content.append("<br/>").append(value.getProductDirection().getDisplayText());
                    }
                    if (value.getCustomerType() != null) {
                        content.append("<br/><i>").append(customerTypeLang.getName(value.getCustomerType())).append("</i>");
                    }
                    return content.toString();
                });
        columns.add(numberColumn);*/

        Column<ReservedIp> address = new Column<>(lang.address(), "address",
                value -> "<b>" + value.getIp() + "</b>" + (value.getDescription() == null ? "" : "<br/><small>" + value.getDescription() + "</small>"));
        columns.add(address);

        Column<ReservedIp> ownerColumn = new Column<>(lang.owner(), "owner",
                value -> {
                    if (value.getTeam() == null) return null;

                    Optional<PersonProjectMemberView> leader = value.getTeam().stream()
                            .filter(ppm -> En_DevUnitPersonRoleType.HEAD_MANAGER.equals(ppm.getRole()))
                            .findFirst();

                    int teamSize = value.getTeam().size() - (leader.isPresent() ? 1 : 0);

                    StringBuilder content = new StringBuilder();
                    leader.ifPresent(lead -> content.append(lead.getName()));

                    if (teamSize > 0) {
                        leader.ifPresent(lead -> content.append(" + "));
                        content.append(teamSize).append(" ").append(lang.membersCount());
                    }

                    return content.toString();
                });
        columns.add(ownerColumn);

        table.addColumn( statusColumn.header, statusColumn.values );
        table.addColumn( addressColumn.header, addressColumn.values );
        table.addColumn( commentColumn.header, commentColumn.values );
        table.addColumn( ownerColumn.header, ownerColumn.values );
        table.addColumn( editClickColumn.header, editClickColumn.values );
        table.addColumn( removeClickColumn.header, removeClickColumn.values );
    }

    @UiField
    Lang lang;
    @UiField
    TableWidget<ReservedIp> table;
    @UiField
    HTMLPanel tableContainer;
    @UiField
    HTMLPanel previewContainer;
    @UiField
    HTMLPanel filterContainer;

    @Inject
    PolicyService policyService;

    @Inject
    EditClickColumn<ReservedIp> editClickColumn;
    @Inject
    RemoveClickColumn<ReservedIp> removeClickColumn;

    private AbstractReservedIpTableActivity activity;

    private List<ClickColumn> columns = new ArrayList<>();
    private ClickColumnProvider<ReservedIp> columnProvider = new ClickColumnProvider<>();

    private static ReservedIpTableViewUiBinder ourUiBinder = GWT.create( ReservedIpTableViewUiBinder.class );
    interface ReservedIpTableViewUiBinder extends UiBinder< HTMLPanel, ReservedIpTableView> {}
}