package ru.protei.portal.ui.casestate.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.TableWidget;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.ui.casestate.client.activity.table.AbstractCaseStateTableActivity;
import ru.protei.portal.ui.casestate.client.activity.table.AbstractCaseStateTableView;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.lang.En_CaseStateLang;
import ru.protei.portal.ui.common.client.lang.En_CaseStateUsageInCompaniesLang;

import java.util.ArrayList;
import java.util.List;

import static ru.protei.portal.core.model.helper.StringUtils.defaultString;

public class CaseStateTableView extends Composite implements AbstractCaseStateTableView {

    private AbstractCaseStateTableActivity activity;

    @Inject
    public void onInit(En_CaseStateLang caseStateLang,
                       En_CaseStateUsageInCompaniesLang caseStateUsageInCompaniesLang,
                       EditClickColumn< CaseState > editClickColumn
    ) {
        this.caseStateLang = caseStateLang;
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
        animation.setContainers(tableContainer, previewContainer, filterContainer);
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


    private void initTable() {
        ClickColumn<CaseState> name = new ClickColumn<CaseState>() {
            @Override
            protected void fillColumnHeader(Element element) {
                element.setInnerText("Статус");
            }

            @Override
            public void fillColumnValue(Element cell, CaseState value) {
                String stateName = caseStateLang.getStateName(En_CaseState.getById(value.getId()));
                cell.setInnerText(stateName);
            }
        };
        columns.add(name);
        ClickColumn<CaseState> description = new ClickColumn<CaseState>() {
            @Override
            protected void fillColumnHeader(Element element) {
                element.setInnerText("Описание");
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
                element.setInnerText("Применим к компаниям");
            }

            @Override
            public void fillColumnValue(Element cell, CaseState value) {
                cell.setInnerText(defaultString(caseStateUsageInCompaniesLang.getStateName(value.getUsageInCompanies()), ""));
            }
        };
        columns.add(usageInCompanies);

        table.addColumn(name.header, name.values);
        table.addColumn(description.header, description.values);
        table.addColumn(usageInCompanies.header, usageInCompanies.values);

        editClickColumn.setPrivilege( En_Privilege.CASE_STATES_EDIT );
        table.addColumn( editClickColumn.header, editClickColumn.values );

    }

    @UiField
    TableWidget<CaseState> table;
    @UiField
    HTMLPanel tableContainer;
    @UiField
    HTMLPanel previewContainer;
    @UiField
    HTMLPanel filterContainer;

    En_CaseStateLang caseStateLang;

    private ClickColumnProvider<CaseState> columnProvider = new ClickColumnProvider<>();
    private List<ClickColumn> columns = new ArrayList<>();
    private En_CaseStateUsageInCompaniesLang caseStateUsageInCompaniesLang;
    private  EditClickColumn< CaseState > editClickColumn;

    private static CaseStateTableViewUiBinder ourUiBinder = GWT.create(CaseStateTableViewUiBinder.class);

    interface CaseStateTableViewUiBinder extends UiBinder<HTMLPanel, CaseStateTableView> {
    }
}
