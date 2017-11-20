package ru.protei.portal.ui.common.client.widget.selector.company;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.popup.SelectorPopup;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Селектор списка компаний
 */
public class CompanySelector extends ButtonSelector< EntityOption > implements ModelSelector <EntityOption> {

    @Inject
    public void init( CompanyModel companyModel ) {
        companyModel.subscribe(this, categories);

        setSearchEnabled( true );
        setSearchAutoFocus( true );

        setDisplayOptionCreator( value -> new DisplayOption( value == null ? defaultValue : value.getDisplayText() ) );
    }

    @Override
    protected void showPopup( IsWidget relative ) {
        popup = popupProvider.get();
        popup.setSearchVisible( true );
        popup.setSearchAutoFocus( true );

        if ( vcHandler != null ) {
            vcHandler.removeHandler();
        }

        vcHandler = popup.addValueChangeHandler( valueChangeEvent -> fillFilteredItems( options.stream()
                .filter( op -> op.getDisplayText().contains( popup.search.getText() ) )
                .collect( Collectors.toList() )
        ) );

        popup.getChildContainer().clear();
        popup.showNear( relative );

        fillFilteredItems( options );
    }

    private void fillFilteredItems( List<EntityOption> options ) {
        StringBuilder result = new StringBuilder();

        Map< String, EntityOption > optionMap = new HashMap<>();
        String prefixId = getClass().getName()+":"+( new Date().getTime())+":";

        if ( defaultValue != null ) {
            result
                    .append( "<li><a href='#'><span id='" )
                    .append( prefixId+"null" )
                    .append( "'>" )
                    .append( defaultValue )
                    .append( "</span><link rel=\"icon\"/></a></li>" );
            optionMap.put( prefixId, null );
        }

        options.forEach( item -> {
            String id = prefixId+item.getId();
            result
                    .append( "<li><a href='#'><span id='" )
                    .append( id ).append( "'>" )
                    .append( item.getDisplayText() )
                    .append( "</span></a></li>" );
            optionMap.put( id, item );
        } );

        popup.childContainer.getElement().setInnerHTML( result.toString() );

        if ( regHandler != null ) {
            regHandler.removeHandler();
        }

        regHandler = popup.childContainer.addDomHandler( clickEvent -> {
            clickEvent.preventDefault();

            Element handledElement = clickEvent.getNativeEvent().getEventTarget().cast();
            Element target = handledElement;
            if ( handledElement.hasTagName( "li" ) || handledElement.hasTagName( "a" )) {
                target = handledElement.getElementsByTagName( "span" ).getItem( 0 );
            }
            String id = target.getAttribute( "id" );

            EntityOption option = optionMap.get( id );
            setValue( option, true );
            popup.hide();
        }, ClickEvent.getType() );
    }

    public void fillOptions( List< EntityOption > options ) {
        this.options = options;
        clearOptions();
    }

    public void setDefaultValue( String value ) {
        this.defaultValue = value;
    }

    public void setCategories(List<En_CompanyCategory> categories) {
        this.categories = categories;
    }

    @Inject
    private Provider<SelectorPopup> popupProvider;

    private String defaultValue = null;

    private List< EntityOption > options;
    private HandlerRegistration regHandler;
    private HandlerRegistration vcHandler;

    private List<En_CompanyCategory> categories = null;
}
