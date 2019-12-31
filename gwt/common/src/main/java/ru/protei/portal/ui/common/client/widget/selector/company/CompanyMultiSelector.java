package ru.protei.portal.ui.common.client.widget.selector.company;

import com.google.inject.Inject;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.components.client.input.InputPopupMultiSelector;

/**
 * Мультиселектор компаний
 */
public class CompanyMultiSelector extends InputPopupMultiSelector<EntityOption> {

    @Inject
    public void init( CompanyModel model, Lang lang ) {
        setAsyncSelectorModel( model );
        setSelectorItemRenderer( model );
        setAddName( lang.buttonAdd() );
        setClearName( lang.buttonClear() );
    }

}