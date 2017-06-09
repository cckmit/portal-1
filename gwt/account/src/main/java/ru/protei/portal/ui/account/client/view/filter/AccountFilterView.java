package ru.protei.portal.ui.account.client.view.filter;

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
import ru.protei.portal.core.model.dict.En_AuthType;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.ui.account.client.activity.filter.AbstractAccountFilterActivity;
import ru.protei.portal.ui.account.client.activity.filter.AbstractAccountFilterView;
import ru.protei.portal.ui.account.client.widget.type.AuthTypeBtnGroupMulti;
import ru.protei.portal.ui.common.client.common.FixedPositioner;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.SortFieldSelector;

import java.util.HashSet;
import java.util.Set;

/**
 * Абстракция вида фильтра учетных записей
 */
public class AccountFilterView extends Composite implements AbstractAccountFilterView {
    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        search.getElement().setPropertyString( "placeholder", lang.search() );
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        positioner.watch(this, FixedPositioner.NAVBAR_TOP_OFFSET);
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        positioner.ignore(this);
    }

    @Override
    public void setActivity( AbstractAccountFilterActivity activity ) {
        this.activity = activity;
    }

    @Override
    public HasValue< String > searchPattern() {
        return search;
    }

    @Override
    public HasValue< En_SortField > sortField() {
        return sortField;
    }

    @Override
    public HasValue< Boolean > sortDir() {
        return sortDir;
    }

    @Override
    public HasValue< Set< En_AuthType > > types() {
        return types;
    }

    @Override
    public void resetFilter() {
        sortField.setValue( En_SortField.ulogin );
        sortDir.setValue( true );
        search.setText( "" );
        types.setValue( new HashSet<>() );
    }

    @UiHandler( "resetBtn" )
    public void onResetClicked ( ClickEvent event ) {
        if ( activity != null ) {
            resetFilter();
            activity.onFilterChanged();
        }
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

    @UiHandler( "types" )
    public void onTypeSelected( ValueChangeEvent< Set< En_AuthType > > event ) {
        if ( activity != null ) {
            activity.onFilterChanged();
        }
    }

    @UiHandler( "search" )
    public void onKeyUpSearch( KeyUpEvent event ) {
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

    @Inject
    @UiField( provided = true )
    SortFieldSelector sortField;

    @UiField
    ToggleButton sortDir;

    @UiField
    TextBox search;

    @Inject
    @UiField( provided = true )
    AuthTypeBtnGroupMulti types;

    @UiField
    Button resetBtn;

    @Inject
    @UiField
    Lang lang;

    @Inject
    FixedPositioner positioner;
    
    AbstractAccountFilterActivity activity;

    private static AccountFilterViewUiBinder ourUiBinder = GWT.create( AccountFilterViewUiBinder.class );
    interface AccountFilterViewUiBinder extends UiBinder< HTMLPanel, AccountFilterView > {}
}