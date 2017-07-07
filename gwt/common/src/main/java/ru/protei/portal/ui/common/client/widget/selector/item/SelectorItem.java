package ru.protei.portal.ui.common.client.widget.selector.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.SpanElement;
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
import com.google.gwt.user.client.ui.Image;

/**
 * Вид одного элемента из выпадайки селектора
 */
public class SelectorItem
        extends Composite
        implements HasClickHandlers
{

    public SelectorItem() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    @Override
    public HandlerRegistration addClickHandler( ClickHandler handler ) {
        return addHandler( handler, ClickEvent.getType() );
    }

    public void setName( String name ) {
        text.setInnerHTML( name );
    }

    public void setStyle( String style ) {
        anchor.setStyleName( style );
    }

    public void setIcon( String className ) {
        icon.setClassName( className );
    }

    public void setImage( String src ) {
        image.setSrc( src );
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
    Element icon;
    @UiField
    SpanElement text;
    @UiField
    ImageElement image;

    interface SelectorItemViewUiBinder extends UiBinder<HTMLPanel, SelectorItem > {}
    private static SelectorItemViewUiBinder ourUiBinder = GWT.create( SelectorItemViewUiBinder.class );
}