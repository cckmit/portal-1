package ru.protei.portal.ui.project.client.view.table;

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
import ru.brainworm.factory.widget.table.client.TableWidget;
import ru.protei.portal.core.model.dict.En_DevUnitPersonRoleType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.struct.ProjectInfo;
import ru.protei.portal.core.model.view.PersonProjectMemberView;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.columns.*;
import ru.protei.portal.ui.common.client.lang.En_CustomerTypeLang;
import ru.protei.portal.ui.common.client.lang.En_RegionStateLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.project.client.activity.table.AbstractProjectTableActivity;
import ru.protei.portal.ui.project.client.activity.table.AbstractProjectTableView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * Представление таблицы проектов
 */
public class ProjectTableView extends Composite implements AbstractProjectTableView {

    @Inject
    public void onInit(EditClickColumn< ProjectInfo > editClickColumn, RemoveClickColumn<ProjectInfo> removeClickColumn) {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        this.editClickColumn = editClickColumn;
        this.removeClickColumn = removeClickColumn;
        initTable();
    }

    @Override
    public void setActivity( AbstractProjectTableActivity activity ) {
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
    public void addRow( ProjectInfo row ) {
        table.addRow( row );
    }

    @Override
    public void addSeparator( String text ) {
        Element elem = DOM.createDiv();
        SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
        safeHtmlBuilder.appendHtmlConstant( "<b>" ).appendEscapedLines( text ).appendHtmlConstant( "</b>" );
        elem.setInnerSafeHtml( safeHtmlBuilder.toSafeHtml() );
        table.addCustomRow( elem, "region", null );
    }

    @Override
    public void updateRow( ProjectInfo project ) {
        table.updateRow( project );
    }

    @Override
    public void selectRow(ProjectInfo project) {
        columnProvider.setSelectedValue (project);
    }

    private void initTable () {
        editClickColumn.setPrivilege( En_Privilege.PROJECT_EDIT );
        columns.add(editClickColumn);

        removeClickColumn.setPrivilege( En_Privilege.PROJECT_REMOVE );
        columns.add(removeClickColumn);

        DynamicColumn<ProjectInfo> statusColumn = new DynamicColumn<>(null, "status",
                value -> "<i class='"+ regionStateLang.getStateIcon( value.getState() )+" fa-2x"+"'></i>");
        columns.add(statusColumn);

        DynamicColumn<ProjectInfo> numberColumn = new DynamicColumn<>(lang.projectDirection(), "number",
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
        columns.add(numberColumn);

        DynamicColumn<ProjectInfo> infoColumn = new DynamicColumn<>(lang.projectInfo(), "info",
                value -> "<b>" + value.getName() + "</b>" + (value.getDescription() == null ? "" : "<br/><small>" + value.getDescription() + "</small>"));
        columns.add(infoColumn);

        DynamicColumn<ProjectInfo> managerColumn = new DynamicColumn<>(lang.projectTeam(), "managers",
                value -> {
                    if (value.getTeam() == null) return null;

                    Optional<PersonProjectMemberView> leader = value.getTeam().stream()
                            .filter(ppm -> En_DevUnitPersonRoleType.HEAD_MANAGER.equals(ppm.getRole()))
                            .findFirst();

                    int teamSize = value.getTeam().size() - (leader.isPresent() ? 1 : 0);

                    StringBuilder content = new StringBuilder();
                    leader.ifPresent(lead -> content.append(lead.getDisplayShortName()));

                    if (teamSize > 0) {
                        leader.ifPresent(lead -> content.append(" + "));
                        content.append(teamSize).append(" ").append(lang.membersCount());
                    }

                    return content.toString();
                });
        columns.add(managerColumn);

        table.addColumn( statusColumn.header, statusColumn.values );
        table.addColumn( numberColumn.header, numberColumn.values );
        table.addColumn( infoColumn.header, infoColumn.values );
        table.addColumn( managerColumn.header, managerColumn.values );
        table.addColumn( editClickColumn.header, editClickColumn.values );
        table.addColumn( removeClickColumn.header, removeClickColumn.values );
    }

    @UiField
    Lang lang;
    @UiField
    TableWidget<ProjectInfo> table;
    @UiField
    HTMLPanel tableContainer;
    @UiField
    HTMLPanel previewContainer;
    @UiField
    HTMLPanel filterContainer;

    @Inject
    En_RegionStateLang regionStateLang;
    @Inject
    En_CustomerTypeLang customerTypeLang;

    @Inject
    EditClickColumn< ProjectInfo > editClickColumn;
    @Inject
    RemoveClickColumn<ProjectInfo> removeClickColumn;

    private AbstractProjectTableActivity activity;

    private List<ClickColumn> columns = new ArrayList<>();
    private ClickColumnProvider<ProjectInfo> columnProvider = new ClickColumnProvider<>();

    private static ProjectTableViewUiBinder ourUiBinder = GWT.create( ProjectTableViewUiBinder.class );
    interface ProjectTableViewUiBinder extends UiBinder< HTMLPanel, ProjectTableView> {}
}