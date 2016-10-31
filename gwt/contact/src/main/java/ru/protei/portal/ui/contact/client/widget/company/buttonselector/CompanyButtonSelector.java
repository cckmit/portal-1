package ru.protei.portal.ui.contact.client.widget.company.buttonselector;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.models.CompanyModel;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.List;

/**
 * Created by turik on 28.10.16.
 */
public class CompanyButtonSelector extends ButtonSelector< Company > implements ModelSelector < Company > {

    @Inject
    public void init( CompanyModel companyModel ) {
        companyModel.subscribe( this );
        setSearchEnabled( true );
        setSearchAutoFocus( true );
    }

    public void fillOptions( List< Company > companies ) {
        clearOptions();

        if ( hasAnyValue ) {
            addOption( lang.company(), null );
        }
        int recNum = 0;
        for ( Company company : companies ) {
            addOption( company.getCname(), company );
            if ( ++ recNum > 20 )
                break;
        }
    }

    public void setHasAnyValue( boolean hasAnyValue ) {
        this.hasAnyValue = hasAnyValue;
    }

    @Inject
    Lang lang;

    private boolean hasAnyValue = true;

}
