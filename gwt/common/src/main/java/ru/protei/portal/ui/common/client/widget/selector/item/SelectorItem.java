package ru.protei.portal.ui.common.client.widget.selector.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import ru.protei.portal.ui.common.client.selector.SelectorItemChangeHandler;
import ru.protei.portal.ui.common.client.widget.selector.event.*;

/**
 * Вид одного элемента из выпадайки селектора
 */
public class SelectorItem
        extends Composite
        implements HasSelectorItemSelectHandlers, SelectorItemChangeHandler
{

    public SelectorItem() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    @Override
    public HandlerRegistration addSelectorItemSelectHandler(SelectorItemSelectHandler handler) {
        return addHandler(handler, SelectorItemSelectEvent.getType());
    }

    @Override
    public void onItemClicked() {
        SelectorItemSelectEvent.fire(this);
    }

    public void setName(String name ) {
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
