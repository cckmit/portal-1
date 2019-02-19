package ru.protei.portal.ui.common.client.widget.report.timeelapsed;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.client.view.input.range.RangePicker;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.issuefilter.AbstractIssueFilterParamActivity;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.util.IssueFilterUtils;
import ru.protei.portal.ui.common.client.widget.cleanablesearchbox.CleanableSearchBox;
import ru.protei.portal.ui.common.client.widget.optionlist.item.OptionItem;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanyMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.product.devunit.DevUnitMultiSelector;

import java.util.Set;

import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.REQUIRED;

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
    public AbstractIssueFilterParamActivity getActivity() {
        return activity;
    }

    @Override
    public HasValue<String> searchPattern() {
        return search;
    }

    @Override
    public HasValue<Boolean> searchByComments() {
        return searchByComments;
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
    public HasValue<Set<PersonShortView>> managers() {
        return managers;
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
    public HasVisibility managersVisibility() {
        return managers;
    }

    @Override
    public HasVisibility commentAuthorsVisibility() {
        return commentAuthors;
    }

    @Override
    public HasVisibility searchByCommentsVisibility() {
        return searchByCommentsContainer;
    }

    @Override
    public void resetFilter() {
        companies.setValue( null );
        products.setValue( null );
        managers.setValue( null );
        commentAuthors.setValue( null );
        dateRange.setValue( null );
        search.setValue( "" );

        searchByComments.setValue( false );
        toggleMsgSearchThreshold();
    }

    @Override
    public void fillFilterFields( CaseQuery caseQuery ) {
        searchPattern().setValue( caseQuery.getSearchString() );
        searchByComments().setValue( caseQuery.isSearchStringAtComments() );
        dateRange().setValue( new DateInterval( caseQuery.getFrom(), caseQuery.getTo() ) );
        companies().setValue( IssueFilterUtils.getCompanies( caseQuery.getCompanyIds() ) );
        managers().setValue( IssueFilterUtils.getPersons( caseQuery.getManagerIds() ) );
        products().setValue( IssueFilterUtils.getProducts( caseQuery.getProductIds() ) );
        commentAuthors().setValue( IssueFilterUtils.getPersons( caseQuery.getCommentAuthorIds() ) );
    }

    @Override
    public void toggleMsgSearchThreshold() {
        if (searchByComments.getValue()) {
            int actualLength = search.getValue().length();
            if (actualLength >= CrmConstants.Issue.MIN_LENGTH_FOR_SEARCH_BY_COMMENTS) {
                searchByCommentsWarning.setVisible( false );
            } else {
                searchByCommentsWarning.setText( lang.searchByCommentsUnavailable( CrmConstants.Issue.MIN_LENGTH_FOR_SEARCH_BY_COMMENTS ) );
                searchByCommentsWarning.setVisible( true );
            }
        } else if (searchByCommentsWarning.isVisible()) {
            searchByCommentsWarning.setVisible( false );
        }
    }

    @Override
    public void setCompaniesErrorStyle( boolean hasError ) {
        if (hasError) {
            companies.addStyleName( REQUIRED );
        } else {
            companies.removeStyleName( REQUIRED );
        }
    }

    @Override
    public void setProductsErrorStyle( boolean hasError ) {
        if (hasError) {
            products.addStyleName( REQUIRED );
        } else {
            products.removeStyleName( REQUIRED );
        }
    }

    @Override
    public void setManagersErrorStyle( boolean hasError ) {
        if (hasError) {
            managers.addStyleName( REQUIRED );
        } else {
            managers.removeStyleName( REQUIRED );
        }
    }

    @Override
    public void addBodyStyles( String styles ) {
        body.addStyleName( styles );
    }



    @UiHandler("search")
    public void onSearchChanged( ValueChangeEvent<String> event ) {
        startFilterChangedTimer();
    }

    @UiHandler("searchByComments")
    public void onSearchByCommentsChanged( ValueChangeEvent<Boolean> event ) {
        toggleMsgSearchThreshold();
        onFilterChanged();
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

    @UiHandler("managers")
    public void onManagersSelected( ValueChangeEvent<Set<PersonShortView>> event ) {
        onFilterChanged();
    }

    @UiHandler("commentAuthors")
    public void onCommentAuthorsSelected( ValueChangeEvent<Set<PersonShortView>> event ) {
        onFilterChanged();
    }


    private void ensureDebugIds() {

        search.setEnsureDebugIdTextBox( DebugIds.FILTER.SEARCH_INPUT );
        search.setEnsureDebugIdAction( DebugIds.FILTER.SEARCH_CLEAR_BUTTON );
        searchByComments.setEnsureDebugId( DebugIds.FILTER.SEARCH_BY_COMMENTS_TOGGLE );
        dateRange.setEnsureDebugId( DebugIds.FILTER.DATE_RANGE_SELECTOR );
        companies.setAddEnsureDebugId( DebugIds.FILTER.COMPANY_SELECTOR_ADD_BUTTON );
        companies.setClearEnsureDebugId( DebugIds.FILTER.COMPANY_SELECTOR_CLEAR_BUTTON );
        products.setAddEnsureDebugId( DebugIds.FILTER.PRODUCT_SELECTOR_ADD_BUTTON );
        products.setClearEnsureDebugId( DebugIds.FILTER.PRODUCT_SELECTOR_CLEAR_BUTTON );
        managers.setAddEnsureDebugId( DebugIds.FILTER.MANAGER_SELECTOR_ADD_BUTTON );
        managers.setClearEnsureDebugId( DebugIds.FILTER.MANAGER_SELECTOR_CLEAR_BUTTON );

    }

    private void onFilterChanged() {
        if (activity != null) {
            activity.onFilterChanged();
        }
    }

    private void startFilterChangedTimer() {
        if (timer == null) {
            timer = new Timer() {
                @Override
                public void run() {
                    toggleMsgSearchThreshold();
                    onFilterChanged();
                }
            };
        } else {
            timer.cancel();
        }
        timer.schedule( 300 );
    }

    @Inject
    @UiField
    Lang lang;



    @UiField
    HTMLPanel body;
    @UiField
    CleanableSearchBox search;
    @UiField
    HTMLPanel searchByCommentsContainer;
    @UiField
    Label searchByCommentsWarning;
    @UiField
    OptionItem searchByComments;
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
    EmployeeMultiSelector managers;
    @Inject
    @UiField(provided = true)
    EmployeeMultiSelector commentAuthors;


    private Timer timer = null;
    private AbstractIssueFilterParamActivity activity = null;

    interface IssueFilterUiBinder extends UiBinder<HTMLPanel, TimeElapsedReportView> {
    }

    private static IssueFilterUiBinder ourUiBinder = GWT.create( IssueFilterUiBinder.class );
}