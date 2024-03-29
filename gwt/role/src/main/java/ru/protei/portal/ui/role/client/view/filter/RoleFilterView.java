package ru.protei.portal.ui.role.client.view.filter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.cleanablesearchbox.CleanableSearchBox;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.SortFieldSelector;
import ru.protei.portal.ui.role.client.activity.filter.AbstractRoleFilterActivity;
import ru.protei.portal.ui.role.client.activity.filter.AbstractRoleFilterView;

/**
 * Представление фильтра роли
 */
public class RoleFilterView extends Composite implements AbstractRoleFilterView {
    
    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    @Override
    public void setActivity( AbstractRoleFilterActivity activity ) {
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
    public void resetFilter() {
        search.setValue( "" );
        sortField.setValue( En_SortField.role_name );
        sortDir.setValue( true );
    }

    @UiHandler( "resetBtn" )
    public void onResetClicked ( ClickEvent event ) {
        if ( activity != null ) {
            resetFilter();
            activity.onFilterChanged();
        }
    }

    @UiHandler( "search" )
    public void onSearchChanged( ValueChangeEvent<String> event ) {
        timer.cancel();
        timer.schedule( 300 );
    }

    @UiHandler( "sortField" )
    public void onSortFieldSelected( ValueChangeEvent< En_SortField > event ) {
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

    @UiField
    CleanableSearchBox search;

    @UiField
    Button resetBtn;

    @Inject
    @UiField( provided = true )
    SortFieldSelector sortField;

    @UiField
    ToggleButton sortDir;
    @Inject
    @UiField
    Lang lang;

    Timer timer = new Timer() {
        @Override
        public void run() {
            if ( activity != null ) {
                activity.onFilterChanged();
            }
        }
    };

    AbstractRoleFilterActivity activity;

    private static RoleFilterView.RoleFilterViewUiBinder ourUiBinder = GWT.create( RoleFilterViewUiBinder.class );
    interface RoleFilterViewUiBinder extends UiBinder<HTMLPanel, RoleFilterView > {}

}