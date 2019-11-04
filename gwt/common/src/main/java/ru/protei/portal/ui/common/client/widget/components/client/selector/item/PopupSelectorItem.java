package ru.protei.portal.ui.common.client.widget.components.client.selector.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.ui.common.client.widget.components.client.selector.baseselector.SelectorItemHandler;
import ru.protei.portal.ui.common.client.widget.components.client.selector.baseselector.SelectorItem;
import ru.protei.portal.ui.common.client.widget.selector.item.SelectableItem;

/**
 * Вид одного элемента из выпадайки селектора
 */
public class PopupSelectorItem<T>
        extends Composite
        implements SelectorItem<T>
{

    private SelectorItemHandler selectorItemHandler;
    private T value;

    public PopupSelectorItem() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    @Override
    public void addSelectorHandler(SelectorItemHandler selectorItemHandler) {
        this.selectorItemHandler = selectorItemHandler;
    }

    @Override
    public void setElementHtml(String name ) {
        text.getElement().setInnerHTML( name );
    }

    @UiHandler( "checkbox" )
    public void onCheckboxClicked(ClickEvent event) {
        selectorItemHandler.onSelectorItemClicked(this);
        setSelectedStyle();
    }

    @UiHandler( {"text", "info"}  )
    public void onAnchorClicked( ClickEvent event ) {
        event.preventDefault();
        checkbox.setValue( !checkbox.getValue() );
        if(selectorItemHandler!=null) {
            selectorItemHandler.onSelectorItemClicked(this);
        }
    }

    public void setEnsureDebugId( String debugId ) {
        checkbox.ensureDebugId( debugId );
    }

    private void setSelectedStyle() {
        if ( checkbox.getValue() ) {
            text.addStyleName( SELECTED );
            info.addStyleName( SELECTED );
        } else {
            text.removeStyleName( SELECTED );
            info.removeStyleName( SELECTED );
        }
    }

    @UiField
    HTMLPanel panel;
    @UiField
    CheckBox checkbox;
    @UiField
    InlineLabel text;
    @UiField
    InlineLabel info;

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public void setValue(T t) {
        value = t;
    }

    public static final String SELECTED = "selected";


    interface SelectorItemViewUiBinder extends UiBinder<HTMLPanel, PopupSelectorItem> {
    }
    private static SelectorItemViewUiBinder ourUiBinder = GWT.create( SelectorItemViewUiBinder.class );
}