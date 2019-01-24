package ru.protei.portal.ui.common.client.widget.selector.company;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.query.CompanyQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.Arrays;
import java.util.List;

/**
 * Селектор списка родительских компаний
 */
public class CompanyParentSelector extends ButtonSelector<EntityOption> implements SelectorWithModel<EntityOption> {

    @Inject
    public void init( CompanyParentModel model ) {
        setSelectorModel( model );
        setSearchEnabled( true );
        setSearchAutoFocus( true );

        setDisplayOptionCreator( value -> new DisplayOption( value == null ? defaultValue : value.getDisplayText() ) );
    }

    public void fillOptions( List<EntityOption> options ) {
        clearOptions();
        if (defaultValue != null) {
            options.add( 0, new EntityOption( defaultValue, null ) );
        }
        for ( EntityOption option : options ) {
            addOption( option );
        }
    }

    public void setDefaultValue( String value ) {
        this.defaultValue = value;
    }

    protected String defaultValue = null;
}
