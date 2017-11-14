package ru.protei.portal.ui.common.client.widget.selector.company;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
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
        this.model = companyModel;
        model.subscribe(this, categories);

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
        vcHandler = popup.addValueChangeHandler( new ValueChangeHandler< String >() {
            @Override
            public void onValueChange( ValueChangeEvent< String > valueChangeEvent ) {
                fillFilterdItems( options.stream().filter(
                        (op)->op.getDisplayText().contains( popup.search.getText() ) ).collect( Collectors.toList())
                );
            }
        } );

        popup.getChildContainer().clear();
        popup.showNear( relative );

        fillFilterdItems( options );
    }

    private void fillFilterdItems( List<EntityOption> options ) {
        StringBuilder result = new StringBuilder();

        final Map< String, EntityOption > optionMap = new HashMap< String, EntityOption >();

        final String prefixId = getClass().getName()+":"+( new Date().getTime())+":";
        String itemid = prefixId+"null";
        result.append( "<li><a href='#'><span id='"+itemid+"'>" + "не выбрано" + "</span><link rel=\"icon\"/></a></li>" );
        optionMap.put( itemid, null );

        options.forEach( (item)->{
            String id = prefixId+item.getId();
            result.append( "<li><a href='#'><span id='"+id+"'>" + item.getDisplayText() + "</span><link rel=\"icon\"/></a></li>" );
            optionMap.put( id, item );
        } );

        popup.childContainer.getElement().setInnerHTML( result.toString() );

        if ( regHandler != null ) {
            regHandler.removeHandler();
        }
        regHandler = popup.childContainer.addDomHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent clickEvent ) {
                clickEvent.preventDefault();

                Element span = clickEvent.getNativeEvent().getEventTarget().cast();
                String id = span.getAttribute( "id" );
                GWT.log( id+":"+span.getInnerText() );

                setValue( optionMap.get( id ), true );
                popup.hide();
            }
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

    private List<En_CompanyCategory> categories = Arrays.asList(
            En_CompanyCategory.CUSTOMER, En_CompanyCategory.PARTNER,
            En_CompanyCategory.SUBCONTRACTOR);

    private CompanyModel model;
    private String defaultValue = null;

    @Inject
    Provider<SelectorPopup> popupProvider;

    List< EntityOption > options;
    HandlerRegistration regHandler;
    HandlerRegistration vcHandler;
}
