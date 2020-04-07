package ru.protei.portal.ui.project.client.view.filter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_RegionState;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.struct.ProductDirectionInfo;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.cleanablesearchbox.CleanableSearchBox;
import ru.protei.portal.ui.common.client.widget.selector.productdirection.ProductDirectionButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.region.RegionMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.ModuleType;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.SortFieldSelector;
import ru.protei.portal.ui.common.client.widget.selector.state.RegionStateBtnGroupMulti;
import ru.protei.portal.ui.project.client.activity.filter.AbstractProjectFilterActivity;
import ru.protei.portal.ui.project.client.activity.filter.AbstractProjectFilterView;

import java.util.HashSet;
import java.util.Set;

/**
 * Представление фильтра проектов
 */
public class ProjectFilterView extends Composite implements AbstractProjectFilterView {
    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        direction.setDefaultValue(lang.contractSelectDirection());
        sortField.setType( ModuleType.PROJECT );
    }

    @Override
    public void setActivity( AbstractProjectFilterActivity activity ) {
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
    public HasValue<ProductDirectionInfo> direction() {
        return direction;
    }

    @Override
    public HasValue<Boolean> onlyMineProjects() {
        return onlyMineProjects;
    }

    @Override
    public HasValue< Set<EntityOption> > regions() {
        return regions;
    }

    @Override
    public void resetFilter() {
        sortField.setValue( En_SortField.project_name );
        sortDir.setValue( true );
        search.setValue( "" );
        direction.setValue( null );
        states.setValue( new HashSet<>() );
        regions.clearSelector();
        onlyMineProjects.setValue( false );
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

    @UiHandler( "regions" )
    public void onRegionSelected( ValueChangeEvent<Set<EntityOption>> event ) {
        if ( activity != null ) {
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
    @UiField(provided = true)
    RegionMultiSelector regions;

    @Inject
    @UiField( provided = true )
    ProductDirectionButtonSelector direction;

    @UiField
    CheckBox onlyMineProjects;

    AbstractProjectFilterActivity activity;

    private static ProjectFilterView.RegionFilterViewUiBinder ourUiBinder = GWT.create( ProjectFilterView.RegionFilterViewUiBinder.class );
    interface RegionFilterViewUiBinder extends UiBinder<HTMLPanel, ProjectFilterView> {}

}