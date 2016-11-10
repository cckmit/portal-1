package ru.protei.portal.ui.common.client.widget.selector.company;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.List;

/**
 * Селектор списка компаний
 */
public class CompanySelector extends ButtonSelector< EntityOption > implements ModelSelector <EntityOption> {

    @Inject
    public void init( CompanyModel companyModel ) {
        companyModel.subscribe( this );
        setSearchEnabled( true );
        setSearchAutoFocus( true );
    }

    public void fillOptions( List< EntityOption > options ) {
        clearOptions();

        if ( hasAnyValue ) {
            addOption( lang.company(), null );
        }

        options.forEach(option -> addOption(option.getDisplayText(),option));
    }

    public void setHasAnyValue( boolean hasAnyValue ) {
        this.hasAnyValue = hasAnyValue;
    }

    @Inject
    Lang lang;

    private boolean hasAnyValue = true;

}
