package ru.protei.portal.ui.contract.client.view.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SimpleHtmlSanitizer;
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
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.columns.DynamicColumn;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.lang.En_ContractStateLang;
import ru.protei.portal.ui.common.client.lang.En_ContractTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.contract.client.activity.table.AbstractContractTableActivity;
import ru.protei.portal.ui.contract.client.activity.table.AbstractContractTableView;

import java.util.LinkedList;
import java.util.List;

import static ru.protei.portal.core.model.helper.StringUtils.trim;

public class ContractTableView extends Composite implements AbstractContractTableView {

    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
        initTable();
    }

    @Override
    public void setActivity(AbstractContractTableActivity activity) {
        clickColumns.forEach(col -> {
            col.setHandler(activity);
            col.setColumnProvider(columnProvider);
        });

        editClickColumn.setEditHandler(activity);
        editClickColumn.setColumnProvider(columnProvider);
        table.setLoadHandler(activity);
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
        editClickColumn.setEnabledPredicate(v -> policyService.hasPrivilegeFor(En_Privilege.CONTRACT_EDIT) );

        ClickColumn<Contract> type = new ClickColumn< Contract >() {
            @Override
            protected void fillColumnHeader( Element columnHeader ) {}

            @Override
            public void fillColumnValue( Element cell, Contract value ) {
                cell.addClassName("contract-type-column");
                Element root = DOM.createDiv();
                cell.appendChild( root );

                ImageElement image = DOM.createImg().cast();
                image.addClassName("height-40");
                image.setSrc( "./images/contract_" + value.getState().name().toLowerCase() + ".png" );
                image.setTitle( contractStateLang.getName(value.getState()) );
                root.appendChild( image );
            }
        };
        clickColumns.add(type);

        DynamicColumn<Contract> numTypeColumn = new DynamicColumn<>(lang.contractNumber(), "num-column",
                contract -> "<b>" + SimpleHtmlSanitizer.sanitizeHtml(lang.contractNum(contract.getNumber())).asString() + "</b><br/>"
                        + "<small>" + contractTypeLang.getName(contract.getContractType()) + "<br/>"
                        + "<b>" + lang.contractDateSigning() + ":</b> " + (contract.getDateSigning() == null ? lang.contractDateNotDefined() : dateFormat.format(contract.getDateSigning())) + "<br/>"
                        + "<b>" + lang.contractDateValid() + ":</b> " + (contract.getDateValid() == null ? lang.contractDateNotDefined() : dateFormat.format(contract.getDateValid()))  + "</small>");
        clickColumns.add(numTypeColumn);

        DynamicColumn<Contract> descriptionColumn = new DynamicColumn<>(lang.contractDescription(), "description-column",
                contract -> "<b>" + (contract.getDirectionId() == null ?  "" : StringUtils.emptyIfNull(contract.getDirectionName() ) + "</b><br/>"
                            + (contract.getProjectId() == null ? "" : StringUtils.emptyIfNull(contract.getProjectName() + "<br/>"))
                            + SimpleHtmlSanitizer.sanitizeHtml(StringUtils.emptyIfNull(contract.getDescription())).asString()));
        clickColumns.add(descriptionColumn);

        DynamicColumn<Contract> workGroupColumn = new DynamicColumn<>(lang.contractWorkGroup(), "work-group-column",
                contract -> "<b>" + lang.contractOrganization() + ":</b> " + StringUtils.emptyIfNull(contract.getOrganizationName()) + "</b><br/>"
                        +  "<b>" + lang.contractManager() + ":</b> " + (contract.getProjectId() == null ? StringUtils.emptyIfNull(contract.getCaseManagerShortName()) : StringUtils.emptyIfNull(contract.getManagerShortName())) + "</b><br/>"
                        +  "<b>" + lang.contractCurator() + ":</b> " + StringUtils.emptyIfNull(contract.getCuratorShortName()) + "</b><br/>"
                        +  "<b>" + lang.contractContractor() + ":</b> " + (contract.getContractor() == null ? "" : StringUtils.emptyIfNull(contract.getContractor().getName())) + "</b>");
        clickColumns.add(workGroupColumn);

        DynamicColumn<Contract> costColumn = new DynamicColumn<>(lang.contractCost(), "cost-column",
                contract -> {
                    String cost = contract.getCost() != null
                            ? contract.getCost().toString()
                            : "";
                    String currency = contract.getCurrency() != null
                            ? contract.getCurrency().getCode()
                            : "";
                    String vat = contract.getVat() != null
                            ? lang.vat(contract.getVat())
                            : lang.withoutVat();
                    return trim(cost + " " + currency + " " + vat);
                }
        );
        clickColumns.add(costColumn);

        clickColumns.forEach(c -> table.addColumn(c.header, c.values));
        table.addColumn(editClickColumn.header, editClickColumn.values);
    }

    @UiField
    InfiniteTableWidget<Contract> table;

    @UiField
    HTMLPanel tableContainer;
    @UiField
    HTMLPanel previewContainer;
    @UiField
    HTMLPanel filterContainer;

    @UiField
    Lang lang;
    @UiField
    HTMLPanel pagerContainer;

    @Inject
    private EditClickColumn<Contract> editClickColumn;
    @Inject
    private En_ContractStateLang contractStateLang;
    @Inject
    private En_ContractTypeLang contractTypeLang;

    @Inject
    private PolicyService policyService;

    private ClickColumnProvider<Contract> columnProvider = new ClickColumnProvider<>();
    private List<ClickColumn<Contract>> clickColumns = new LinkedList<>();

    private DateTimeFormat dateFormat = DateTimeFormat.getFormat("dd.MM.yyyy");

    private static TableViewUiBinder ourUiBinder = GWT.create(TableViewUiBinder.class);
    interface TableViewUiBinder extends UiBinder<HTMLPanel, ContractTableView> {}
}
