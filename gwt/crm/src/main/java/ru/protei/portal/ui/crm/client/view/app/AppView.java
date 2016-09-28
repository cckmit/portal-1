package ru.protei.portal.ui.crm.client.view.app;

import com.google.gwt.core.client.GWT;
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

    public void setActivity( AbstractAppActivity activity ) {
        this.activity = activity;
    }

    public void setUsername(String username) {
        this.user.setText( username );
    }

    public void setPanelName(String panelName) {
        this.panelName.setText( panelName );
    }

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
        if ( activity != null ) {
            activity.onLogoutClicked();
        }
    }

    @UiHandler("hideBarButton")
    public void onHideBarButtonClicked(ClickEvent event) {
        logo.getElement().addClassName("inactive");
        sidebar.getElement().addClassName("inactive");
    }

    public void onKeyPress (KeyPressEvent event) {
        if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE)
            activity.onLogoutClicked();
    }

    public void onClick (ClickEvent event) {
        logo.getElement().removeClassName("inactive");
        sidebar.getElement().removeClassName("inactive");
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
    HTMLPanel logo;
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


//    @UiField
//    HTMLPanel footer;

    AbstractAppActivity activity;

    interface AppViewUiBinder extends UiBinder<Widget, AppView> {}
    private static  AppViewUiBinder ourUiBinder = GWT.create(  AppViewUiBinder.class );
}