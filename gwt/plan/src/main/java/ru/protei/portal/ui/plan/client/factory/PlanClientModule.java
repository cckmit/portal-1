package ru.protei.portal.ui.plan.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.plan.client.activity.edit.AbstractPlanEditPopupView;
import ru.protei.portal.ui.plan.client.activity.edit.AbstractPlanEditView;
import ru.protei.portal.ui.plan.client.activity.edit.PlanEditActivity;
import ru.protei.portal.ui.plan.client.activity.edit.PlanEditPopupActivity;
import ru.protei.portal.ui.plan.client.activity.edit.tables.AbstractPlannedIssuesTableView;
import ru.protei.portal.ui.plan.client.activity.edit.tables.AbstractUnplannedIssuesTableView;
import ru.protei.portal.ui.plan.client.activity.edit.tables.PlannedIssuesTableActivity;
import ru.protei.portal.ui.plan.client.activity.edit.tables.UnplannedIssuesTableActivity;
import ru.protei.portal.ui.plan.client.activity.filter.AbstractPlanFilterView;
import ru.protei.portal.ui.plan.client.activity.preview.AbstractPlanPreviewView;
import ru.protei.portal.ui.plan.client.activity.preview.PlanPreviewActivity;
import ru.protei.portal.ui.plan.client.activity.table.AbstractPlanTableView;
import ru.protei.portal.ui.plan.client.activity.table.PlanTableActivity;
import ru.protei.portal.ui.plan.client.page.PlanPage;
import ru.protei.portal.ui.plan.client.view.edit.PlanEditPopupView;
import ru.protei.portal.ui.plan.client.view.edit.PlanEditView;
import ru.protei.portal.ui.plan.client.view.edit.tables.PlannedIssuesTableView;
import ru.protei.portal.ui.plan.client.view.edit.tables.UnplannedIssuesTableView;
import ru.protei.portal.ui.plan.client.view.filter.PlanFilterView;
import ru.protei.portal.ui.plan.client.view.preview.PlanPreviewView;
import ru.protei.portal.ui.plan.client.view.table.PlanTableView;

public class PlanClientModule extends AbstractGinModule {

    @Override
    protected void configure() {
        bind( PlanPage.class ).asEagerSingleton();

        bind( PlanTableActivity.class ).asEagerSingleton ();
        bind( AbstractPlanTableView.class ).to(PlanTableView.class).in(Singleton.class);
        bind( AbstractPlanFilterView.class ).to(PlanFilterView.class).in(Singleton.class);

        bind( PlanPreviewActivity.class ).asEagerSingleton();
        bind( AbstractPlanPreviewView.class ).to( PlanPreviewView.class ).in(Singleton.class);

        bind( PlanEditActivity.class ).asEagerSingleton();
        bind( AbstractPlanEditView.class ).to( PlanEditView.class ).in(Singleton.class);

        bind(PlannedIssuesTableActivity.class).asEagerSingleton();
        bind(AbstractPlannedIssuesTableView.class).to(PlannedIssuesTableView.class).in(Singleton.class);

        bind(UnplannedIssuesTableActivity.class).asEagerSingleton();
        bind(AbstractUnplannedIssuesTableView.class).to(UnplannedIssuesTableView.class).in(Singleton.class);

        bind(PlanEditPopupActivity.class).asEagerSingleton();
        bind(AbstractPlanEditPopupView.class).to(PlanEditPopupView.class).in(Singleton.class);
    }
}
