package ru.protei.portal.ui.common.client.widget.selector.icon;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;
import ru.protei.portal.ui.common.client.widget.selector.item.SelectorItem;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Селектор с иконками
 */
public class IconSelector<T> extends Selector<T> implements HasEnabled {

    public IconSelector() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    public void setHeader( String label ) {
        this.label.getElement().setInnerText( label == null ? "" : label );
    }

    @Override
    public boolean isEnabled() {
        return anchorIcon.isEnabled();
    }

    @Override
    public void setEnabled( boolean enabled ) {
        anchorIcon.setEnabled( enabled );
        if ( enabled ) {
            removeStyleName( "disabled" );
        } else {
            addStyleName( "disabled" );
        }
    }

    @UiHandler( { "caretAnchor", "anchorIcon" } )
    public void onShowPopupClicked( ClickEvent event ) {
        event.preventDefault();
        if ( anchorIcon.isEnabled() ) {
            showPopup( inputContainer );
        }
    }

    @UiHandler( { "caretAnchor", "anchorIcon" } )
    public void onKeyDownPressed( KeyDownEvent event ) {
        if ( event.getNativeKeyCode() == KeyCodes.KEY_DOWN ) {
            event.preventDefault();
            if ( anchorIcon.isEnabled() ) {
                showPopup( inputContainer );
            }
        }
    }

    @Override
    public void fillSelectorView( DisplayOption selectedValue ) {
        anchorIcon.setStyleName( selectedValue.getIcon() );
        anchorIcon.setTitle( selectedValue.getName() );
    }

    @UiField
    Anchor anchorIcon;
    @UiField
    Anchor caretAnchor;
    @UiField
    HTMLPanel inputContainer;
    @UiField
    HTMLPanel label;

    interface SelectorUiBinder extends UiBinder< HTMLPanel, IconSelector> {}
    private static SelectorUiBinder ourUiBinder = GWT.create( SelectorUiBinder.class );
}
