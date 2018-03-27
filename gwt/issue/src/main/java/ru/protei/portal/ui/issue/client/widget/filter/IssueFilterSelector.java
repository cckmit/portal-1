package ru.protei.portal.ui.issue.client.widget.filter;

import com.google.inject.Inject;
import ru.protei.portal.core.model.view.IssueFilterShortView;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.List;

public class IssueFilterSelector extends ButtonSelector< IssueFilterShortView > implements ModelSelector< IssueFilterShortView > {

    @Inject
    public void init( IssueFilterModel productModel ) {

        productModel.subscribe( this );
        setSearchEnabled( true );
        setSearchAutoFocus( true );
        setDisplayOptionCreator( value -> new DisplayOption( value == null ? defaultValue : value.getName() ) );
    }

    public void setDefaultValue( String value ) {
        this.defaultValue = value;
    }

    public void fillOptions( List< IssueFilterShortView > filters ) {
        clearOptions();

        if ( defaultValue != null ) {
            addOption( null );
            setValue( null );
        }
        filters.forEach( this::addOption );
    }

    private String defaultValue = null;
}