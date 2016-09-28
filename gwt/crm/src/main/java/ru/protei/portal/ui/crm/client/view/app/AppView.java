package ru.protei.portal.ui.crm.client.view.app;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.ui.crm.client.activity.app.AbstractAppActivity;
import ru.protei.portal.ui.crm.client.activity.app.AbstractAppView;

/**
 * Вид основной формы приложения
 */
public class AppView extends Composite implements AbstractAppView, KeyPressHandler, ClickHandler {
    public AppView() {
        initWidget(ourUiBinder.createAndBindUi(this));
        initHandlers();
    }

    @Override
    public void setActivity( AbstractAppActivity activity ) {
        this.activity = activity;
    }

    @Override
    public void setUsername(String username) {
        this.user.setText( username );
    }

    @Override
    public void setPanelName(String panelName) {
        this.panelName.setText( panelName );
    }

    @Override
    public HasWidgets getDetailsContainer() {
        return container;
    }

    @UiHandler("user")
    public void onUserClicked( ClickEvent event ) {
        event.preventDefault();
        if ( activity != null ) {
            activity.onUserClicked();
        }
    }

    @UiHandler("logout")
    public void onLogoutButtonClicked(ClickEvent event) {
        event.preventDefault();
        if ( activity != null ) {
            activity.onLogoutClicked();
        }
    }

    @UiHandler("hideBarButton")
    public void onHideBarButtonClicked(ClickEvent event) {
        event.preventDefault();
        logo.addClassName("inactive");
        sidebar.addStyleName("inactive");
    }

    @UiHandler("companies")
    public void onCompaniesClicked(ClickEvent event) {
        event.preventDefault();
        if ( activity != null ) {
            activity.onCompaniesClicked();
        }
    }

    @Override
    public void onKeyPress (KeyPressEvent event) {
        if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE)
            activity.onLogoutClicked();
    }

    @Override
    public void onClick (ClickEvent event) {
        logo.removeClassName("inactive");
        sidebar.removeStyleName("inactive");
    }

    private void initHandlers() {
        appPanel.sinkEvents(Event.ONKEYPRESS);
        appPanel.addHandler(this, KeyPressEvent.getType());

        noScrol.sinkEvents(Event.ONCLICK);
        noScrol.addHandler(this, ClickEvent.getType());
    }

    public void setFocus () {
        search.setFocus(true);
    }

    @UiField
    HTMLPanel appPanel;

    @UiField
    DivElement logo;
    @UiField
    Anchor hideBarButton;

    @UiField
    HTMLPanel noScrol;

    @UiField
    HTMLPanel navbar;
    @UiField
    TextBox search;
    @UiField
    Anchor logout;

    @UiField
    HTMLPanel sidebar;
    @UiField
    HTMLPanel container;

    @UiField
    Anchor user;
    @UiField
    Label panelName;


    @UiField
    Anchor companies;


//    @UiField
//    HTMLPanel footer;

    AbstractAppActivity activity;

    interface AppViewUiBinder extends UiBinder<Widget, AppView> {}
    private static  AppViewUiBinder ourUiBinder = GWT.create(  AppViewUiBinder.class );
}