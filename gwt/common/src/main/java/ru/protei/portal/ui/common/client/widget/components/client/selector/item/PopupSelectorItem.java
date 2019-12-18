package ru.protei.portal.ui.common.client.widget.components.client.selector.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.widget.components.client.selector.baseselector.SelectorItemHandler;
import ru.protei.portal.ui.common.client.widget.components.client.selector.baseselector.SelectorItem;
import ru.protei.portal.ui.common.client.widget.selector.item.SelectableItem;

import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;

/**
 * Вид одного элемента из выпадайки селектора
 */
public class PopupSelectorItem<T>
        extends Composite
        implements SelectorItem<T>
{
    public PopupSelectorItem() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        root.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.SELECTOR.POPUP.ITEM);
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public void setValue(T t) {
        value = t;
    }

    @Override
    public void addSelectorHandler(SelectorItemHandler selectorItemHandler) {
        this.selectorItemHandler = selectorItemHandler;
    }

    @Override
    public HandlerRegistration addKeyUpHandler( KeyUpHandler keyUpHandler) {
        return addHandler( keyUpHandler, KeyUpEvent.getType() );
    }

    @Override
    public void setElementHtml(String elementHtml ) {
        root.getElement().setInnerHTML( elementHtml );
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
        image.removeClassName( "hide" );
        image.setSrc( src );
    }

    @UiHandler( "anchor" )
    public void onAnchorClicked( ClickEvent event ) {
        event.preventDefault();

        if(selectorItemHandler!=null) {
            selectorItemHandler.onSelectorItemClicked(this);
        }
    }

    @UiHandler("anchor")
    public void onKeyUpEvent( KeyUpEvent keyUpEvent) {
        keyUpEvent.preventDefault();

        KeyUpEvent.fireNativeEvent(keyUpEvent.getNativeEvent(), this);
    }

    @UiField
    SpanElement text;
    @UiField
    HTMLPanel root;
    @UiField
    Anchor anchor;
    @UiField
    ImageElement image;
    @UiField
    Element icon;

    private SelectorItemHandler selectorItemHandler;
    private T value;

    interface SelectorItemViewUiBinder extends UiBinder<HTMLPanel, PopupSelectorItem> {
    }
    private static SelectorItemViewUiBinder ourUiBinder = GWT.create( SelectorItemViewUiBinder.class );
}