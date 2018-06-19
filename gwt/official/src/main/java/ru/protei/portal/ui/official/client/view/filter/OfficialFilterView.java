package ru.protei.portal.ui.official.client.view.filter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.client.view.input.range.RangePicker;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.cleanablesearchbox.CleanableSearchBox;
import ru.protei.portal.ui.common.client.widget.selector.product.ProductButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.region.RegionButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.ModuleType;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.SortFieldSelector;
import ru.protei.portal.ui.official.client.activity.filter.AbstractOfficialFilterActivity;
import ru.protei.portal.ui.official.client.activity.filter.AbstractOfficialFilterView;

/**
 * Представление фильтра должностных лиц
 */
public class OfficialFilterView extends Composite implements AbstractOfficialFilterView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        sortField.setType( ModuleType.OFFICIAL );
        sortDir.setValue( false );
        product.setDefaultValue( lang.selectIssueProduct() );
        region.setDefaultValue( lang.selectOfficialRegion());
        dateRange.setPlaceholder( lang.selectDate() );
    }


    @Override
    public void setActivity(AbstractOfficialFilterActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasValue<String> searchPattern() { return search; }

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
    public HasValue<ProductShortView> product() {
        return product;
    }

    @Override
    public HasValue<EntityOption> region() {
        return region;
    }

    @Override
    public void resetFilter() {
        search.setValue("");
        dateRange.setValue(null);
        product.setValue(null);
        region.setValue(null);
        sortField.setValue(En_SortField.creation_date);
        sortDir.setValue(false);
    }

    @UiHandler( "search" )
    public void onSearchChanged( ValueChangeEvent<String> event ) {
        timer.cancel();
        timer.schedule( 300 );
    }

    Timer timer = new Timer() {
        @Override
        public void run() {
            if ( activity != null ) {
                activity.onFilterChanged();
            }
        }
    };

    @UiHandler("dateRange")
    public void onDateRangeChanged(ValueChangeEvent<DateInterval> event) {
        if (activity != null) {
            activity.onFilterChanged();
        }
    }

    @UiHandler("product")
    public void onProductChanged(ValueChangeEvent<ProductShortView> event) {
        if (activity != null) {
            activity.onFilterChanged();
        }
    }

    @UiHandler("region")
    public void onRegionChanged(ValueChangeEvent<EntityOption> event) {
        if (activity != null) {
            activity.onFilterChanged();
        }
    }

    @UiHandler("sortField")
    public void onSortFieldChanged(ValueChangeEvent<En_SortField> event) {
        if (activity != null) {
            activity.onFilterChanged();
        }
    }

    @UiHandler("sortDir")
    public void onSortDirClicked(ClickEvent event) {
        if (activity != null) {
            activity.onFilterChanged();
        }
    }

    @UiHandler("resetBtn")
    public void onResetButtonChanged(ClickEvent event) {
        if (activity != null) {
            resetFilter();
            activity.onFilterChanged();
        }
    }

    @Inject
    @UiField
    Lang lang;

    @UiField
    CleanableSearchBox search;

    @Inject
    @UiField (provided = true)
    RangePicker dateRange;

    @Inject
    @UiField (provided = true)
    ProductButtonSelector product;

    @Inject
    @UiField (provided = true)
    RegionButtonSelector region;

    @Inject
    @UiField (provided = true)
    SortFieldSelector sortField;

    @UiField
    ToggleButton sortDir;

    @UiField
    Button resetBtn;

    private AbstractOfficialFilterActivity activity;

    interface OfficialFilterViewUiBinder extends UiBinder<HTMLPanel, OfficialFilterView > {}
    private static OfficialFilterView.OfficialFilterViewUiBinder ourUiBinder = GWT.create( OfficialFilterView.OfficialFilterViewUiBinder.class );

}
