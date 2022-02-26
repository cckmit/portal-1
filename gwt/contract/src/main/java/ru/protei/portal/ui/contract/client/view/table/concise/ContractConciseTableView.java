package ru.protei.portal.ui.contract.client.view.table.concise;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.TableWidget;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.lang.En_ContractKindLang;
import ru.protei.portal.ui.common.client.lang.En_ContractStateLang;
import ru.protei.portal.ui.common.client.lang.En_ContractTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.shared.util.HtmlUtils;
import ru.protei.portal.ui.contract.client.activity.table.concise.AbstractContractConciseTableActivity;
import ru.protei.portal.ui.contract.client.activity.table.concise.AbstractContractConciseTableView;

import java.util.List;

import static ru.protei.portal.core.model.helper.CollectionUtils.stream;
import static ru.protei.portal.core.model.util.ContractSupportService.getContractKind;
import static ru.protei.portal.ui.common.shared.util.HtmlUtils.sanitizeHtml;

public class ContractConciseTableView extends Composite implements AbstractContractConciseTableView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractContractConciseTableActivity activity) {
        this.activity = activity;
        initTable();
    }

    @Override
    public void clearRecords() {
        table.clearRows();
    }

    @Override
    public void setData(List<Contract> data) {
        data.forEach(table::addRow);
    }

    private void initTable() {

        ClickColumnProvider<Contract> columnProvider = new ClickColumnProvider<>();

        table.addColumn(columnState.header, columnState.values);
        columnState.setHandler(activity);
        columnState.setColumnProvider(columnProvider);

        table.addColumn(columnInfo.header, columnInfo.values);
        columnInfo.setHandler(activity);
        columnInfo.setColumnProvider(columnProvider);

        table.addColumn(columnContractor.header, columnContractor.values);
        columnContractor.setHandler(activity);
        columnContractor.setColumnProvider(columnProvider);

        editClickColumn.setEnabledPredicate(v -> policyService.hasPrivilegeFor(En_Privilege.CONTRACT_EDIT) );
        table.addColumn(editClickColumn.header, editClickColumn.values);
        editClickColumn.setActionHandler(activity);
        editClickColumn.setHandler(activity);
        editClickColumn.setColumnProvider(columnProvider);
    }

    private final ClickColumn<Contract> columnState = new ClickColumn<Contract>() {
        protected String getColumnClassName() { return "column-state"; }
        protected void fillColumnHeader(Element columnHeader) {}
        public void fillColumnValue(Element cell, Contract contract) {
            Element root = DOM.createDiv();
            ImageElement image = DOM.createImg().cast();
            image.addClassName("height-30");
            image.setSrc("./images/contract_" + contract.getStateName().toLowerCase() + ".png");
            image.setTitle(stateLang.getName(contract.getStateName()));
            root.appendChild(image);
            cell.appendChild(root);
        }
    };

    private final ClickColumn<Contract> columnInfo = new ClickColumn<Contract>(){
        protected String getColumnClassName() { return "column-info"; }
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.setInnerText(lang.contractNumber());
        }
        public void fillColumnValue(Element cell, Contract contract) {
            Element root = DOM.createDiv();
            StringBuilder sb = new StringBuilder();
            sb.append("<b>")
                    .append(sanitizeHtml(typeLang.getName(contract.getContractType())))
                    .append(" ")
                    .append(HtmlUtils.sanitizeHtml(contract.getNumber()))
                    .append("</b>");
            sb.append("<br/>");
            sb.append("<small>");
            sb.append(kindLang.getName(getContractKind(contract)));
            sb.append("</small>");
            root.setInnerHTML(sb.toString());
            cell.appendChild(root);
        }
    };

    private final ClickColumn<Contract> columnContractor = new ClickColumn<Contract>(){
        protected String getColumnClassName() { return "column-contractor"; }
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.setInnerText(lang.contractContractor());
        }
        public void fillColumnValue(Element cell, Contract contract) {
            Element root = DOM.createDiv();
            StringBuilder sb = new StringBuilder();
            sb.append(HtmlUtils.sanitizeHtml(contract.getContractor() != null
                    ? contract.getContractor().getName()
                    : ""));
            root.setInnerHTML(sb.toString());
            cell.appendChild(root);
        }
    };

    @Inject
    EditClickColumn<Contract> editClickColumn;
    @Inject
    PolicyService policyService;
    @Inject
    En_ContractStateLang stateLang;
    @Inject
    En_ContractTypeLang typeLang;
    @Inject
    En_ContractKindLang kindLang;

    @Inject
    @UiField
    Lang lang;

    @UiField
    HTMLPanel tableContainer;
    @UiField
    TableWidget<Contract> table;

    private AbstractContractConciseTableActivity activity;

    private static ContractConciseTableViewUiBinder ourUiBinder = GWT.create(ContractConciseTableViewUiBinder.class);
    interface ContractConciseTableViewUiBinder extends UiBinder<HTMLPanel, ContractConciseTableView> {}
}
