package ru.protei.portal.ui.common.client.widget.selector.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.ui.common.client.selector.AbstractSelectorItem;
import ru.protei.portal.ui.common.client.selector.SelectorItem;
import ru.protei.portal.ui.common.client.selector.popup.item.SelectorItemHandler;

import static ru.protei.portal.ui.common.client.selector.util.SelectorItemKeyboardKey.isSelectorItemKeyboardKey;

/**
 * Вид одного элемента из выпадайки селектора
 */
public class PopupSelectableItem<T> extends Composite
        implements  HasEnabled, SelectorItem<T>
{
    public PopupSelectableItem() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        checkbox.setFormValue( Boolean.FALSE.toString() );

        addDomHandler(event -> {
            event.preventDefault();

            setSelected(!checkbox.getValue());

            if (selectorItemHandler != null) {
                selectorItemHandler.onMouseClickEvent(this, event);
            }
        }, ClickEvent.getType());

        addDomHandler(event -> {
            if (!isSelectorItemKeyboardKey(event.getNativeKeyCode())) {
                return;
            }

            event.preventDefault();

            setSelected(!checkbox.getValue());

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

    public void setTitle( String text ) {
        checkbox.setTitle(text);
    }

    @Override
    public void addSelectorHandler(SelectorItemHandler<T> selectorItemHandler) {
        this.selectorItemHandler = selectorItemHandler;
    }

    @Override
    public void setElementHtml(String html ) {
        label.setInnerHTML(html);
    }

    public void setIcon(String style) {
        icon.setClassName(style);
    }

    public void setSelected( Boolean isSelected ) {
        checkbox.setValue( isSelected );
        checkbox.setFormValue( isSelected.toString() );
    }

    @Override
    public boolean isEnabled() {
        return checkbox.isEnabled();
    }

    @Override
    public void setEnabled( boolean enabled ) {
        checkbox.setEnabled( enabled );
    }

    public void setEnsureDebugId( String debugId ) {
        checkbox.ensureDebugId( debugId );
    }

    @Override
    public HandlerRegistration addKeyUpHandler( KeyUpHandler keyUpHandler) {
        return addHandler( keyUpHandler, KeyUpEvent.getType() );
    }

    @UiHandler("checkbox")
    public void onKeyUpEvent( KeyUpEvent keyUpEvent) {
        keyUpEvent.preventDefault();

        KeyUpEvent.fireNativeEvent(keyUpEvent.getNativeEvent(), this);
    }

    private T value;

    @UiField
    HTMLPanel root;

    @UiField
    CheckBox checkbox;

    @UiField
    Element icon;

    @UiField
    SpanElement label;

    private SelectorItemHandler<T> selectorItemHandler;

    interface SelectorItemViewUiBinder extends UiBinder<HTMLPanel, PopupSelectableItem> {}
    private static SelectorItemViewUiBinder ourUiBinder = GWT.create( SelectorItemViewUiBinder.class );
}
