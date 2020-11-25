package ru.protei.portal.ui.project.client.view.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SimpleHtmlSanitizer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.dict.En_DevUnitPersonRoleType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dto.Project;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonProjectMemberView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
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

import static ru.protei.portal.core.model.helper.CollectionUtils.*;


/**
 * Представление таблицы проектов
 */
public class ProjectTableView extends Composite implements AbstractProjectTableView {

    @Inject
    public void onInit(EditClickColumn<Project> editClickColumn, RemoveClickColumn<Project> removeClickColumn) {
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

        table.setLoadHandler( activity );
        table.setPagerListener( activity );
    }
    
    @Override
    public void setAnimation ( TableAnimation animation ) {
        animation.setContainers( tableContainer, previewContainer, filterContainer );
        columnProvider.setChangeSelectionIfSelectedPredicate(project -> animation.isPreviewShow());
    }

    @Override
    public HasWidgets getPreviewContainer () { return previewContainer; }

    @Override
    public HasWidgets getFilterContainer () { return filterContainer; }

    @Override
    public void clearSelection() {
        columnProvider.removeSelection();
    }

    @Override
    public HasWidgets getPagerContainer() {
        return pagerContainer;
    }

    @Override
    public void clearRecords() {
        table.clearCache();
        table.clearRows();
    }

    @Override
    public void updateRow( Project project ) {
        if(project != null) {
            table.updateRow(project);
        }
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
    public void scrollTo( int page ) {
        table.scrollToPage( page );
    }

    private void initTable () {
        editClickColumn.setEnabledPredicate(v -> policyService.hasPrivilegeFor(En_Privilege.PROJECT_EDIT) );
        columns.add(editClickColumn);

        removeClickColumn.setEnabledPredicate(v -> policyService.hasPrivilegeFor(En_Privilege.PROJECT_REMOVE) && !v.isDeleted() );
        columns.add(removeClickColumn);

        DynamicColumn<Project> statusColumn = new DynamicColumn<>(null, "status",
                value -> "<i class='"+ regionStateLang.getStateIcon( value.getState() )+" fa-2x"+"'></i>");
        columns.add(statusColumn);

        DynamicColumn<Project> numberColumn = new DynamicColumn<>(lang.projectDirection(), "number",
                value -> {
                    StringBuilder content = new StringBuilder();
                    content.append("<b>").append(value.getId()).append("</b>").append("<br/>");

                    if (isNotEmpty(value.getProductDirectionEntityOptionList())) {
                        content.append(joining(value.getProductDirectionEntityOptionList(), ", ", EntityOption::getDisplayText));
                    }
                    return content.toString();
                });
        columns.add(numberColumn);

        DynamicColumn<Project> customerColumn = new DynamicColumn<>(lang.projectCustomer(), "customers",
                value -> {
                    StringBuilder content = new StringBuilder();
                    if ( value.getCustomer() != null && value.getCustomer().toEntityOption() != null) {
                        content.append("<b>").append(value.getCustomer().toEntityOption().getDisplayText()).append("</b>").append("<br/>");
                    }
                    if (value.getCustomerType() != null) {
                        content.append("<i>").append(customerTypeLang.getName(value.getCustomerType())).append("</i>").append("<br/>");
                    }
                    if ( value.getRegion() != null && value.getRegion().getDisplayText() != null) {
                        content.append(value.getRegion().getDisplayText());
                    }
                    return content.toString();
                });
        columns.add(customerColumn);

        DynamicColumn<Project> infoColumn = new DynamicColumn<>(lang.projectInfo(), "info",
                value -> "<b>" + SimpleHtmlSanitizer.sanitizeHtml(value.getName()).asString() + "</b>" +
                                    (value.getDescription() == null ? "" : "<br/><small>" + SimpleHtmlSanitizer.sanitizeHtml(value.getDescription()).asString() + "</small>"));
        columns.add(infoColumn);

        DynamicColumn<Project> managerColumn = new DynamicColumn<>(lang.projectTeam(), "managers",
                value -> {
                    if (isEmpty(value.getTeam())) return null;

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
        columns.add(managerColumn);

        table.addColumn( statusColumn.header, statusColumn.values );
        table.addColumn( numberColumn.header, numberColumn.values );
        table.addColumn( customerColumn.header, customerColumn.values );
        table.addColumn( infoColumn.header, infoColumn.values );
        table.addColumn( managerColumn.header, managerColumn.values );
        table.addColumn( editClickColumn.header, editClickColumn.values );
        table.addColumn( removeClickColumn.header, removeClickColumn.values );
    }

    @UiField
    Lang lang;
    @UiField
    InfiniteTableWidget<Project> table;
    @UiField
    HTMLPanel tableContainer;
    @UiField
    HTMLPanel previewContainer;
    @UiField
    HTMLPanel filterContainer;
    @UiField
    HTMLPanel pagerContainer;

    @Inject
    En_RegionStateLang regionStateLang;
    @Inject
    En_CustomerTypeLang customerTypeLang;
    @Inject
    PolicyService policyService;

    @Inject
    EditClickColumn<Project> editClickColumn;
    @Inject
    RemoveClickColumn<Project> removeClickColumn;

    private AbstractProjectTableActivity activity;

    private List<ClickColumn> columns = new ArrayList<>();
    private ClickColumnProvider<Project> columnProvider = new ClickColumnProvider<>();

    private static ProjectTableViewUiBinder ourUiBinder = GWT.create( ProjectTableViewUiBinder.class );
    interface ProjectTableViewUiBinder extends UiBinder< HTMLPanel, ProjectTableView> {}
}
