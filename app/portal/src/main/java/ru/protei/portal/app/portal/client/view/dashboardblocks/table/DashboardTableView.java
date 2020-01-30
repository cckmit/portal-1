package ru.protei.portal.app.portal.client.view.dashboardblocks.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.TableWidget;
import ru.protei.portal.app.portal.client.activity.dashboardblocks.table.AbstractDashboardTableActivity;
import ru.protei.portal.app.portal.client.activity.dashboardblocks.table.AbstractDashboardTableView;
import ru.protei.portal.app.portal.client.view.dashboardblocks.table.columns.ContactColumn;
import ru.protei.portal.app.portal.client.view.dashboardblocks.table.columns.ManagerColumn;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.lang.En_CaseStateLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.issue.client.view.table.columns.InfoColumn;
import ru.protei.portal.ui.issue.client.view.table.columns.NumberColumn;

import java.util.List;

public class DashboardTableView extends Composite implements AbstractDashboardTableView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractDashboardTableActivity activity) {
        this.activity = activity;
        initTable();
    }

    @Override
    public void clearRecords() {
        table.clearRows();
        count.setInnerText("");
    }

    @Override
    public void putRecords(List<CaseShortView> list) {
        list.forEach(table::addRow);
    }

    @Override
    public void setName(String name) {
        this.name.setInnerText(name);
    }

    @Override
    public void setTotalRecords(int totalRecords) {
        count.setInnerText("(" + totalRecords + ")");
    }

    @Override
    public void showLoader(boolean isShow) {
        loading.removeClassName("show");
        if (isShow) {
            loading.addClassName("show");
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

    @UiHandler("open")
    public void onOpenClicked(ClickEvent event) {
        event.preventDefault();
        if (activity != null) {
            activity.onOpenClicked();
        }
    }

    @UiHandler("reload")
    public void onReloadClicked(ClickEvent event) {
        event.preventDefault();
        if (activity != null) {
            activity.onReloadClicked();
        }
    }

    private void initTable() {

        ClickColumnProvider<CaseShortView> columnProvider = new ClickColumnProvider<>();

        NumberColumn number = new NumberColumn(lang, caseStateLang);
        table.addColumn(number.header, number.values);
        number.setHandler(activity);
        number.setColumnProvider(columnProvider);

        InfoColumn info = new InfoColumn(lang);
        table.addColumn(info.header, info.values);
        info.setHandler(activity);
        info.setColumnProvider(columnProvider);

        ContactColumn contact = new ContactColumn(lang);
        table.addColumn(contact.header, contact.values);
        contact.setHandler(activity);
        contact.setColumnProvider(columnProvider);

        ManagerColumn manager = new ManagerColumn(lang);
        table.addColumn(manager.header, manager.values);
        manager.setHandler(activity);
        manager.setColumnProvider(columnProvider);
    }

    @Inject
    En_CaseStateLang caseStateLang;
    @Inject
    @UiField
    Lang lang;

    @UiField
    SpanElement name;
    @UiField
    SpanElement count;
    @UiField
    Anchor open;
    @UiField
    Anchor reload;
    @UiField
    DivElement loading;
    @UiField
    TableWidget<CaseShortView> table;
    @UiField
    HTMLPanel tableOverflow;
    @UiField
    SpanElement tableOverflowText;

    private AbstractDashboardTableActivity activity;

    interface CaseTableViewUiBinder extends UiBinder<HTMLPanel, DashboardTableView> {}
    private static CaseTableViewUiBinder ourUiBinder = GWT.create(CaseTableViewUiBinder.class);
}