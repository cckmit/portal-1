package ru.protei.portal.ui.crm.client.view.auth;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;
import ru.protei.portal.ui.crm.client.activity.auth.AbstractAuthActivity;
import ru.protei.portal.ui.crm.client.activity.auth.AbstractAuthView;

/**
 * Created by turik on 23.09.16.
 */
public class AuthView extends Composite implements AbstractAuthView {

    public AuthView() {
        initWidget (ourUiBinder.createAndBindUi (this));
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

    @UiField
    TextBox login;

    @UiField
    TextBox password;

    @UiField
    Button loginButton;

    AbstractAuthActivity activity;

    interface AuthViewUiBinder extends UiBinder<HTMLPanel, AuthView> {}
    private static AuthViewUiBinder ourUiBinder = GWT.create (AuthViewUiBinder.class);
}