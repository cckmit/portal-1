package ru.protei.portal.ui.common.client.widget.selector.input;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.protei.portal.ui.common.client.widget.selector.base.MultipleSelector;
import ru.protei.portal.ui.common.client.widget.selector.item.SelectItemView;

import java.util.ArrayList;
import java.util.List;

/**
 * Абстрактный селектор с полем ввода
 */
public class MultipleInputSelector<T> extends MultipleSelector<T> implements HasEnabled {

    public MultipleInputSelector() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        initHandlers();
    }

    public void setHeader( String label ) {
        this.label.getElement().setInnerText( label == null ? "" : label );
    }

    @Override
    public boolean isEnabled() {
        return caretButton.isEnabled();
    }

    @Override
    public void setEnabled( boolean enabled ) {
        if ( enabled ) {
            itemContainer.removeStyleName( "inactive" );
        } else {
            itemContainer.addStyleName( "inactive" );
        }
        caretButton.setEnabled( enabled );
    }

    @UiHandler( { "caretButton" } )
    public void onShowPopupClicked( ClickEvent event ) {
        showPopup( itemContainer );
    }

    public void fillSelectorView( List<String> selectedValues ) {
        itemContainer.clear();
        itemViews.clear();

        for ( String val : selectedValues ) {
            addItem( val );
        }
    }

    public void setAddName( String string ) {
        add.setInnerText( string );
        add.removeClassName( "caret" );
    }

    private void addItem( final String val ) {
        SelectItemView itemView = itemViewProvider.get();
        itemView.setValue( val );

        itemViews.add( itemView );
        itemView.setActivity( itemView1 -> removeItem( itemView1, val ) );
        itemContainer.add( itemView );
    }

    private void removeItem( SelectItemView itemView, String name ) {
        itemContainer.remove( itemView );
        itemViews.remove( itemView );

        removeItemByName( name );
    }


    private void initHandlers() {
        itemContainer.addDomHandler( event -> showPopup( itemContainer ), ClickEvent.getType() );
    }

    @UiField
    Button caretButton;
    @UiField
    HTMLPanel label;
    @UiField
    HTMLPanel itemContainer;
    @UiField
    SpanElement add;

    @Inject
    Provider<SelectItemView> itemViewProvider;

    List< SelectItemView > itemViews = new ArrayList<SelectItemView >();

    interface SelectorUiBinder extends UiBinder< HTMLPanel, MultipleInputSelector > {}
    private static SelectorUiBinder ourUiBinder = GWT.create( SelectorUiBinder.class );
}