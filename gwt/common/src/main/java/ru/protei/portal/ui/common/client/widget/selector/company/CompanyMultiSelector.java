package ru.protei.portal.ui.common.client.widget.selector.company;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.components.client.input.InputPopupMultiSelector;
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
//        implements SelectorWithModel< EntityOption >
{

    @Inject
    public void init( CompanyModel model, Lang lang ) {
//        model.subscribe( this, categories );
//        setSelectorModel( (ru.protei.portal.ui.common.client.widget.components.client.selector.SelectorModel) model );
        setAddName( lang.buttonAdd() );
        setClearName( lang.buttonClear() );
        setSelectorItemRenderer( new SelectorItemRenderer<EntityOption>() {
            @Override
            public String getElementName( EntityOption value ) {
                return value == null ? "" : value.getDisplayText();
            }
        } );

        setSearchEnabled( true );
        setPageSize( 30 );
    }

    public void fillOptions( List< EntityOption > options ) {

//        clearOptions();
//        for ( EntityOption option : emptyIfNull( options) ) {
//            addOption(  option.getDisplayText(), option );
//        }
    }


    private static final Logger log = Logger.getLogger( CompanyMultiSelector.class.getName() );
//
//    @Override
//    public void refreshValue() {
//        log.warning( "refreshValue(): Not implemented." );//TODO NotImplemented
//
//    }
//
//    @Override
//    public void clearOptions() {
//        log.warning( "clearOptions(): Not implemented." );//TODO NotImplemented
//
//    }
//
//    @Override
//    public Collection<EntityOption> getValues() {
//        return getValue();
//    }
//
//    @Override
//    public void setSelectorModel( SelectorModel<EntityOption> selectorModel ) {
//        log.warning( "setSelectorModel(): Not implemented." );//TODO NotImplemented
//
//    }


    private List<En_CompanyCategory > categories = Arrays.asList(
            En_CompanyCategory.CUSTOMER,
            En_CompanyCategory.PARTNER,
            En_CompanyCategory.SUBCONTRACTOR,
            En_CompanyCategory.HOME);

}