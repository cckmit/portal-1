package ru.protei.portal.ui.common.client.view.report.timeresolution;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.client.view.input.range.RangePicker;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.issuefilter.AbstractIssueFilterParamActivity;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.issueimportance.btngroup.ImportanceBtnGroupMulti;
import ru.protei.portal.ui.common.client.widget.issuestate.optionlist.IssueStatesOptionList;
import ru.protei.portal.ui.common.client.widget.selector.casetag.CaseTagMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanyMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.product.devunit.DevUnitMultiSelector;

import java.util.Set;

public class ResolutionTimeReportView extends Composite implements AbstractResolutionTimeReportView {
    @Inject
    public void init() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        ensureDebugIds();
        products.updateQuery(null, null);
        tags.setCaseType(En_CaseType.CRM_SUPPORT);
    }
    @Override
    public void setActivity( AbstractIssueFilterParamActivity activity ) {
        this.activity = activity;
    }

    @Override
    public HasValue<Set<EntityOption>> companies() {
        return companies;
    }

    @Override
    public HasValue<Set<ProductShortView>> products() { return products; }

    @Override
    public HasValue<Set<PersonShortView>> managers() {
        return managers;
    }

    @Override
    public HasValue<Set<EntityOption>> tags() {
        return tags;
    }

    @Override
    public HasValue<Set<En_ImportanceLevel>> importances() {
        return importance;
    }

    @Override
    public HasValue<Set<En_CaseState>> states() {
        return state;
    }

    @Override
    public HasValue<DateInterval> dateRange() {
        return dateRange;
    }

    @Override
    public void resetFilter() {
        dateRange.setValue( null );
        companies.setValue( null );
        products.setValue( null );
        managers.setValue( null );
        tags.setValue( null );
        importance.setValue( null );
        state.setValue( null );
    }

    @UiHandler("dateRange")
    public void onDateRangeChanged( ValueChangeEvent<DateInterval> event ) {
        onFilterChanged();
    }

    @UiHandler("companies")
    public void onCompaniesSelected(ValueChangeEvent<Set<EntityOption>> event) { onFilterChanged(); }

    @UiHandler("products")
    public void onProductsSelected(ValueChangeEvent<Set<ProductShortView>> event) {
        onFilterChanged();
    }

    @UiHandler("managers")
    public void onManagersSelected(ValueChangeEvent<Set<PersonShortView>> event) {
        onFilterChanged();
    }

    @UiHandler("tags")
    public void onTagsSelected(ValueChangeEvent<Set<EntityOption>> event) {
        onFilterChanged();
    }

    @UiHandler("importance")
    public void onImportanceSelected(ValueChangeEvent<Set<En_ImportanceLevel>> event) {
        onFilterChanged();
    }

    @UiHandler("state")
    public void onStateSelected( ValueChangeEvent<Set<En_CaseState>> event ) {
        onFilterChanged();
    }

    private void ensureDebugIds() {
        dateRange.setEnsureDebugId( DebugIds.FILTER.DATE_RANGE_SELECTOR );
        companies.setAddEnsureDebugId(DebugIds.FILTER.COMPANY_SELECTOR_ADD_BUTTON);
        companies.setClearEnsureDebugId(DebugIds.FILTER.COMPANY_SELECTOR_CLEAR_BUTTON);
        products.setAddEnsureDebugId(DebugIds.FILTER.PRODUCT_SELECTOR_ADD_BUTTON);
        products.setClearEnsureDebugId(DebugIds.FILTER.PRODUCT_SELECTOR_CLEAR_BUTTON);
        managers.setAddEnsureDebugId(DebugIds.FILTER.MANAGER_SELECTOR_ADD_BUTTON);
        managers.setClearEnsureDebugId(DebugIds.FILTER.MANAGER_SELECTOR_CLEAR_BUTTON);
    }

    private void onFilterChanged() {
        activity.onFilterChanged();
    }

    @Inject
    @UiField
    Lang lang;

    @UiField
    HTMLPanel body;

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
    CaseTagMultiSelector tags;

    @Inject
    @UiField(provided = true)
    ImportanceBtnGroupMulti importance;

    @Inject
    @UiField(provided = true)
    IssueStatesOptionList state;

    private AbstractIssueFilterParamActivity activity = null;

    interface IssueFilterUiBinder extends UiBinder<HTMLPanel, ResolutionTimeReportView> {
    }

    private static IssueFilterUiBinder ourUiBinder = GWT.create( IssueFilterUiBinder.class );
}
