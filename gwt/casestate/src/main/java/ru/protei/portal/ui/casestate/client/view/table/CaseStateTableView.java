package ru.protei.portal.ui.casestate.client.view.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.TableWidget;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.ui.casestate.client.activity.table.AbstractCaseStateTableActivity;
import ru.protei.portal.ui.casestate.client.activity.table.AbstractCaseStateTableView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.lang.En_CaseStateUsageInCompaniesLang;
import ru.protei.portal.ui.common.client.lang.Lang;

import java.util.ArrayList;
import java.util.List;

import static ru.protei.portal.core.model.dict.En_CaseStateUsageInCompanies.NONE;
import static ru.protei.portal.core.model.dict.En_CaseStateUsageInCompanies.SELECTED;
import static ru.protei.portal.core.model.dict.En_CaseStateUsageInCompanies.ALL;
import static ru.protei.portal.core.model.helper.StringUtils.defaultString;

public class CaseStateTableView extends Composite implements AbstractCaseStateTableView {

    private AbstractCaseStateTableActivity activity;

    @Inject
    public void onInit(En_CaseStateUsageInCompaniesLang caseStateUsageInCompaniesLang,
                       EditClickColumn< CaseState > editClickColumn
    ) {
        this.caseStateUsageInCompaniesLang = caseStateUsageInCompaniesLang;
        this.editClickColumn = editClickColumn;
        initWidget(ourUiBinder.createAndBindUi(this));
        initTable();
    }

    @Override
    public void setActivity(AbstractCaseStateTableActivity activity) {
        this.activity = activity;

        editClickColumn.setHandler( activity );
        editClickColumn.setEditHandler( activity );
        editClickColumn.setColumnProvider( columnProvider );

        columns.forEach(clickColumn -> {
            clickColumn.setHandler(activity);
            clickColumn.setColumnProvider(columnProvider);
        });
    }

    @Override
    public void setAnimation(TableAnimation animation) {
        animation.setContainers(tableContainer, previewContainer, null);
    }

    @Override
    public HasWidgets getPreviewContainer() {
        return previewContainer;
    }

    @Override
    public void setData(List<CaseState> result) {
        for (CaseState role: result) {
            table.addRow(role);
        }
    }

    @Override
    public void updateRow(CaseState changedCaseState) {
        if(!isAttached()) return;
        table.updateRow(changedCaseState);
    }

    @Override
    public void clearRecords() {
        table.clearRows();
    }

    @Override
    public void clearSelection() {
        columnProvider.removeSelection();
    }

    private void initTable() {
        ClickColumn<CaseState> name = new ClickColumn<CaseState>() {
            @Override
            protected void fillColumnHeader(Element element) {
                element.setInnerText(lang.caseStatesColumnName());
            }

            @Override
            public void fillColumnValue(Element cell, CaseState value) {
                String stateName = value.getState();
                cell.setInnerText(stateName);
            }
        };
        columns.add(name);
        ClickColumn<CaseState> description = new ClickColumn<CaseState>() {
            @Override
            protected void fillColumnHeader(Element element) {
                element.setInnerText(lang.caseStatesColumnInfo());
            }

            @Override
            public void fillColumnValue(Element cell, CaseState value) {
                cell.setInnerText(defaultString(value.getInfo(), ""));
            }
        };
        columns.add(description);

        ClickColumn<CaseState> usageInCompanies = new ClickColumn<CaseState>() {
            @Override
            protected void fillColumnHeader(Element element) {
                element.setInnerText(lang.caseStatesColumnUsageInCompanies());
            }

            @Override
            public void fillColumnValue(Element cell, CaseState value) {
                String message = "";
                switch (value.getUsageInCompanies()) {
                    case NONE:
                        message = "<i class=\"fa fa-ban m-r-10\"></i>" + caseStateUsageInCompaniesLang.getStateName(NONE);
                        break;
                    case ALL:
                        message = "<i class=\"fa fa-users m-r-10 text-complete\"></i>" + caseStateUsageInCompaniesLang.getStateName(ALL);
                        break;
                    case SELECTED:
                        message = "<i class=\"fa fa-user m-r-10 text-purple\"></i>" + caseStateUsageInCompaniesLang.getStateName(SELECTED);
                        break;
                }
                cell.setInnerHTML(message);
            }
        };
        columns.add(usageInCompanies);

        table.addColumn(name.header, name.values);
        table.addColumn(description.header, description.values);
        table.addColumn(usageInCompanies.header, usageInCompanies.values);

        editClickColumn.setEnabledPredicate(v -> policyService.hasPrivilegeFor(En_Privilege.CASE_STATES_EDIT) );

        table.addColumn( editClickColumn.header, editClickColumn.values );
    }

    @UiField
    TableWidget<CaseState> table;
    @UiField
    HTMLPanel tableContainer;
    @UiField
    HTMLPanel previewContainer;

    @UiField
    Lang lang;

    @Inject
    PolicyService policyService;

    private ClickColumnProvider<CaseState> columnProvider = new ClickColumnProvider<>();
    private List<ClickColumn> columns = new ArrayList<>();
    private En_CaseStateUsageInCompaniesLang caseStateUsageInCompaniesLang;
    private  EditClickColumn< CaseState > editClickColumn;

    private static CaseStateTableViewUiBinder ourUiBinder = GWT.create(CaseStateTableViewUiBinder.class);

    interface CaseStateTableViewUiBinder extends UiBinder<HTMLPanel, CaseStateTableView> {
    }
}
