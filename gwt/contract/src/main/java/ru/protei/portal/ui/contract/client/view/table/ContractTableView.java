package ru.protei.portal.ui.contract.client.view.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.common.MoneyRenderer;
import ru.protei.portal.ui.common.client.lang.En_ContractKindLang;
import ru.protei.portal.ui.common.client.lang.En_ContractStateLang;
import ru.protei.portal.ui.common.client.lang.En_ContractTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.contract.client.activity.table.AbstractContractTableActivity;
import ru.protei.portal.ui.contract.client.activity.table.AbstractContractTableView;

import static ru.protei.portal.core.model.helper.CollectionUtils.isNotEmpty;
import static ru.protei.portal.core.model.helper.CollectionUtils.joining;
import static ru.protei.portal.core.model.util.ContractSupportService.getContractKind;
import static ru.protei.portal.ui.common.shared.util.HtmlUtils.sanitizeHtml;
import static ru.protei.portal.core.model.helper.StringUtils.isNotBlank;

public class ContractTableView extends Composite implements AbstractContractTableView {

    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractContractTableActivity activity) {
        this.activity = activity;
        initTable();
    }

    @Override
    public void setAnimation(TableAnimation animation) {
        animation.setContainers(tableContainer, previewContainer, filterContainer);
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
    public HTMLPanel getFilterContainer() {
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

    private void initTable() {

        columnProvider = new ClickColumnProvider<>();

        table.addColumn(columnState.header, columnState.values);
        columnState.setHandler(activity);
        columnState.setColumnProvider(columnProvider);

        table.addColumn(columnInfo.header, columnInfo.values);
        columnInfo.setHandler(activity);
        columnInfo.setColumnProvider(columnProvider);

        table.addColumn(columnContractor.header, columnContractor.values);
        columnContractor.setHandler(activity);
        columnContractor.setColumnProvider(columnProvider);

        table.addColumn(columnDescription.header, columnDescription.values);
        columnDescription.setHandler(activity);
        columnDescription.setColumnProvider(columnProvider);

        table.addColumn(columnWorkGroup.header, columnWorkGroup.values);
        columnWorkGroup.setHandler(activity);
        columnWorkGroup.setColumnProvider(columnProvider);

        table.addColumn(columnCost.header, columnCost.values);
        columnCost.setHandler(activity);
        columnCost.setColumnProvider(columnProvider);

        editClickColumn.setEnabledPredicate(v -> policyService.hasPrivilegeFor(En_Privilege.CONTRACT_EDIT));
        table.addColumn(editClickColumn.header, editClickColumn.values);
        editClickColumn.setActionHandler(activity);
        editClickColumn.setEditHandler(activity);
        editClickColumn.setHandler(activity);
        editClickColumn.setColumnProvider(columnProvider);

        table.setLoadHandler(activity);
        table.setPagerListener(activity);
    }

    private final ClickColumn<Contract> columnState = new ClickColumn<Contract>() {
        protected String getColumnClassName() { return "contract-column-state"; }
        protected void fillColumnHeader(Element columnHeader) {}
        public void fillColumnValue(Element cell, Contract contract) {
            Element root = DOM.createDiv();
            ImageElement image = DOM.createImg().cast();
            image.addClassName("height-40");
            // https://www.flaticon.com/authors/flat_circular/flat
            // https://www.flaticon.com/packs/business-strategy-2
            image.setSrc( "./images/contract_" + contract.getState().name().toLowerCase() + ".png" );
            image.setTitle( contractStateLang.getName(contract.getState()) );
            root.appendChild(image);
            cell.appendChild(root);
        }
    };

    private final ClickColumn<Contract> columnInfo = new ClickColumn<Contract>(){
        protected String getColumnClassName() { return "contract-column-info"; }
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.setInnerText(lang.contractNumber());
        }
        public void fillColumnValue(Element cell, Contract contract) {
            Element root = DOM.createDiv();
            StringBuilder sb = new StringBuilder();
            sb.append("<b>")
                    .append(sanitizeHtml(contractTypeLang.getName(contract.getContractType())))
                    .append(" № ")
                    .append(sanitizeHtml(contract.getNumber()))
                    .append("</b>");
            sb.append("<br/>");
            sb.append("<small>");
            sb.append(contractKindLang.getName(getContractKind(contract)))
                    .append("<br/>");
            sb.append("<b>")
                    .append(lang.contractDateSigning())
                    .append(":</b> ")
                    .append(contract.getDateSigning() != null
                            ? dateFormat.format(contract.getDateSigning())
                            : lang.contractDateNotDefined());
            sb.append("<br/>");
            sb.append("<b>")
                    .append(lang.contractDateValid())
                    .append(":</b> ")
                    .append(contract.getDateValid() != null
                            ? dateFormat.format(contract.getDateValid())
                            : lang.contractDateNotDefined());
            sb.append("</small>");
            root.setInnerHTML(sb.toString());
            cell.appendChild(root);
        }
    };

    private final ClickColumn<Contract> columnContractor = new ClickColumn<Contract>(){
        protected String getColumnClassName() { return "contract-column-contractor"; }
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.setInnerText(lang.contractContractor());
        }
        public void fillColumnValue(Element cell, Contract contract) {
            Element root = DOM.createDiv();
            StringBuilder sb = new StringBuilder();
            sb.append(sanitizeHtml(contract.getContractor() != null
                    ? contract.getContractor().getName()
                    : ""));
            root.setInnerHTML(sb.toString());
            cell.appendChild(root);
        }
    };

    private final ClickColumn<Contract> columnDescription = new ClickColumn<Contract>(){
        protected String getColumnClassName() { return "contract-column-description"; }
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.setInnerText(lang.contractDescription());
        }
        public void fillColumnValue(Element cell, Contract contract) {
            Element root = DOM.createDiv();
            StringBuilder sb = new StringBuilder();
            sb.append("<b>")
                    .append(lang.contractProject())
                    .append(":</b> ");
            if (isNotEmpty(contract.getProductDirections())) {
                sb.append("<i>")
                        .append(joining(contract.getProductDirections(), ", ", direction -> sanitizeHtml(direction.getName())))
                        .append("</i>")
                        .append(" ");
            }
            sb.append(sanitizeHtml(contract.getProjectName()));
            sb.append("<br/>");
            sb.append("<b>")
                    .append(lang.contractDescription())
                    .append(":</b> ")
                    .append(sanitizeHtml(contract.getDescription()));
            root.setInnerHTML(sb.toString());
            cell.appendChild(root);
        }
    };

    private final ClickColumn<Contract> columnWorkGroup = new ClickColumn<Contract>(){
        protected String getColumnClassName() { return "contract-column-work-group"; }
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.setInnerText(lang.contractWorkGroup());
        }
        public void fillColumnValue(Element cell, Contract contract) {
            Element root = DOM.createDiv();
            StringBuilder sb = new StringBuilder();
            sb.append("<b>")
                    .append(lang.contractOrganization())
                    .append(":</b> ")
                    .append(sanitizeHtml(contract.getOrganizationName()))
                    .append("<br/>");
            sb.append("<b>")
                    .append(lang.contractProjectManager())
                    .append(":</b> ")
                    .append(sanitizeHtml(contract.getProjectManagerShortName()))
                    .append("<br/>");
            sb.append("<b>")
                    .append(lang.contractCurator())
                    .append(":</b> ")
                    .append(sanitizeHtml(contract.getCuratorShortName()))
                    .append("<br/>");
            root.setInnerHTML(sb.toString());
            cell.appendChild(root);
        }
    };

    private final ClickColumn<Contract> columnCost = new ClickColumn<Contract>(){
        protected String getColumnClassName() { return "contract-column-cost"; }
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.setInnerText(lang.contractCost());
        }
        public void fillColumnValue(Element cell, Contract contract) {
            Element root = DOM.createDiv();
            StringBuilder sb = new StringBuilder();
            if (contract.getCost() != null) {
                sb.append(MoneyRenderer.getInstance().render(contract.getCost())).append(" ");
                if (contract.getCurrency() != null) {
                    sb.append(contract.getCurrency().getCode()).append(" ");
                }
                sb.append("<br/>").append(contract.getVat() != null
                        ? lang.vat(contract.getVat())
                        : lang.withoutVat());
            }
            root.setInnerHTML(sb.toString());
            cell.appendChild(root);
        }
    };

    @Inject
    @UiField
    Lang lang;
    @UiField
    InfiniteTableWidget<Contract> table;
    @UiField
    HTMLPanel tableContainer;
    @UiField
    HTMLPanel previewContainer;
    @UiField
    HTMLPanel filterContainer;
    @UiField
    HTMLPanel pagerContainer;

    @Inject
    private EditClickColumn<Contract> editClickColumn;
    @Inject
    private En_ContractStateLang contractStateLang;
    @Inject
    private En_ContractTypeLang contractTypeLang;
    @Inject
    private En_ContractKindLang contractKindLang;
    @Inject
    private PolicyService policyService;

    private ClickColumnProvider<Contract> columnProvider = new ClickColumnProvider<>();
    private AbstractContractTableActivity activity;
    private final DateTimeFormat dateFormat = DateTimeFormat.getFormat("dd.MM.yyyy");

    private static TableViewUiBinder ourUiBinder = GWT.create(TableViewUiBinder.class);
    interface TableViewUiBinder extends UiBinder<HTMLPanel, ContractTableView> {}
}
