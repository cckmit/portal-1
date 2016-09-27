package ru.protei.portal.ui.crm.client.view.auth;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;
import ru.protei.portal.ui.crm.client.activity.auth.AbstractAuthActivity;
import ru.protei.portal.ui.crm.client.activity.auth.AbstractAuthView;

/**
 * Created by turik on 23.09.16.
 */
public class AuthView extends Composite implements AbstractAuthView, KeyPressHandler {

    public AuthView() {
        initWidget (ourUiBinder.createAndBindUi (this));
        initHandlers();
    }

    public void setActivity(AbstractAuthActivity activity) {
        this.activity = activity;
    }

    public String getUserName() {
        return login.getText ();
    }

    public void setUserName(String userName) {
        login.setText (userName);
    }

    public String getPassword() {
        return password.getText ();
    }

    public void setPassword(String password) {
        this.password.setText (password);

    }

    @UiHandler ( "loginButton")
    public void onLoginClicked( ClickEvent event ) {
        if (activity != null) {
            activity.onLoginClicked ();
        }
    }

    @Override
    public void onKeyPress (KeyPressEvent event) {
        if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER)
            activity.onLoginClicked();
    }

    private void initHandlers() {
        loginPanel.sinkEvents(Event.ONKEYPRESS);
        loginPanel.addHandler(this, KeyPressEvent.getType());
    }

    @Override
    public void setFocus () {
        login.setFocus(true);
    }

    @UiField
    TextBox login;

    @UiField
    TextBox password;

    @UiField
    Button loginButton;
    @UiField
    HTMLPanel loginPanel;

    AbstractAuthActivity activity;

    interface AuthViewUiBinder extends UiBinder<HTMLPanel, AuthView> {}
    private static AuthViewUiBinder ourUiBinder = GWT.create (AuthViewUiBinder.class);
}