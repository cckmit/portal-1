package ru.protei.portal.ui.ipreservation.client.view.filter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_RegionState;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.struct.DistrictInfo;
import ru.protei.portal.core.model.struct.ProductDirectionInfo;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.cleanablesearchbox.CleanableSearchBox;
import ru.protei.portal.ui.common.client.widget.selector.district.DistrictBtnGroupMulti;
import ru.protei.portal.ui.common.client.widget.selector.productdirection.ProductDirectionButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.ModuleType;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.SortFieldSelector;
import ru.protei.portal.ui.common.client.widget.selector.state.RegionStateBtnGroupMulti;
import ru.protei.portal.ui.ipreservation.client.activity.filter.AbstractIpReservationFilterActivity;
import ru.protei.portal.ui.ipreservation.client.activity.filter.AbstractIpReservationFilterView;
w;

import java.util.HashSet;
import java.util.Set;

/**
 * Представление фильтра проектов
 */
public class IpReservationFilterView extends Composite implements AbstractIpReservationFilterView {
    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        direction.setDefaultValue(lang.contractSelectDirection());
        sortField.setType( ModuleType.PROJECT );
    }

    @Override
    public void setActivity( AbstractIpReservationFilterActivity activity ) {
        this.activity = activity;
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
    public HasValue<Set<En_RegionState>> states() {
        return states;
    }

    @Override
    public HasValue<Set<DistrictInfo>> districts() {
        return districts;
    }

    @Override
    public HasValue<ProductDirectionInfo> direction() {
        return direction;
    }

    @Override
    public HasValue<Boolean> onlyMineProjects() {
        return onlyMineProjects;
    }

    @Override
    public void resetFilter() {
        sortField.setValue( En_SortField.project_name );
        sortDir.setValue( true );
        search.setValue( "" );
        direction.setValue( null );
        districts.setValue( new HashSet<>() );
        states.setValue( new HashSet<>() );
        onlyMineProjects.setValue( true );
    }

    @UiHandler( "resetBtn" )
    public void onResetClicked ( ClickEvent event ) {
        if ( activity != null ) {
            resetFilter();
            activity.onFilterChanged();
        }
    }

    @UiHandler( "sortField" )
    public void onSortFieldSelected( ValueChangeEvent<En_SortField> event ) {
        if ( activity != null ) {
            activity.onFilterChanged();
        }
    }

    @UiHandler( "states" )
    public void onStateSelected( ValueChangeEvent<Set<En_RegionState>> event ) {
        if ( activity != null ) {
            activity.onFilterChanged();
        }
    }

    @UiHandler( "districts" )
    public void onDistrictSelected( ValueChangeEvent<Set<DistrictInfo>> event ) {
        if ( activity != null ) {
            activity.onFilterChanged();
        }
    }

    @UiHandler( "direction" )
    public void onDirectionSelected( ValueChangeEvent<ProductDirectionInfo> event ) {
        if ( activity != null ) {
            activity.onFilterChanged();
        }
    }

    @UiHandler("sortDir")
    public void onSortDirClicked( ClickEvent event ) {
        if ( activity != null ) {
            activity.onFilterChanged();
        }
    }

    @UiHandler( "search" )
    public void onSearchChanged( ValueChangeEvent<String> event ) {
        timer.cancel();
        timer.schedule( 300 );
    }

    @UiHandler( "onlyMineProjects" )
    public void onOnlyMineProjectsChanged( ValueChangeEvent<Boolean> event ) {
        if (activity != null) {
            activity.onFilterChanged();
        }
    }

    Timer timer = new Timer() {
        @Override
        public void run() {
            if ( activity != null ) {
                activity.onFilterChanged();
            }
        }
    };

    @Inject
    @UiField( provided = true )
    SortFieldSelector sortField;

    @UiField
    ToggleButton sortDir;

    @UiField
    CleanableSearchBox search;

    @UiField
    Button resetBtn;

    @Inject
    @UiField
    Lang lang;

    @Inject
    @UiField( provided = true )
    RegionStateBtnGroupMulti states;

    @Inject
    @UiField( provided = true )
    DistrictBtnGroupMulti districts;

    @Inject
    @UiField( provided = true )
    ProductDirectionButtonSelector direction;

    @UiField
    CheckBox onlyMineProjects;

    AbstractProjectFilterActivity activity;

    private static ProjectFilterView.RegionFilterViewUiBinder ourUiBinder = GWT.create( ProjectFilterView.RegionFilterViewUiBinder.class );
    interface RegionFilterViewUiBinder extends UiBinder<HTMLPanel, ProjectFilterView> {}

}