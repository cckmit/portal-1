package ru.protei.portal.ui.contract.client.view.date.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.AbstractColumn;
import ru.brainworm.factory.widget.table.client.TableWidget;
import ru.brainworm.factory.widget.table.client.helper.StaticColumn;
import ru.brainworm.factory.widget.table.client.helper.StaticTextColumn;
import ru.protei.portal.core.model.dict.En_ContractDatesType;
import ru.protei.portal.core.model.ent.ContractDate;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.columns.RemoveClickColumn;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.common.MoneyRenderer;
import ru.protei.portal.ui.common.client.lang.En_ContractDatesTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.contract.client.activity.date.table.AbstractContractDateTableActivity;
import ru.protei.portal.ui.contract.client.activity.date.table.AbstractContractDateTableView;

import java.util.List;

public class ContractDateTableView extends Composite implements AbstractContractDateTableView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        initTable();
        initDebugIds();
    }

    @Override
    public void setActivity(AbstractContractDateTableActivity activity) {
        this.activity = activity;

        editColumn.setEditHandler(activity);
        removeColumn.setRemoveHandler(activity);
    }

    @Override
    public void setData(List<ContractDate> values) {
        table.clearRows();
        if (values == null) return;
        values.forEach(value -> table.addRow(value));
    }

    @Override
    public void showEditableColumns(boolean isVisible) {
        abstractEditColumn.setVisibility(isVisible);
        abstractRemoveColumn.setVisibility(isVisible);
    }

    @Override
    public void removeRow(ContractDate value) {
        table.removeRow(value);
    }

    @Override
    public void addRow(ContractDate value) {
        table.addRow(value);
    }

    @Override
    public void showWarning(String message) {
        warning.setVisible(true);
        warningText.setText(message);
    }

    @Override
    public void hideWarning() {
        warning.setVisible(false);
        warningText.setText(null);
    }

    private void initTable() {
        StaticTextColumn<ContractDate> dateColumn = new StaticTextColumn<ContractDate>(lang.contractDateColumn()) {
            @Override
            public String getColumnValue(ContractDate value) {
                return DateFormatter.formatDateOnly(value.getDate());
            }
        };
        table.addColumn(dateColumn.header, dateColumn.values);

        StaticColumn<ContractDate> costColumn = new StaticColumn<ContractDate>(lang.contractCostColumn()) {
            @Override
            public void fillColumnValue(Element element, ContractDate value) {
                String html = "<b>" + datesTypeLang.getName(value.getType()) + "</b>";
                if (value.getCost() != null && (En_ContractDatesType.PREPAYMENT.equals(value.getType())
                        || En_ContractDatesType.POSTPAYMENT.equals(value.getType()))) {
                    html += " " + lang.contractInAmount(MoneyRenderer.getInstance().render(value.getCost())) + " "
                            + (value.getCurrency() != null ? value.getCurrency().getCode() : "");
                }
                element.setInnerHTML(html);
            }
        };
        table.addColumn(costColumn.header, costColumn.values);

        StaticColumn<ContractDate> commentColumn = new StaticColumn<ContractDate>(lang.contractCommentColumn()) {
            @Override
            public void fillColumnValue(Element element, ContractDate value) {
                element.setInnerHTML("<small><i>" + StringUtils.emptyIfNull(value.getComment()) + "</i></small>");
            }
        };
        table.addColumn(commentColumn.header, commentColumn.values);

        editColumn = new EditClickColumn<>(lang);
        abstractEditColumn = table.addColumn(editColumn.header, editColumn.values);
        editColumn.setEditHandler(activity);

        removeColumn = new RemoveClickColumn<>(lang);
        abstractRemoveColumn = table.addColumn(removeColumn.header, removeColumn.values);
        removeColumn.setRemoveHandler(activity);
    }

    private void initDebugIds() {
    }

    @UiField
    TableWidget<ContractDate> table;
    @UiField
    Lang lang;
    @UiField
    HTMLPanel warning;
    @UiField
    InlineLabel warningText;

    @Inject
    En_ContractDatesTypeLang datesTypeLang;

    EditClickColumn<ContractDate> editColumn;
    RemoveClickColumn<ContractDate> removeColumn;

    AbstractColumn abstractEditColumn;
    AbstractColumn abstractRemoveColumn;
    AbstractContractDateTableActivity activity;

    private static ContractDateTableTableViewUiBinder ourUiBinder = GWT.create(ContractDateTableTableViewUiBinder.class);
    interface ContractDateTableTableViewUiBinder extends UiBinder<HTMLPanel, ContractDateTableView> {}
}
