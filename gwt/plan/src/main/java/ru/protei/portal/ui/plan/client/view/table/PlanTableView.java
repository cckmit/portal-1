package ru.protei.portal.ui.plan.client.view.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Plan;
import ru.protei.portal.core.model.ent.ReservedIp;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.columns.RemoveClickColumn;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.plan.client.activity.table.AbstractPlanTableActivity;
import ru.protei.portal.ui.plan.client.activity.table.AbstractPlanTableView;

public class PlanTableView extends Composite implements AbstractPlanTableView {

    @Inject
    public void onInit(EditClickColumn<Plan> editClickColumn, RemoveClickColumn<Plan> removeClickColumn){
        initWidget(ourUiBinder.createAndBindUi(this));
        this.editClickColumn = editClickColumn;
        this.removeClickColumn = removeClickColumn;
        initTable();
    }


    @Override
    public void setActivity(AbstractPlanTableActivity activity) {
        this.activity = activity;

        editClickColumn.setEditHandler( activity );
        editClickColumn.setHandler( activity );
        editClickColumn.setColumnProvider( columnProvider );

        removeClickColumn.setRemoveHandler(activity);
        removeClickColumn.setHandler( activity );
        removeClickColumn.setColumnProvider(columnProvider);

        name.setHandler( activity );
        name.setColumnProvider( columnProvider );

        period.setHandler( activity );
        period.setColumnProvider( columnProvider );

        creator.setHandler( activity );
        creator.setColumnProvider( columnProvider );

        issueQuantity.setHandler( activity );
        issueQuantity.setColumnProvider( columnProvider );

        table.setLoadHandler( activity );
        table.setPagerListener( activity );
    }

    @Override
    public void clearSelection() {
        columnProvider.setSelectedValue(null);
    }

    @Override
    public void updateRow(Plan plan) {
        table.updateRow(plan);
    }

  /*  @Override
    public void setAnimation ( TableAnimation animation ) {
        animation.setContainers( tableContainer, previewContainer, filterContainer );
        animation.setStyles("col-md-12", "col-md-9", "col-md-3", "col-md-6", "col-md-6");
    }
*/
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
    public HTMLPanel getFilterContainer() { return filterContainer; }

    @Override
    public HTMLPanel getPreviewContainer() { return previewContainer; }

    @Override
    public HasWidgets getPagerContainer() { return pagerContainer; }

    @Override
    public void setAnimation(TableAnimation animation) {
        animation.setContainers( tableContainer, previewContainer, filterContainer );
    }

    @Override
    public void clearRecords() {
        table.clearCache();
        table.clearRows();
    }

    private void initTable () {
        //добавить проверку создателя
        editClickColumn.setEnabledPredicate(v -> policyService.hasPrivilegeFor(En_Privilege.PLAN_EDIT) );
        removeClickColumn.setEnabledPredicate(v -> policyService.hasPrivilegeFor(En_Privilege.PLAN_REMOVE) );

        name = new ClickColumn<Plan>() {
            @Override
            protected void fillColumnHeader(Element columnHeader) {
                columnHeader.addClassName( "plan-name" );
                columnHeader.setInnerText(lang.planNameColumn());
            }

            @Override
            public void fillColumnValue(Element cell, Plan value) {
                cell.addClassName( "plan-name" );
                cell.setInnerText(value.getName());
            }
        };

        period = new ClickColumn<Plan>() {
            @Override
            protected void fillColumnHeader(Element columnHeader) {
                columnHeader.addClassName( "plan-range" );
                columnHeader.setInnerText(lang.planPeriodColumn());
            }

            @Override
            public void fillColumnValue(Element cell, Plan value) {
                cell.addClassName( "plan-range" );
                cell.setInnerText(DateFormatter.formatDateOnly(value.getStartDate()) + " - " + DateFormatter.formatDateOnly(value.getFinishDate()));
            }
        };

        creator = new ClickColumn<Plan>() {
            @Override
            protected void fillColumnHeader(Element columnHeader) {
                columnHeader.addClassName( "plan-creator" );
                columnHeader.setInnerText(lang.planCreatorColumn());
            }

            @Override
            public void fillColumnValue(Element cell, Plan value) {
                cell.addClassName( "plan-creator" );
                cell.setInnerText(value.getCreatorShortName());
            }
        };

        issueQuantity = new ClickColumn<Plan>() {
            @Override
            protected void fillColumnHeader(Element columnHeader) {
                columnHeader.addClassName( "plan-issue-quantity" );
                columnHeader.setInnerText(lang.planIssueQuantityColumn());
            }

            @Override
            public void fillColumnValue(Element cell, Plan value) {
                cell.addClassName( "plan-issue-quantity" );
                cell.setInnerText(String.valueOf(value.getIssuesCount()));
            }
        };

        table.addColumn(name.header, name.values);
        table.addColumn(period.header, period.values);
        table.addColumn(creator.header, creator.values);
        table.addColumn(issueQuantity.header, issueQuantity.values);
        table.addColumn(editClickColumn.header, editClickColumn.values);
        table.addColumn(removeClickColumn.header, removeClickColumn.values);
    }

    @UiField
    InfiniteTableWidget<Plan> table;
    @UiField
    HTMLPanel tableContainer;
    @UiField
    HTMLPanel previewContainer;
    @UiField
    HTMLPanel filterContainer;
    @UiField
    HTMLPanel pagerContainer;

    @Inject
    Lang lang;
    @Inject
    PolicyService policyService;

    ClickColumnProvider<Plan> columnProvider = new ClickColumnProvider<>();

    ClickColumn<Plan> name;
    ClickColumn<Plan> period;
    ClickColumn<Plan> creator;
    ClickColumn<Plan> issueQuantity;

    EditClickColumn<Plan> editClickColumn;
    RemoveClickColumn<Plan> removeClickColumn;

    AbstractPlanTableActivity activity;

    private static PlanTableViewUiBinder ourUiBinder = GWT.create(PlanTableViewUiBinder.class);
    interface PlanTableViewUiBinder extends UiBinder<HTMLPanel, PlanTableView> {}
}
