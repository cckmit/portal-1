package ru.protei.portal.ui.common.client.widget.selector.image;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;
import ru.protei.portal.ui.common.client.widget.selector.item.SelectorItem;

import java.util.HashMap;
import java.util.Map;

/**
 * Селектор картинок с текстом
 */
public class ImageSelector< T > extends Selector< T > implements HasEnabled {

    public ImageSelector() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    @Override
    public boolean isEnabled() {
        return anchor.isEnabled();
    }

    @UiHandler( "anchor" )
    public void onAnchorClicked( ClickEvent event ) {
        event.preventDefault();
        if ( anchor.isEnabled() ) {
            showPopup( inputContainer );
        }
    }

    @Override
    public void setEnabled( boolean enabled ) {
        anchor.setEnabled( enabled );
        if ( enabled ) {
            removeStyleName( "disabled" );
        } else {
            addStyleName( "disabled" );
        }
    }

    @Override
    public void fillSelectorView( DisplayOption selectedValue ) {
        T selected = getValue();
        String name = itemModelToIcon.get( selected );
        if ( selected != null ) {
            label.setInnerText( name );
        }
    }

    public void addOption( String name, T value, String img ) {
        super.addOption( name, value );
        itemModelToIcon.put( value, name );

        SelectorItem selectorItem = itemToViewModel.get( value );
        if ( selectorItem != null ) {
            selectorItem.setImage( img );
        }
    }

    public void setLabel( String value ) {
        label.setInnerText( value );
    }

    public void setAnchorTitle( String value ) {
        anchor.setTitle( value );
    }

    public void setIcon( String value ) {
        icon.setClassName( value );
    }

    @UiField
    Anchor anchor;
    @UiField
    SpanElement label;
    @UiField
    Element icon;
    @UiField
    HTMLPanel inputContainer;

    @UiField
    Lang lang;

    private Map< T, String > itemModelToIcon = new HashMap<>();

    interface ImageSelectorUiBinder extends UiBinder< HTMLPanel, ImageSelector > {
    }

    private static ImageSelectorUiBinder ourUiBinder = GWT.create( ImageSelectorUiBinder.class );
}
