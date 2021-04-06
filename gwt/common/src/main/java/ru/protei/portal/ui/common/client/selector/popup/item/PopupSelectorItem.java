package ru.protei.portal.ui.common.client.selector.popup.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.ui.common.client.selector.SelectorItem;

import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.HIDE;
import static ru.protei.portal.ui.common.client.selector.util.SelectorItemKeyboardKey.isSelectorItemKeyboardKey;

/**
 * Вид одного элемента из выпадайки селектора
 */
public class PopupSelectorItem<T>
        extends Composite
        implements SelectorItem<T>
{
    public PopupSelectorItem() {
        initWidget(ourUiBinder.createAndBindUi(this));
        addDomHandler(event -> {
            event.preventDefault();
            if (selectorItemHandler != null) {
                selectorItemHandler.onMouseClickEvent(this, event);
            }
        }, ClickEvent.getType());

        addDomHandler(event -> {
            if (!isSelectorItemKeyboardKey(event.getNativeKeyCode())) {
                return;
            }

            event.preventDefault();
            if (selectorItemHandler != null) {
                selectorItemHandler.onKeyboardButtonDown(this, event);
            }
        }, KeyDownEvent.getType());
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public void addSelectorHandler(SelectorItemHandler<T> selectorItemHandler) {
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

    public void setName(String name ) {
        text.setInnerHTML( name );
    }

    public void setStyle( String style ) {
        anchor.setStyleName( style );
    }

    public void setIcon( String className ) {
        icon.setClassName( className );
    }

    public void setIcon( String className, String innerText ) {
        icon.setClassName( className );
        icon.setInnerText( innerText );
    }

    public void setIconColor( String color ) {
        icon.getStyle().setColor( color );
    }

    public void setIconColor( String color,  String backgroundColor ) {
        icon.getStyle().setColor( color );
        icon.getStyle().setBackgroundColor( backgroundColor );
    }

    public void setImage( String src ) {
        image.removeClassName( HIDE );
        image.setSrc( src );
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

    private SelectorItemHandler<T> selectorItemHandler;
    private T value;

    interface SelectorItemViewUiBinder extends UiBinder<HTMLPanel, PopupSelectorItem> {
    }
    private static SelectorItemViewUiBinder ourUiBinder = GWT.create( SelectorItemViewUiBinder.class );
}
