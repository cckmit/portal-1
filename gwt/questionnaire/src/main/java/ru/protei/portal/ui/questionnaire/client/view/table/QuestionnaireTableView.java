package ru.protei.portal.ui.questionnaire.client.view.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.ent.Questionnaire;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.questionnaire.client.activity.table.AbstractQuestionnaireTableActivity;
import ru.protei.portal.ui.questionnaire.client.activity.table.AbstractQuestionnaireTableView;

import java.util.LinkedList;
import java.util.List;

public class QuestionnaireTableView extends Composite implements AbstractQuestionnaireTableView {

    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
        initTable();
    }

    @Override
    public void setActivity(AbstractQuestionnaireTableActivity activity) {
        clickColumns.forEach(col -> {
            col.setHandler(activity);
            col.setColumnProvider(columnProvider);
        });

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
    public void setRecordCount(int count) {
        table.setTotalRecords(count);
    }

    @Override
    public HasWidgets getPreviewContainer() {
        return previewContainer;
    }

    @Override
    public HTMLPanel getFilterContainer() {
        return filterContainer;
    }

    private void initTable() {
        ClickColumn<Questionnaire> fullName = new ClickColumn<Questionnaire>() {
            @Override
            protected void fillColumnHeader(Element columnHeader) {
                columnHeader.setInnerText(lang.questionnaireEmployeeFullNameColumnHeader());
            }

            @Override
            public void fillColumnValue(Element cell, Questionnaire value) {
                cell.setInnerText(value.getEmployeeFullName());
            }
        };

        ClickColumn<Questionnaire> headOfDepartment = new ClickColumn<Questionnaire>() {
            @Override
            protected void fillColumnHeader(Element columnHeader) {
                columnHeader.setInnerText(lang.questionnaireHeadOfDepartmentColumnHeader());
            }

            @Override
            public void fillColumnValue(Element cell, Questionnaire value) {
                if (value.getHeadOfDepartment() == null) {
                    cell.setInnerText("");
                    return;
                }
                cell.setInnerText(value.getHeadOfDepartment().getDisplayShortName());
            }
        };

        ClickColumn<Questionnaire> employmentDate = new ClickColumn<Questionnaire>() {
            @Override
            protected void fillColumnHeader(Element columnHeader) {
                columnHeader.setInnerText(lang.questionnaireEmploymentDateColumnHeader());
            }

            @Override
            public void fillColumnValue(Element cell, Questionnaire value) {
                cell.setInnerText(DateFormatter.formatDateOnly(value.getEmploymentDate()));
            }
        };

        clickColumns.add(fullName);
        clickColumns.add(headOfDepartment);
        clickColumns.add(employmentDate);

        clickColumns.forEach(c -> table.addColumn(c.header, c.values));
    }

    @UiField
    InfiniteTableWidget<Questionnaire> table;

    @UiField
    HTMLPanel tableContainer;
    @UiField
    HTMLPanel previewContainer;
    @UiField
    HTMLPanel filterContainer;

    @Inject
    @UiField
    Lang lang;

    private ClickColumnProvider<Questionnaire> columnProvider = new ClickColumnProvider<>();

    private List<ClickColumn<Questionnaire>> clickColumns = new LinkedList<>();

    private static QuestionnaireUiBinder ourUiBinder = GWT.create(QuestionnaireUiBinder.class);
    interface QuestionnaireUiBinder extends UiBinder<HTMLPanel, QuestionnaireTableView> {
    }
}
