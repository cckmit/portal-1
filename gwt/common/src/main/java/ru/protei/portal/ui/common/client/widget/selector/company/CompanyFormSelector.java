package ru.protei.portal.ui.common.client.widget.selector.company;

import com.google.inject.Inject;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.widget.components.client.form.FormSelector;

/**
 * Селектор списка компаний
 */
public class CompanyFormSelector
        extends FormSelector< EntityOption >
{

    @Inject
    public void init( CompanyModel companyModel ) {
        setAsyncSelectorModel(companyModel);

        setSearchEnabled( true );
        setSearchAutoFocus( true );
        setPageSize( CrmConstants.DEFAULT_SELECTOR_PAGE_SIZE );

        setSelectorItemRenderer( value -> value == null ? defaultValue : value.getDisplayText() );
    }

    public void setDefaultValue( String value ) {
        this.defaultValue = value;
    }


    protected String defaultValue = null;
}
