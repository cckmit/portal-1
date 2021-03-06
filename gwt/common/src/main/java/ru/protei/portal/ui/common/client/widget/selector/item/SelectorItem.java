package ru.protei.portal.ui.common.client.widget.selector.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.widget.selector.event.*;

import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;

/**
 * Вид одного элемента из выпадайки селектора
 */
public class SelectorItem
        extends Composite
        implements HasSelectorItemSelectHandlers
{

    public SelectorItem() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        root.addDomHandler(event -> {
            if (event.getNativeKeyCode() != KeyCodes.KEY_ENTER) {
                return;
            }

            event.preventDefault();
            SelectorItemSelectEvent.fire(this);
        }, KeyDownEvent.getType());
    }

    @Override
    public HandlerRegistration addSelectorItemSelectHandler(SelectorItemSelectHandler handler) {
        return addHandler(handler, SelectorItemSelectEvent.getType());
    }

    public void setName( String name ) {
        text.setInnerText( name );
        setTitle(name);
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

    public void setTitle(String title) {
        root.setTitle(title);
    }

    @UiHandler( "anchor" )
    public void onAnchorClicked( ClickEvent event ) {
        event.preventDefault();

        SelectorItemSelectEvent.fire(this);
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
