package ru.protei.portal.ui.project.client.view.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SimpleHtmlSanitizer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.dict.En_PersonRoleType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dto.Project;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonProjectMemberView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.columns.*;
import ru.protei.portal.ui.common.client.lang.En_CustomerTypeLang;
import ru.protei.portal.ui.common.client.lang.ProjectStateLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.project.filter.ProjectFilterWidget;
import ru.protei.portal.ui.common.client.widget.project.filter.ProjectFilterWidgetModel;
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
    public void onInit(ProjectFilterWidgetModel projectFilterWidgetModel, EditClickColumn<Project> editClickColumn, RemoveClickColumn<Project> removeClickColumn) {
        this.filterWidget.onInit(projectFilterWidgetModel);
        initWidget( ourUiBinder.createAndBindUi( this ) );
        this.editClickColumn = editClickColumn;
        this.removeClickColumn = removeClickColumn;
        initTable();
    }

    @Override
    public void setActivity( AbstractProjectTableActivity activity ) {
        this.activity = activity;

        filterWidget.setOnFilterChangeCallback(activity::onFilterChanged);

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
    public void clearSelection() {
        columnProvider.removeSelection();
    }

    @Override
    public HasWidgets getPagerContainer() {
        return pagerContainer;
    }

    @Override
    public ProjectFilterWidget getFilterWidget() {
        return filterWidget;
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
                value -> {
                    String iconState = projectStateLang.getStateIcon(new CaseState(value.getStateName()));
                    String style = "'style='color: " + value.getStateColor();
                    String title = "'title='" + projectStateLang.getStateName(new CaseState(value.getStateName()));

                    return "<i class='" + iconState + " fa-2x" + title + style + "'></i>";
                });

        columns.add(statusColumn);

        DynamicColumn<Project> numberColumn = new DynamicColumn<>(lang.projectDirections(), "number",
                value -> {
                    StringBuilder content = new StringBuilder();
                    content.append("<div");
                    if (CASE_STATE_FINISHED.equals(value.getStateName()) || CASE_STATE_CANCELED.equals(value.getStateName())) {
                        content.append(" class='").append("line-through").append(" text-gray'");
                    }
                    content.append(">").append("<b>").append(value.getId()).append("</b>");
                    if (isNotEmpty(value.getProductDirectionEntityOptionList())) {
                        Label directions = new Label(joining(value.getProductDirectionEntityOptionList(), ", ", EntityOption::getDisplayText));
                        directions.setStyleName("directions-label");
                        content.append(directions.toString());
                    }
                    content.append("</div>");

                    return content.toString();
                });
        columns.add(numberColumn);

        DynamicColumn<Project> customerColumn = new DynamicColumn<>(lang.projectCustomer(), "customers",
                value -> {
                    StringBuilder content = new StringBuilder();
                    content.append("<div");
                    if (CASE_STATE_FINISHED.equals(value.getStateName()) || CASE_STATE_CANCELED.equals(value.getStateName())) {
                        content.append(" class='").append(" text-gray'");
                    }
                    content.append(">");
                    if ( value.getCustomer() != null && value.getCustomer().toEntityOption() != null) {
                        content.append("<b>").append(value.getCustomer().toEntityOption().getDisplayText()).append("</b>").append("<br/>");
                    }
                    if (value.getCustomerType() != null) {
                        content.append("<i>").append(customerTypeLang.getName(value.getCustomerType())).append("</i>").append("<br/>");
                    }
                    if ( value.getRegion() != null && value.getRegion().getDisplayText() != null) {
                        content.append(value.getRegion().getDisplayText());
                    }
                    content.append("</div>");

                    return content.toString();
                });
        columns.add(customerColumn);

        DynamicColumn<Project> infoColumn = new DynamicColumn<>(lang.projectInfo(), "info",
                value -> {
                    StringBuilder content = new StringBuilder();
                    content.append("<div");
                    if (CASE_STATE_FINISHED.equals(value.getStateName()) || CASE_STATE_CANCELED.equals(value.getStateName())) {
                        content.append(" class='").append(" text-gray'");
                    }
                    content.append(">");
                    content.append("<b>").append(SimpleHtmlSanitizer.sanitizeHtml(value.getName()).asString()).append("</b>");
                    content.append(value.getDescription() == null ? "" : "<br/><small>" + SimpleHtmlSanitizer.sanitizeHtml(value.getDescription()).asString() + "</small>");
                    content.append("</div>");

                    return content.toString();
                });
        columns.add(infoColumn);

        DynamicColumn<Project> managerColumn = new DynamicColumn<>(lang.projectTeam(), "managers",
                value -> {
                    if (isEmpty(value.getTeam())) return null;

                    Optional<PersonProjectMemberView> leader = value.getTeam().stream()
                            .filter(ppm -> En_PersonRoleType.HEAD_MANAGER.equals(ppm.getRole()))
                            .findFirst();

                    int teamSize = value.getTeam().size() - (leader.isPresent() ? 1 : 0);

                    StringBuilder content = new StringBuilder();
                    content.append("<div");
                    if (CASE_STATE_FINISHED.equals(value.getStateName()) || CASE_STATE_CANCELED.equals(value.getStateName())) {
                        content.append(" class='").append(" text-gray'");
                    }
                    content.append(">");
                    leader.ifPresent(lead -> content.append(lead.getName()));

                    if (teamSize > 0) {
                        leader.ifPresent(lead -> content.append(" + "));
                        content.append(teamSize).append(" ").append(lang.membersCount());
                    }
                    content.append("</div>");

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
    @Inject
    @UiField(provided = true)
    ProjectFilterWidget filterWidget;
    @UiField
    HTMLPanel pagerContainer;

    @Inject
    ProjectStateLang projectStateLang;
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

    private final String CASE_STATE_CANCELED = "canceled";
    private final String CASE_STATE_FINISHED= "finished";

    private static ProjectTableViewUiBinder ourUiBinder = GWT.create( ProjectTableViewUiBinder.class );
    interface ProjectTableViewUiBinder extends UiBinder< HTMLPanel, ProjectTableView> {}
}
