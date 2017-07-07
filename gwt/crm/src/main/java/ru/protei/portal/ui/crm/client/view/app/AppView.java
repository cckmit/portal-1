package ru.protei.portal.ui.crm.client.view.app;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.ParagraphElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.ui.crm.client.activity.app.AbstractAppActivity;
import ru.protei.portal.ui.crm.client.activity.app.AbstractAppView;
import ru.protei.portal.ui.crm.client.widget.localeselector.LocaleImagesHelper;
import ru.protei.portal.ui.crm.client.widget.localeselector.LocaleSelector;
import ru.protei.portal.ui.crm.client.widget.navsearch.NavSearchBox;

import java.util.List;

/**
 * Вид основной формы приложения
 */
public class AppView extends Composite
        implements AbstractAppView,
        KeyUpHandler {
    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        initHandlers();
        // todo temporary set invisible
        search.setVisible( false );
    }

    @Override
    public void setActivity( AbstractAppActivity activity ) {
        this.activity = activity;
    }

    @Override
    public void setUsername( String username, String role ) {
        this.username.setInnerText( username );
        //this.role.setInnerText( role );
    }

    @Override
    public HasWidgets getDetailsContainer() {
        return container;
    }

    @Override
    public HasWidgets getMenuContainer() {
        return menuContainer;
    }

    @Override
    public HasWidgets getNotifyContainer() {
        return notifyContainer;
    }

    @Override
    public HasWidgets getActionBarContainer() {
        return actionBarContainer;
    }

    @Override
    public void setLocaleList( List< LocaleImagesHelper.ImageModel > langModelList ) {
        locale.fillOptions( langModelList );
    }

    @Override
    public void setCurrentLocaleLabel( String currentLang ) {
        locale.setLabel( currentLang );
    }

    @UiHandler( "logout" )
    public void onLogoutClicked( ClickEvent event ) {
        event.preventDefault();
        if ( activity != null ) {
            activity.onLogoutClicked();
        }
    }

    @UiHandler( "locale" )
    public void onLocaleClicked( ValueChangeEvent< LocaleImagesHelper.ImageModel > event ) {
        if ( activity != null ) {
            activity.onLocaleClicked( event.getValue().value );
        }
    }

    @UiHandler( "toggleButton" )
    public void onToggleButtonClicked( ClickEvent event ) {
        event.preventDefault();
        boolean isCollapsed = RootPanel.get().getStyleName().contains( "sidebar-collapse" );
        if ( isCollapsed ) {
            RootPanel.get().removeStyleName( "sidebar-collapse" );
            return;
        }

        RootPanel.get().addStyleName( "sidebar-collapse" );
    }

    @Override
    public void onKeyUp( KeyUpEvent event ) {
        if ( event.getNativeKeyCode() == KeyCodes.KEY_F4 && event.isAnyModifierKeyDown() && event.isControlKeyDown() ) {
            event.preventDefault();
            activity.onLogoutClicked();
        }
    }

    private void initHandlers() {
        RootPanel.get().sinkEvents( Event.ONKEYUP );
        RootPanel.get().addHandler( this, KeyUpEvent.getType() );
    }

    @UiField
    Anchor toggleButton;
    @UiField
    NavSearchBox search;
    @Inject
    @UiField( provided = true )
    LocaleSelector locale;
    @UiField
    Anchor logout;

    @UiField
    HTMLPanel sidebar;
    @UiField
    HTMLPanel container;

    @UiField
    HTMLPanel notifyContainer;
    @UiField
    ParagraphElement username;
    @UiField
    AnchorElement role;
    @UiField
    HTMLPanel menuContainer;
    @UiField
    HTMLPanel actionBarContainer;

    AbstractAppActivity activity;

    interface AppViewUiBinder extends UiBinder< Widget, AppView > {
    }

    private static AppViewUiBinder ourUiBinder = GWT.create( AppViewUiBinder.class );
}