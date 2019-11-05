package ru.protei.portal.ui.common.client.widget.selector.company;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.widget.components.client.buttonselector.ButtonPopupSingleSelector;
import ru.protei.portal.ui.common.client.widget.components.client.selector.SelectorItemRenderer;

import java.util.*;
import java.util.logging.Logger;

/**
 * Селектор списка компаний
 */
public class CompanySelector
        extends ButtonPopupSingleSelector< EntityOption >
//        implements SelectorWithModel<EntityOption>
//    , HasValidable
{

    @Inject
    public void init( CompanyModel companyModel ) {
//        model = companyModel;
//        model.subscribe(this, categories);
        setAsyncSelectorModel( companyModel );

        setSearchEnabled( true );
        setSearchAutoFocus( true );
//
        setSelectorItemRenderer( (SelectorItemRenderer<EntityOption>) value -> value == null ? defaultValue : value.getDisplayText() );
    }



    public void setDefaultValue( String value ) {
        this.defaultValue = value;
    }

    protected String defaultValue = null;
    private static final Logger log = Logger.getLogger( CompanySelector.class.getName() );

}
