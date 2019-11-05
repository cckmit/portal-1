package ru.protei.portal.ui.common.client.widget.selector.company;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.widget.form.FormSelector;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.client.widget.selector.popup.SelectorPopup;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Селектор списка компаний
 */
public class CompanyFormSelector extends FormSelector< EntityOption > implements SelectorWithModel<EntityOption> {

    @Inject
    public void init( CompanyModel companyModel ) {
        model = companyModel;
        model.subscribe(this, categories);
        setSelectorModel(model);

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

        vcHandler = popup.addValueChangeHandler( valueChangeEvent -> fillFilteredItems( filter( options ) ) );

        popup.getChildContainer().clear();
        popup.showNear( relative );

        fillFilteredItems( filter(options) );
    }

    @Override
    public void clearOptions() {
        super.clearOptions();
        this.options = null;
    }

    private void fillFilteredItems(List<EntityOption> options ) {
        StringBuilder result = new StringBuilder();

        Map< String, EntityOption > optionMap = new HashMap<>();
        String companyId = getClass().getName()+":"+( new Date().getTime())+":";

        if ( defaultValue != null && hasNullValue ) {
            result
                    .append( "<li><a href='#'><span id='" )
                    .append( companyId+"null" )
                    .append( "'>" )
                    .append( defaultValue )
                    .append( "</span><link rel=\"icon\"/></a></li>" );
            optionMap.put( companyId, null );
        }

        options.forEach( item -> {
            String id = companyId+item.getId();
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
        clearOptions();
        this.options = options;
        if (deferedApplyValueIfOneOption) {
            applyValueIfOneOption();
        }
    }

    public void setDefaultValue( String value ) {
        this.defaultValue = value;
    }

    public void setCategories( List< En_CompanyCategory > categories ) {
        this.categories = categories;
        if ( model != null ) {
            model.showOnlyParentCompanies( this, this.categories );
        }
    }

    public void showOnlyParentCompanies( boolean isOnlyParentCompanies ) {
        if (model != null) {
            model.updateQuery( this, categories, isOnlyParentCompanies );
        }
    }

    public void showDeprecated(Boolean isShowDeprecated) {
        if (model != null) {
            model.showOnlyParentCompanies(this, isShowDeprecated);
        }
    }

    public void applyValueIfOneOption() {
        if ( options == null ) {
            deferedApplyValueIfOneOption = true;
            return;
        }

        deferedApplyValueIfOneOption = false;
        if (options.size() == 1) {
            setValue(options.get(0), true);
        } else {
            setValue(null, true);
        }
    }

    private List<EntityOption> filter( List<EntityOption> options ) {
        return options.stream()
                .filter( this::applyPredicate )
                .collect( Collectors.toList() );
    }

    private boolean applyPredicate(EntityOption op) {
        if (filter != null && !filter.isDisplayed(op)) return false;
        return op.getDisplayText().toLowerCase().contains(popup.search.getValue().toLowerCase());
    }

    @Inject
    private Provider<SelectorPopup> popupProvider;

    private List< EntityOption > options;
    private HandlerRegistration regHandler;
    private HandlerRegistration vcHandler;

    protected List<En_CompanyCategory> categories = Arrays.asList(
            En_CompanyCategory.CUSTOMER,
            En_CompanyCategory.PARTNER,
            En_CompanyCategory.SUBCONTRACTOR,
            En_CompanyCategory.HOME);

    protected CompanyModel model;

    protected String defaultValue = null;
    private boolean deferedApplyValueIfOneOption = false;
}
