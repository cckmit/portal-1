package ru.protei.portal.app.portal.client.view.auth;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.app.portal.client.widget.locale.LocaleBtnGroup;
import ru.protei.portal.app.portal.client.widget.locale.LocaleImage;
import ru.protei.portal.app.portal.client.widget.locale.LocaleSelector;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.app.portal.client.activity.auth.AbstractAuthActivity;
import ru.protei.portal.app.portal.client.activity.auth.AbstractAuthView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.optionlist.item.OptionItem;

/**
 * Вид формы авторизации
 */
public class AuthView extends Composite implements AbstractAuthView, KeyPressHandler {

    @Inject
    public void onInit() {
        initWidget (ourUiBinder.createAndBindUi (this));
        ensureDebugIds();
        initHandlers();
        initPlaceholders();
    }

    public void setActivity(AbstractAuthActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasValue<String> login() {
        return login;
    }

    @Override
    public HasValue<String> password() {
        return password;
    }

    @Override
    public HasValue<Boolean> rememberMe() {
        return rememberMe;
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
    public HasValue<LocaleImage> locale() {
        return locale;
    }

    @Override
    public void reset() {
        login.setText("");
        password.setText("");
    }

    @UiHandler( "locale" )
    public void onLocaleClicked( ValueChangeEvent<LocaleImage> event ) {
        if ( activity != null ) {
            activity.onLocaleChanged( event.getValue().getLocale() );
        }
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
    OptionItem rememberMe;
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
    @UiField
    LocaleBtnGroup locale;

    AbstractAuthActivity activity;

    interface AuthViewUiBinder extends UiBinder<HTMLPanel, AuthView> {}
    private static AuthViewUiBinder ourUiBinder = GWT.create (AuthViewUiBinder.class);
}