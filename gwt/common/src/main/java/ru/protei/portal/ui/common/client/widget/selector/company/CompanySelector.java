package ru.protei.portal.ui.common.client.widget.selector.company;

import com.google.inject.Inject;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.widget.components.client.buttonselector.ButtonPopupSingleSelector;

import java.util.logging.Logger;

/**
 * Селектор списка компаний
 */
public class CompanySelector
        extends ButtonPopupSingleSelector< EntityOption >
{

    @Inject
    public void init( CompanyModel companyModel ) {
        setAsyncSelectorModel( companyModel );
        setSelectorItemRenderer( value -> value == null ? defaultValue : value.getDisplayText() );
    }

    public void setDefaultValue( String value ) {
        this.defaultValue = value;
    }

    protected String defaultValue = null;
    private static final Logger log = Logger.getLogger( CompanySelector.class.getName() );

}
