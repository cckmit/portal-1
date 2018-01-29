package ru.protei.portal.ui.crm.client.view.app;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.ParagraphElement;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.ui.crm.client.activity.app.AbstractAppActivity;
import ru.protei.portal.ui.crm.client.activity.app.AbstractAppView;
import ru.protei.portal.ui.crm.client.widget.localeselector.LocaleImage;
import ru.protei.portal.ui.crm.client.widget.localeselector.LocaleSelector;
import ru.protei.portal.ui.crm.client.widget.navsearch.NavSearchBox;

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
    public void setUser( String username, String company, String iconSrc ) {
        this.username.setInnerText( username );
        this.company.setInnerText( company );
        this.icon.setSrc( iconSrc );
    }

    @Override
    public HasValue< LocaleImage > locale() {
        return locale;
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

    @UiHandler( "logout" )
    public void onLogoutClicked( ClickEvent event ) {
        event.preventDefault();
        if ( activity != null ) {
            activity.onLogoutClicked();
        }
    }

    @UiHandler( "locale" )
    public void onLocaleClicked( ValueChangeEvent<LocaleImage> event ) {
        if ( activity != null ) {
            activity.onLocaleChanged( event.getValue().getLocale() );
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

        userPanel.sinkEvents( Event.ONCLICK );
        userPanel.addHandler( event -> {
            if ( activity != null ) {
                activity.onUserClicked();
            }
        }, ClickEvent.getType() );
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
    AnchorElement company;
    @UiField
    HTMLPanel menuContainer;
    @UiField
    HTMLPanel actionBarContainer;
    @UiField
    HTMLPanel userPanel;
    @UiField
    ImageElement icon;

    AbstractAppActivity activity;

    interface AppViewUiBinder extends UiBinder< Widget, AppView > {}

    private static AppViewUiBinder ourUiBinder = GWT.create( AppViewUiBinder.class );
}