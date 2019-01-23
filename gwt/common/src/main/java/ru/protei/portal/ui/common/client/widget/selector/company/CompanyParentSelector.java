package ru.protei.portal.ui.common.client.widget.selector.company;

import com.google.inject.Inject;
import ru.protei.portal.core.model.query.CompanyQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;

/**
 * Селектор списка родительских компаний
 */
public class CompanyParentSelector extends CompanySelector implements SelectorWithModel<EntityOption> {

    @Inject
    public void init( CompanyModel companyModel ) {
        model = companyModel;

        CompanyQuery companyQuery = model.makeQuery( categories );
        companyQuery.setParentIdIsNull(true);
        model.subscribe(this, companyQuery);
        setSelectorModel(model);

        setSearchEnabled( true );
        setSearchAutoFocus( true );

        setDisplayOptionCreator( value -> new DisplayOption( value == null ? defaultValue : value.getDisplayText() ) );
    }
}
