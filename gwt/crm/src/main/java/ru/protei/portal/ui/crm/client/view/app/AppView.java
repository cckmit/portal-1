package ru.protei.portal.ui.crm.client.view.app;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
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
public class AppView extends Composite implements AbstractAppView, KeyPressHandler {
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
    public void onButtonClicked( ClickEvent event ) {
        if ( activity != null ) {
            activity.onLogoutClicked();
        }
    }

    @Override
    public void onKeyPress (KeyPressEvent event) {
        if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE)
            activity.onLogoutClicked();
    }

    private void initHandlers() {
        appPanel.sinkEvents(Event.ONKEYPRESS);
        appPanel.addHandler(this, KeyPressEvent.getType());
    }

    public void setFocus () {
        search.setFocus(true);
    }

    @UiField
    Anchor user;
    @UiField
    HTMLPanel sidebar;
    @UiField
    HTMLPanel container;
    @UiField
    Anchor logout;
    @UiField
    Label panelName;
    @UiField
    HTMLPanel appPanel;
    @UiField
    TextBox search;
//    @UiField
//    HTMLPanel footer;

    AbstractAppActivity activity;

    interface AppViewUiBinder extends UiBinder<Widget, AppView> {}
    private static  AppViewUiBinder ourUiBinder = GWT.create(  AppViewUiBinder.class );
}