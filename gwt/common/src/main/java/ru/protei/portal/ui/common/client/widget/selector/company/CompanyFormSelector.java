package ru.protei.portal.ui.common.client.widget.selector.company;

import com.google.inject.Inject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.widget.form.FormPopupSingleSelector;

/**
 * Селектор списка компаний
 */
public class CompanyFormSelector extends FormPopupSingleSelector<EntityOption> {

    @Inject
    public void init( CompanyModel companyModel ) {
        setAsyncModel( companyModel );
        setItemRenderer( option -> option == null ? defaultValue : option.getDisplayText() );
    }


}
