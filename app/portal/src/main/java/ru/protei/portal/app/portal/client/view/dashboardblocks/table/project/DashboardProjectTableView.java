package ru.protei.portal.app.portal.client.view.dashboardblocks.table.project;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SimpleHtmlSanitizer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.TableWidget;
import ru.protei.portal.app.portal.client.activity.dashboardblocks.table.AbstractDashboardProjectTableActivity;
import ru.protei.portal.app.portal.client.activity.dashboardblocks.table.AbstractDashboardProjectTableView;
import ru.protei.portal.core.model.dict.En_PersonRoleType;
import ru.protei.portal.core.model.dto.Project;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonProjectMemberView;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.columns.DynamicColumn;
import ru.protei.portal.ui.common.client.lang.En_CustomerTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.lang.ProjectStateLang;
import ru.protei.portal.ui.common.client.widget.loading.IndeterminateCircleLoading;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static ru.protei.portal.core.model.helper.CollectionUtils.*;

public class DashboardProjectTableView extends Composite implements AbstractDashboardProjectTableView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        headerContainer.setDraggable(Element.DRAGGABLE_TRUE);
        initTable();
    }

    @Override
    public void setActivity(AbstractDashboardProjectTableActivity activity) {
        this.activity = activity;
        columns.forEach(clickColumn -> {
            clickColumn.setHandler( activity );
            clickColumn.setColumnProvider( columnProvider );
        });
    }

    @Override
    public void clearRecords() {
        table.clearRows();
        count.setInnerText("");
    }

    @Override
    public void putRecords(List<Project> list) {
        list.forEach(table::addRow);
    }

    @Override
    public void setName(String name) {
        this.name.setInnerText(name);
    }

    @Override
    public void setCollapsed(boolean isCollapsed) {
        if (isCollapsed){
            tableContainer.addClassName("table-container-collapsed");
            collapseIcon.replaceClassName("fa-caret-down", "fa-caret-right");
            collapse.setTitle(lang.dashboardActionExpand());
        } else {
            tableContainer.removeClassName("table-container-collapsed");
            collapseIcon.replaceClassName("fa-caret-right", "fa-caret-down");
            collapse.setTitle(lang.dashboardActionCollapse());
        }
    }

    @Override
    public void setTotalRecords(int totalRecords) {
        count.setInnerText("(" + totalRecords + ")");
    }

    @Override
    public void showLoader(boolean isShow) {
        loading.removeStyleName("d-block");
        if (isShow) {
            loading.addStyleName("d-block");
        }
    }

    @Override
    public void showTableOverflow(int showedRecords) {
        tableOverflow.setVisible(true);
        tableOverflowText.setInnerText(lang.dashboardTableOverflow(showedRecords));
    }

    @Override
    public void hideTableOverflow() {
        tableOverflow.setVisible(false);
    }

    @Override
    public void setEnsureDebugId(String debugId) {
        table.setEnsureDebugId(debugId);
    }

    @Override
    public void setChangeSelectionIfSelectedPredicate(Predicate<Project> changeSelectionIfSelectedPredicate) {
        columnProvider.setChangeSelectionIfSelectedPredicate(changeSelectionIfSelectedPredicate);
    }

    @Override
    public HandlerRegistration addDragStartHandler(DragStartHandler handler) {
        return addDomHandler(handler, DragStartEvent.getType());
    }

    @Override
    public HandlerRegistration addDragOverHandler(DragOverHandler handler) {
        return addDomHandler(handler, DragOverEvent.getType());
    }

    @Override
    public HandlerRegistration addDropHandler(DropHandler handler) {
        return addDomHandler(handler, DropEvent.getType());
    }

    @Override
    public HandlerRegistration addDragEndHandler(DragEndHandler handler) {
        return addDomHandler(handler, DragEndEvent.getType());
    }

    @UiHandler("open")
    public void onOpenClicked(ClickEvent event) {
        event.preventDefault();
        if (activity != null) {
            activity.onOpenClicked();
        }
    }

    @UiHandler("edit")
    public void onEditClicked(ClickEvent event) {
        event.preventDefault();
        if (activity != null) {
            activity.onEditClicked();
        }
    }

    @UiHandler("remove")
    public void onRemoveClicked(ClickEvent event) {
        event.preventDefault();
        if (activity != null) {
            activity.onRemoveClicked();
        }
    }

    @UiHandler("reload")
    public void onReloadClicked(ClickEvent event) {
        event.preventDefault();
        if (activity != null) {
            activity.onReloadClicked();
        }
    }

    @UiHandler("collapse")
    public void onCollapseClicked(ClickEvent event) {
        boolean isCollapsed = tableContainer.getClassName().contains("table-container-collapsed");

        activity.onCollapseClicked(!isCollapsed);
        setCollapsed(!isCollapsed);
    }

    private void initTable() {
        DynamicColumn<Project> statusColumn = new DynamicColumn<>(null, "project-status",
                value -> {
                    String iconState = projectStateLang.getStateIcon(new CaseState(value.getStateName()));
                    String style = "'style='color: " + value.getStateColor();
                    String title = "'title='" + projectStateLang.getStateName(new CaseState(value.getStateName()));

                    return "<i class='" + iconState + " fa-2x d-flex justify-content-center align-self-center" + title + style + "'></i>";
                });
        columns.add(statusColumn);

        DynamicColumn<Project> numberColumn = new DynamicColumn<>(lang.projectDirections(), "project-number",
                value -> {
                    StringBuilder content = new StringBuilder();
                    content.append("<div");
                    if (CASE_STATE_FINISHED.equals(value.getStateName()) || CASE_STATE_CANCELED.equals(value.getStateName())) {
                        content.append(" class='").append("line-through").append(" text-gray'");
                    }
                    content.append(">").append("<p>").append(value.getId()).append("</p>");
                    if (isNotEmpty(value.getProductDirectionEntityOptionList())) {
                        Label directions = new Label(joining(value.getProductDirectionEntityOptionList(), ", ", EntityOption::getDisplayText));
                        directions.setStyleName("directions-label");
                        content.append(directions.toString());
                    }
                    content.append("</div>");

                    return content.toString();
                });
        columns.add(numberColumn);

        DynamicColumn<Project> customerColumn = new DynamicColumn<>(lang.projectCustomer(), "project-customers",
                value -> {
                    StringBuilder content = new StringBuilder();
                    content.append("<div class='font-size-12");
                    if (CASE_STATE_FINISHED.equals(value.getStateName()) || CASE_STATE_CANCELED.equals(value.getStateName())) {
                        content.append(" text-gray");
                    }
                    content.append("'>");
                    if ( value.getCustomer() != null && value.getCustomer().toEntityOption() != null) {
                        content.append("<b>").append(value.getCustomer().toEntityOption().getDisplayText()).append("</b>").append("<br/>");
                    }
                    if (value.getCustomerType() != null) {
                        content.append("<i>").append(customerTypeLang.getName(value.getCustomerType())).append("</i>").append("<br/>");
                    }
                    content.append("</div>");

                    return content.toString();
                });
        columns.add(customerColumn);

        DynamicColumn<Project> infoColumn = new DynamicColumn<>(lang.projectInfo(), "project-info",
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

        DynamicColumn<Project> managerColumn = new DynamicColumn<>(lang.projectTeam(), "project-managers",
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
    }

    @Inject
    @UiField
    Lang lang;
    @UiField
    SpanElement name;
    @UiField
    SpanElement count;
    @UiField
    Button open;
    @UiField
    Button edit;
    @UiField
    Button remove;
    @UiField
    Button collapse;
    @UiField
    Button reload;
    @UiField
    IndeterminateCircleLoading loading;
    @UiField
    TableWidget<Project> table;
    @UiField
    DivElement tableContainer;
    @UiField
    HTMLPanel tableOverflow;
    @UiField
    SpanElement tableOverflowText;
    @UiField
    Element collapseIcon;
    @Inject
    ProjectStateLang projectStateLang;
    @Inject
    En_CustomerTypeLang customerTypeLang;

    @UiField
    Element headerContainer;

    private final String CASE_STATE_CANCELED = "canceled";
    private final String CASE_STATE_FINISHED = "finished";

    private AbstractDashboardProjectTableActivity activity;
    private ClickColumnProvider<Project> columnProvider = new ClickColumnProvider<>();

    private List<ClickColumn> columns = new ArrayList<>();

    interface ProjectTableViewUiBinder extends UiBinder<HTMLPanel, DashboardProjectTableView> {}
    private static ProjectTableViewUiBinder ourUiBinder = GWT.create(ProjectTableViewUiBinder.class);
}
