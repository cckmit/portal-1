package ru.protei.portal.ui.issuereport.client.view.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.client.view.input.range.RangePicker;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.dict.En_ReportType;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.widget.cleanablesearchbox.CleanableSearchBox;
import ru.protei.portal.ui.common.client.widget.issuestate.optionlist.IssueStatesOptionList;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanyMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.InitiatorMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.product.devunit.DevUnitMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.SortFieldSelector;
import ru.protei.portal.ui.common.client.widget.threestate.ThreeStateButton;
import ru.protei.portal.ui.issue.client.widget.importance.btngroup.ImportanceBtnGroupMulti;
import ru.protei.portal.ui.issuereport.client.activity.create.AbstractIssueReportCreateActivity;
import ru.protei.portal.ui.issuereport.client.activity.create.AbstractIssueReportCreateView;
import ru.protei.portal.ui.issuereport.client.widget.ReportTypeButtonSelector;

import java.util.Set;

public class IssueReportCreateView extends Composite implements AbstractIssueReportCreateView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractIssueReportCreateActivity activity) {
    }

    @Override
    public HasValue<En_ReportType> reportType() {
        return reportType;
    }

    @Override
    public HasValue<String> name() {
        return name;
    }

    @Override
    public HasValue<Set<EntityOption>> companies() {
        return companies;
    }

    @Override
    public HasValue<Set<ProductShortView>> products() {
        return products;
    }

    @Override
    public HasValue<Set<PersonShortView>> managers() {
        return managers;
    }

    @Override
    public HasValue<Set<PersonShortView>> initiators() {
        return initiators;
    }

    @Override
    public HasValue<Set<En_CaseState>> states() {
        return state;
    }

    @Override
    public HasValue<Set<En_ImportanceLevel>> importances() {
        return importance;
    }

    @Override
    public HasValue<DateInterval> dateRange() {
        return dateRange;
    }

    @Override
    public HasValue<En_SortField> sortField() {
        return sortField;
    }

    @Override
    public HasValue<Boolean> sortDir() {
        return sortDir;
    }

    @Override
    public HasValue<String> searchPattern() {
        return search;
    }

    @Override
    public HasValue<Boolean> searchPrivate() {
        return searchPrivate;
    }

    @Override
    public HasVisibility companiesVisibility() {
        return companies;
    }

    @Override
    public HasVisibility productsVisibility() {
        return products;
    }

    @Override
    public HasVisibility managersVisibility() {
        return managers;
    }

    @Override
    public HasVisibility searchPrivateVisibility() {
        return searchPrivateContainer;
    }

    @Override
    public HasValue<Set<PersonShortView>> commentAuthors() {
        return commentAuthors;
    }

    @Override
    public void resetFilter() {
        reportType.setValue(En_ReportType.CRM_CASE_OBJECTS, true);
        name.setValue(null);
        companies.setValue(null);
        products.setValue(null);
        managers.setValue(null);
        initiators.setValue(null);
        importance.setValue(null);
        state.setValue(null);
        dateRange.setValue(null);
        sortField.setValue(En_SortField.creation_date);
        sortDir.setValue(false);
        search.setValue("");
        searchPrivate.setValue(null);
        commentAuthors.setValue(null);
    }

    @UiHandler("reportType")
    public void onInitiatorsSelected(ValueChangeEvent<En_ReportType> event) {
        commentFiltersBlock.setVisible(En_ReportType.CRM_MANAGER_TIME.equals(reportType.getValue()));
    }

    @Inject
    @UiField(provided = true)
    ReportTypeButtonSelector reportType;
    @UiField
    TextBox name;

    @UiField
    CleanableSearchBox search;
    @Inject
    @UiField(provided = true)
    SortFieldSelector sortField;
    @UiField
    ToggleButton sortDir;
    @Inject
    @UiField(provided = true)
    RangePicker dateRange;
    @Inject
    @UiField(provided = true)
    CompanyMultiSelector companies;
    @Inject
    @UiField(provided = true)
    DevUnitMultiSelector products;
    @Inject
    @UiField(provided = true)
    EmployeeMultiSelector managers;
    @Inject
    @UiField(provided = true)
    InitiatorMultiSelector initiators;
    @UiField
    HTMLPanel searchPrivateContainer;
    @UiField
    ThreeStateButton searchPrivate;
    @Inject
    @UiField(provided = true)
    ImportanceBtnGroupMulti importance;
    @Inject
    @UiField(provided = true)
    IssueStatesOptionList state;

    @UiField
    HTMLPanel commentFiltersBlock;
    @Inject
    @UiField(provided = true)
    EmployeeMultiSelector commentAuthors;

    interface IssueReportCreateViewUiBinder extends UiBinder<Widget, IssueReportCreateView> {}
    private static IssueReportCreateViewUiBinder ourUiBinder = GWT.create(IssueReportCreateViewUiBinder.class);
}
