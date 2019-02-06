package ru.protei.portal.ui.common.client.widget.report;

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
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.issuefilter.AbstractIssueFilterParamActivity;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.issuestate.optionlist.IssueStatesOptionList;
import ru.protei.portal.ui.common.client.widget.selector.product.devunit.DevUnitButtonSelector;

import java.util.Set;

public class CaseCompletionTimeReportView extends Composite implements AbstractCaseCompletionTimeReportView {

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
    public HasValue<DateInterval> dateRange() {
        return dateRange;
    }


    @Override
    public HasValue<ProductShortView> products() {
        return products;
    }


    @Override
    public HasValue<Set<En_CaseState>> states() {
        return state;
    }

    @Override
    public void resetFilter() {
        products.setValue( null );
        state.setValue( null );
        dateRange.setValue( null );
    }

    @UiHandler("dateRange")
    public void onDateRangeChanged( ValueChangeEvent<DateInterval> event ) {
        onFilterChanged();
    }

    @UiHandler("products")
    public void onProductsSelected( ValueChangeEvent<ProductShortView> event ) {
        onFilterChanged();
    }

    @UiHandler("state")
    public void onStateSelected( ValueChangeEvent<Set<En_CaseState>> event ) {
        onFilterChanged();
    }

    private void ensureDebugIds() {
        dateRange.setEnsureDebugId( DebugIds.FILTER.DATE_RANGE_SELECTOR );
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
    DevUnitButtonSelector products;

    @Inject
    @UiField(provided = true)
    IssueStatesOptionList state;

    private AbstractIssueFilterParamActivity activity = null;

    interface IssueFilterUiBinder extends UiBinder<HTMLPanel, CaseCompletionTimeReportView> {
    }

    private static IssueFilterUiBinder ourUiBinder = GWT.create( IssueFilterUiBinder.class );
}
