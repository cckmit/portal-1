package ru.protei.portal.ui.common.client.widget.selector.company;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.query.CompanyQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.Arrays;
import java.util.List;

/**
 * Селектор списка родительских компаний
 */
public class CompanyParentSelector extends ButtonSelector<EntityOption> implements SelectorWithModel<EntityOption> {

    @Inject
    public void init( CompanyParentModel model ) {

//        CompanyQuery companyQuery = model.makeQuery( categories );
//        companyQuery.setParentIdIsNull( true );
//        model.subscribe( this, companyQuery );
        setSelectorModel( model );

        setSearchEnabled( true );
        setSearchAutoFocus( true );

        setDisplayOptionCreator( value -> new DisplayOption( value == null ? defaultValue : value.getDisplayText() ) );
    }

    public void fillOptions( List<EntityOption> options ) {
        clearOptions();
        if (defaultValue != null) {
            options.add( 0, new EntityOption( defaultValue, null ) );
        }
//        this.options = options;
        super.fillOptions(options);
    }
//
//    @Override
//    protected void showPopup( IsWidget relative ) {
//        if ( defaultValue != null ) {
//            addOption( new EntityOption( defaultValue, null ) );
//        }
//        for ( EntityOption option : emptyIfNull( options) ) {
//            addOption( option );
//        }
//
//        options = null; // обнулить, так как clear() очистит для всех экземпляров селектора
//        super.showPopup( relative );
//    }

    public void setDefaultValue( String value ) {
        this.defaultValue = value;
    }

    protected String defaultValue = null;



    private List<EntityOption> options;
}
