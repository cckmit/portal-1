package ru.protei.portal.ui.region.client.view.filter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;
import ru.protei.portal.core.model.struct.DistrictInfo;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.cleanablesearchbox.CleanableSearchBox;
import ru.protei.portal.ui.common.client.widget.selector.district.DistrictBtnGroupMulti;
import ru.protei.portal.ui.region.client.activity.filter.AbstractRegionFilterActivity;
import ru.protei.portal.ui.region.client.activity.filter.AbstractRegionFilterView;

import java.util.HashSet;
import java.util.Set;

/**
 * Представление фильтра регионов
 */
public class RegionFilterView extends Composite implements AbstractRegionFilterView {
    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    @Override
    public void setActivity( AbstractRegionFilterActivity activity ) {
        this.activity = activity;
    }

    @Override
    public HasValue<String> searchPattern() {
        return search;
    }

    @Override
    public HasValue<Set<DistrictInfo>> districts() {
        return districts;
    }

    @Override
    public void resetFilter() {
        search.setValue( "" );
        districts.setValue( new HashSet<>() );
    }

    @UiHandler( "resetBtn" )
    public void onResetClicked ( ClickEvent event ) {
        if ( activity != null ) {
            resetFilter();
            activity.onFilterChanged();
        }
    }

    @UiHandler( "districts" )
    public void onDistrictSelected( ValueChangeEvent<Set<DistrictInfo>> event ) {
        if ( activity != null ) {
            activity.onFilterChanged();
        }
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

    @UiField
    CleanableSearchBox search;

    @UiField
    Button resetBtn;

    @Inject
    @UiField
    Lang lang;

    @Inject
    @UiField( provided = true )
    DistrictBtnGroupMulti districts;

    AbstractRegionFilterActivity activity;

    private static RegionFilterView.RegionFilterViewUiBinder ourUiBinder = GWT.create( RegionFilterView.RegionFilterViewUiBinder.class );
    interface RegionFilterViewUiBinder extends UiBinder<HTMLPanel, RegionFilterView > {}

}