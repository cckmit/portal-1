package ru.protei.portal.ui.delivery.client.view.cardbatch.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.ent.CardBatch;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.lang.CardStateLang;
import ru.protei.portal.ui.common.client.lang.En_PersonRoleTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.delivery.client.activity.cardbatch.table.AbstractCardBatchTableActivity;
import ru.protei.portal.ui.delivery.client.activity.cardbatch.table.AbstractCardBatchTableView;
import ru.protei.portal.ui.delivery.client.view.cardbatch.table.column.*;

import java.util.ArrayList;
import java.util.List;


public class CardBatchTableView extends Composite implements AbstractCardBatchTableView {

    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
        initTable();
    }

    @Override
    public void setActivity(AbstractCardBatchTableActivity activity) {
        this.activity = activity;
        table.setPagerListener(activity);
        table.setLoadHandler(activity);
        columns.forEach(clickColumn -> {
            clickColumn.setHandler(activity);
            clickColumn.setColumnProvider(columnProvider);
        });
    }

    @Override
    public void setAnimation(TableAnimation animation) {
        animation.setContainers(tableContainer, previewContainer, filterContainer);
        animation.setStyles("col-md-12", "col-md-9", "col-md-3", "col-md-3", "col-md-9");
    }

    @Override
    public void clearRecords() {
        table.clearCache();
        table.clearRows();
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
    public void scrollTo(int page) {
        table.scrollToPage(page);
    }

    @Override
    public HasWidgets getFilterContainer() {
        return filterContainer;
    }

    @Override
    public HasWidgets getPagerContainer() {
        return pagerContainer;
    }

    @Override
    public void clearSelection() {
        columnProvider.removeSelection();
    }

    @Override
    public void updateRow(CardBatch item) {
        if(item != null)
            table.updateRow(item);
    }

    private void initTable() {
        columns.add(new TypeColumn(lang, cardStateLang));
        columns.add(new ArticleColumn(lang));
        columns.add(new NumberColumn(lang, cardStateLang));
        columns.add(new AmountColumn(lang));
        columns.add(new DeadlineColumn(lang));
        columns.add(new ContractorsColumn(lang, personRoleTypeLang));
        columns.forEach(clickColumn -> {
            table.addColumn(clickColumn.header, clickColumn.values);
        });
    }

    @UiField
    Lang lang;
    @UiField
    InfiniteTableWidget<CardBatch> table;
    @UiField
    HTMLPanel tableContainer;
    @UiField
    HTMLPanel previewContainer;
    @UiField
    HTMLPanel filterContainer;
    @UiField
    HTMLPanel pagerContainer;

    @Inject
    CardStateLang cardStateLang;
    @Inject
    En_PersonRoleTypeLang personRoleTypeLang;

    private List<ClickColumn> columns = new ArrayList<>();
    private ClickColumnProvider<CardBatch> columnProvider = new ClickColumnProvider<>();
    private AbstractCardBatchTableActivity activity;

    private static TableViewUiBinder ourUiBinder = GWT.create(TableViewUiBinder.class);
    interface TableViewUiBinder extends UiBinder<HTMLPanel, CardBatchTableView> {}
}
