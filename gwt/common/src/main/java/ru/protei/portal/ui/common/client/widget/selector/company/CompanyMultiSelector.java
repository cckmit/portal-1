package ru.protei.portal.ui.common.client.widget.selector.company;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.components.client.input.InputPopupMultiSelector;
import ru.protei.portal.ui.common.client.widget.components.client.selector.AsyncSelectorModel;
import ru.protei.portal.ui.common.client.widget.components.client.selector.SelectorItemRenderer;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorModel;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.client.widget.selector.input.MultipleInputSelector;
import ru.protei.portal.ui.common.client.widget.components.client.buttonselector.ButtonPopupMultiSelector;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import static ru.protei.portal.core.model.helper.CollectionUtils.emptyIfNull;

/**
 * Мультиселектор компаний
 */
public class CompanyMultiSelector
        extends InputPopupMultiSelector< EntityOption >
{

    @Inject
    public void init( CompanyModel model, Lang lang ) {
        setAsyncSelectorModel( model );
        setSelectorItemRenderer( model );
        setAddName( lang.buttonAdd() );
        setClearName( lang.buttonClear() );

        setSearchEnabled( true );
        setPageSize( 10 );
    }

}