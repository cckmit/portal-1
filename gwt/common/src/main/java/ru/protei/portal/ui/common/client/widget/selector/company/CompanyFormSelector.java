package ru.protei.portal.ui.common.client.widget.selector.company;

import com.google.inject.Inject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.widget.components.client.form.FormSingleSelector;

/**
 * Селектор списка компаний
 */
public class CompanyFormSelector extends FormSingleSelector<EntityOption> {

    @Inject
    public void init( CompanyModel companyModel ) {
        setAsyncSelectorModel( companyModel );
        setSelectorItemRenderer( option -> option == null ? defaultValue : option.getDisplayText() );
    }

    public void setDefaultValue( String value ) {
        this.defaultValue = value;
    }

    protected String defaultValue = null;
}
