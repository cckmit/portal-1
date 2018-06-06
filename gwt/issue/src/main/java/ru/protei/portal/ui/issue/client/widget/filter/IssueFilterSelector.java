package ru.protei.portal.ui.issue.client.widget.filter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.inject.Inject;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

    public class IssueFilterSelector extends ButtonSelector< CaseFilterShortView > implements ModelSelector< CaseFilterShortView > {

    @Inject
    public void init( IssueFilterModel model ) {

        this.model = model;
        model.subscribe( this );
        setSearchEnabled( true );
        setSearchAutoFocus( true );
        setDisplayOptionCreator( value -> new DisplayOption( value == null ? defaultValue : value.getName() ) );
    }

    @Override
    public void onBtnClick( ClickEvent event ) {
        super.onBtnClick( event );
        model.requestFilters( this );
    }

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

    private String defaultValue = null;
    private IssueFilterModel model;
}