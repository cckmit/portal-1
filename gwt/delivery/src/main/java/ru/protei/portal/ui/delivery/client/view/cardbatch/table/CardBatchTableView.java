package ru.protei.portal.ui.delivery.client.view.cardbatch.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.CardBatch;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.columns.*;
import ru.protei.portal.ui.common.client.lang.CardBatchStateLang;
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
        createCardClickColumn.setCreateCardHandler(activity);
        createCardClickColumn.setEnabledPredicate(v -> policyService.hasPrivilegeFor(En_Privilege.CARD_CREATE));
        editClickColumn.setEditHandler(activity);
        editClickColumn.setEnabledPredicate(v -> policyService.hasPrivilegeFor(En_Privilege.CARD_BATCH_EDIT));
        removeClickColumn.setRemoveHandler(activity);
        removeClickColumn.setEnabledPredicate(v -> policyService.hasPrivilegeFor(En_Privilege.CARD_BATCH_REMOVE));
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
    public HasWidgets getPreviewContainer() {
        return previewContainer;
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
        columns.add(new NumberColumn(lang, cardBatchStateLang));
        columns.add(new CardTypeColumn(lang));
        columns.add(new AmountColumn(lang));
        columns.add(new DeadlineColumn(lang));
        columns.add(new ContractorsColumn(lang, personRoleTypeLang));
        columns.add(createCardClickColumn);
        columns.add(editClickColumn);
        columns.add(removeClickColumn);

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
    CardBatchStateLang cardBatchStateLang;
    @Inject
    En_PersonRoleTypeLang personRoleTypeLang;
    @Inject
    private CreateCardClickColumn<CardBatch> createCardClickColumn;
    @Inject
    private EditClickColumn<CardBatch> editClickColumn;
    @Inject
    RemoveClickColumn<CardBatch> removeClickColumn;
    @Inject
    private PolicyService policyService;


    private List<ClickColumn<CardBatch>> columns = new ArrayList<>();
    private ClickColumnProvider<CardBatch> columnProvider = new ClickColumnProvider<>();
    private AbstractCardBatchTableActivity activity;

    private static TableViewUiBinder ourUiBinder = GWT.create(TableViewUiBinder.class);
    interface TableViewUiBinder extends UiBinder<HTMLPanel, CardBatchTableView> {}
}
