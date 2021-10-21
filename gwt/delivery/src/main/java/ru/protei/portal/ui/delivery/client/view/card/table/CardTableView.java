package ru.protei.portal.ui.delivery.client.view.card.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Card;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.columns.RemoveClickColumn;
import ru.protei.portal.ui.common.client.lang.CardStateLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.delivery.client.activity.card.table.AbstractCardTableActivity;
import ru.protei.portal.ui.delivery.client.activity.card.table.AbstractCardTableView;
import ru.protei.portal.ui.delivery.client.view.card.table.column.*;

import java.util.ArrayList;
import java.util.List;


public class CardTableView extends Composite implements AbstractCardTableView {

    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
        initTable();
    }

    @Override
    public void setActivity(AbstractCardTableActivity activity) {
        this.activity = activity;

        editClickColumn.setEditHandler(activity);
        editClickColumn.setEnabledPredicate(v -> policyService.hasPrivilegeFor(En_Privilege.CARD_EDIT));

        removeClickColumn.setRemoveHandler(activity);
        removeClickColumn.setEnabledPredicate(v -> policyService.hasPrivilegeFor(En_Privilege.CARD_REMOVE));

        columns.forEach(clickColumn -> {
            clickColumn.setHandler(activity);
            clickColumn.setColumnProvider(columnProvider);
        });

        table.setPagerListener(activity);
        table.setLoadHandler(activity);
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
    public void updateRow(Card item) {
        if(item != null) {
            table.updateRow(item);
        }
    }

    private void initTable() {
        NumberColumn number = new NumberColumn(lang, cardStateLang);
        table.addColumn(number.header, number.values);
        number.setColumnProvider(columnProvider);

        CardTypeColumn cardType = new CardTypeColumn(lang);
        table.addColumn(cardType.header, cardType.values);
        cardType.setColumnProvider(columnProvider);

        ManagerColumn manager = new ManagerColumn(lang);
        table.addColumn(manager.header, manager.values);
        manager.setColumnProvider(columnProvider);

        TestDateColumn testDate = new TestDateColumn(lang);
        table.addColumn(testDate.header, testDate.values);
        testDate.setColumnProvider(columnProvider);

        InfoColumn info = new InfoColumn(lang);
        table.addColumn(info.header, info.values);
        info.setColumnProvider(columnProvider);

        table.addColumn(editClickColumn.header, editClickColumn.values);
        table.addColumn(removeClickColumn.header, removeClickColumn.values);

        columns.add(number);
        columns.add(info);
        columns.add(cardType);
        columns.add(manager);
        columns.add(testDate);
        columns.add(editClickColumn);
        columns.add(removeClickColumn);
    }

    @UiField
    Lang lang;
    @UiField
    InfiniteTableWidget<Card> table;
    @UiField
    HTMLPanel tableContainer;
    @UiField
    HTMLPanel previewContainer;
    @UiField
    HTMLPanel filterContainer;
    @UiField
    HTMLPanel pagerContainer;

    @Inject
    EditClickColumn<Card> editClickColumn;
    @Inject
    RemoveClickColumn<Card> removeClickColumn;

    @Inject
    CardStateLang cardStateLang;
    @Inject
    private PolicyService policyService;

    private final List<ClickColumn<Card>> columns = new ArrayList<>();

    private final ClickColumnProvider<Card> columnProvider = new ClickColumnProvider<>();
    private AbstractCardTableActivity activity;

    private static TableViewUiBinder ourUiBinder = GWT.create(TableViewUiBinder.class);
    interface TableViewUiBinder extends UiBinder<HTMLPanel, CardTableView> {}
}
