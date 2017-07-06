package ru.protei.portal.ui.common.client.widget.selector.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * Вид одного элемента из выпадайки селектора
 */
public class SelectorItemImage
        extends Composite
        implements HasClickHandlers {

    public SelectorItemImage() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    @Override
    public HandlerRegistration addClickHandler( ClickHandler handler ) {
        return addHandler( handler, ClickEvent.getType() );
    }

    public void setImage (String src) {
        this.image.setSrc( src );
    }

    public void setName( String name ) {
        this.name.setInnerText( name );
    }

    @UiHandler( "anchor" )
    public void onAnchorClicked( ClickEvent event ) {
        event.preventDefault();

        ClickEvent.fireNativeEvent( event.getNativeEvent(), this );
    }

    @UiField
    HTMLPanel root;
    @UiField
    Anchor anchor;
    @UiField
    ImageElement image;
    @UiField
    Element name;

    interface SelectorItemImageViewUiBinder extends UiBinder< HTMLPanel, SelectorItemImage > {
    }

    private static SelectorItemImageViewUiBinder ourUiBinder = GWT.create( SelectorItemImageViewUiBinder.class );
}