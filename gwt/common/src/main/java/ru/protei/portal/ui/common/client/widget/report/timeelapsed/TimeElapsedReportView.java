package ru.protei.portal.ui.common.client.widget.report.timeelapsed;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.client.view.input.range.RangePicker;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.issuefilter.AbstractIssueFilterParamActivity;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.util.IssueFilterUtils;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanyMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.product.devunit.DevUnitMultiSelector;

import java.util.Set;

public class TimeElapsedReportView extends Composite implements AbstractTimeElapsedReportView {

    @Inject
    public void init() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        ensureDebugIds();
    }

    @Override
    public void setActivity( AbstractIssueFilterParamActivity activity ) {
        this.activity = activity;
    }

    @Override
    public HasValue<DateInterval> dateRange() {
        return dateRange;
    }

    @Override
    public HasValue<Set<ProductShortView>> products() {
        return products;
    }

    @Override
    public HasValue<Set<EntityOption>> companies() {
        return companies;
    }

    @Override
    public HasValue<Set<PersonShortView>> commentAuthors() {
        return commentAuthors;
    }

    @Override
    public HasVisibility productsVisibility() {
        return products;
    }

    @Override
    public HasVisibility companiesVisibility() {
        return companies;
    }

    @Override
    public void resetFilter() {
        companies.setValue( null );
        products.setValue( null );
        commentAuthors.setValue( null );
        dateRange.setValue( null );

    }

    @UiHandler("dateRange")
    public void onDateRangeChanged( ValueChangeEvent<DateInterval> event ) {
        onFilterChanged();
    }

    @UiHandler("products")
    public void onProductsSelected( ValueChangeEvent<Set<ProductShortView>> event ) {
        onFilterChanged();
    }

    @UiHandler("companies")
    public void onCompaniesSelected( ValueChangeEvent<Set<EntityOption>> event ) {
        if (activity != null) {
            activity.onCompaniesFilterChanged();
        }
    }

    @UiHandler("commentAuthors")
    public void onCommentAuthorsSelected( ValueChangeEvent<Set<PersonShortView>> event ) {
        onFilterChanged();
    }


    private void ensureDebugIds() {
        dateRange.setEnsureDebugId( DebugIds.FILTER.DATE_RANGE_SELECTOR );
        companies.setAddEnsureDebugId( DebugIds.FILTER.COMPANY_SELECTOR_ADD_BUTTON );
        companies.setClearEnsureDebugId( DebugIds.FILTER.COMPANY_SELECTOR_CLEAR_BUTTON );
        products.setAddEnsureDebugId( DebugIds.FILTER.PRODUCT_SELECTOR_ADD_BUTTON );
        products.setClearEnsureDebugId( DebugIds.FILTER.PRODUCT_SELECTOR_CLEAR_BUTTON );

    }

    private void onFilterChanged() {
        if (activity != null) {
            activity.onFilterChanged();
        }
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
    DevUnitMultiSelector products;
    @Inject
    @UiField(provided = true)
    CompanyMultiSelector companies;

    @Inject
    @UiField(provided = true)
    EmployeeMultiSelector commentAuthors;

    private AbstractIssueFilterParamActivity activity = null;

    interface IssueFilterUiBinder extends UiBinder<HTMLPanel, TimeElapsedReportView> {
    }

    private static IssueFilterUiBinder ourUiBinder = GWT.create( IssueFilterUiBinder.class );
}