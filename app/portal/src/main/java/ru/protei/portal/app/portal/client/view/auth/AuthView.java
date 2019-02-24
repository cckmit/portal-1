package ru.protei.portal.app.portal.client.view.auth;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.SpanElement;
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
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.app.portal.client.activity.auth.AbstractAuthActivity;
import ru.protei.portal.app.portal.client.activity.auth.AbstractAuthView;
import ru.protei.portal.ui.common.client.lang.Lang;

/**
 * Вид формы авторизации
 */
public class AuthView extends Composite implements AbstractAuthView, KeyPressHandler {

    public AuthView() {
        initWidget (ourUiBinder.createAndBindUi (this));
        ensureDebugIds();
        initHandlers();
        initPlaceholders();
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

    @UiHandler ("loginButton")
    public void onLoginClicked( ClickEvent event ) {
        if (activity != null) {
            activity.onLoginClicked ();
        }
    }

    public void onKeyPress (KeyPressEvent event) {
        if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER)
            activity.onLoginClicked();
    }

    private void ensureDebugIds() {
        login.ensureDebugId(DebugIds.AUTH.INPUT_LOGIN);
        password.ensureDebugId(DebugIds.AUTH.INPUT_PASSWORD);
        loginButton.ensureDebugId(DebugIds.AUTH.LOGIN_BUTTON);
    }

    private void initHandlers() {
        loginContainer.sinkEvents(Event.ONKEYPRESS);
        loginContainer.addHandler(this, KeyPressEvent.getType());
    }

    public void setFocus () {
        login.setFocus(true);
    }

    @Override
    public void showError(String msg){
        errorMessage.removeClassName("hide");
        errorText.setInnerText(msg);
    }

    @Override
    public void hideError(){
        errorMessage.addClassName("hide");
    }

    @Override
    public void reset() {
        login.setText("");
        password.setText("");
    }

    private void initPlaceholders() {
        login.getElement().setAttribute("placeholder", lang.accountLogin() );
        password.getElement().setAttribute("placeholder", lang.accountPassword() );
    }

    @UiField
    TextBox login;

    @UiField
    TextBox password;

    @UiField
    Button loginButton;

    @UiField
    Lang lang;
    @UiField
    HTMLPanel loginContainer;
    @UiField
    SpanElement errorText;
    @UiField
    DivElement errorMessage;

    AbstractAuthActivity activity;

    interface AuthViewUiBinder extends UiBinder<HTMLPanel, AuthView> {}
    private static AuthViewUiBinder ourUiBinder = GWT.create (AuthViewUiBinder.class);
}