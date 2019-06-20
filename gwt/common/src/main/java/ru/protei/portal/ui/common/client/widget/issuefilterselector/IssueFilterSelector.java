package ru.protei.portal.ui.common.client.widget.issuefilterselector;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.List;

public class IssueFilterSelector extends ButtonSelector< CaseFilterShortView > implements SelectorWithModel< CaseFilterShortView > {

    @Inject
    public void init( IssueFilterModel model ) {

        this.model = model;
        model.subscribe( this );
        setSearchEnabled( true );
        setSearchAutoFocus( true );
        setDisplayOptionCreator( value -> new DisplayOption( value == null ? defaultValue : value.getName() ) );
    }

/*
    @Override
    public void onBtnClick( ClickEvent event ) {
        super.onBtnClick( event );
        model.requestFilters( this );
    }
*/

    public void changeValueName( CaseFilterShortView value ){

        if (value == null){
            return;
        }
        itemToDisplayOptionModel.get( value ).setName( value.getName() );
        refreshValue();
    }

    public void addDisplayOption( CaseFilterShortView value ){
        if (itemToDisplayOptionModel == null){
            return;
        }

        itemToDisplayOptionModel.put( value, new DisplayOption( value.getName() ) );
    }

    public void updateFilterType( En_CaseFilterType filterType ) {
        this.filterType = filterType;
        if ( model != null ) {
            model.updateFilterType( this, this.filterType );
        }
    }

    public void setDefaultValue( String value ) {
        this.defaultValue = value;
    }

    public void fillOptions( List< CaseFilterShortView > filters ) {
        clearOptions();

        filters.sort((o1, o2) -> HelperFunc.compare(o1.getName(), o2.getName(), false));

        if ( defaultValue != null ) {
            addOption( null );
        }
        filters.forEach( this::addOption );
    }

    private En_CaseFilterType filterType;
    private String defaultValue = null;
    private IssueFilterModel model;
}