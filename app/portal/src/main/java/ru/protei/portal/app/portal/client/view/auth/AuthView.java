package ru.protei.portal.app.portal.client.view.auth;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.ParagraphElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.app.portal.client.activity.auth.AbstractAuthActivity;
import ru.protei.portal.app.portal.client.activity.auth.AbstractAuthView;
import ru.protei.portal.app.portal.client.widget.locale.LocaleBtnGroup;
import ru.protei.portal.app.portal.client.widget.locale.LocaleImage;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;

import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.HIDE;

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

    private void notifyOnWindowFocus() {
        if (activity != null) {
            activity.onWindowsFocus();
        }
    }

    public void setFocus () {
        login.setFocus(true);
    }

    @Override
    public void showError(String msg){
        errorMessage.removeClassName(HIDE);
        errorText.setInnerText(msg);
    }

    @Override
    public void hideError(){
        errorMessage.addClassName(HIDE);
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

    @Override
    public void setLogoByLocale(String locale) {
        logo.setSrc("./images/logo-blue-" + locale + ".svg");
    }

    @Override
    public void setYear(String year) {
        footerText.setInnerText("© " + year + " НТЦ ПРОТЕЙ. All rights reserved");
    }

    @UiHandler( "locale" )
    public void onLocaleClicked( ValueChangeEvent<LocaleImage> event ) {
        if ( activity != null ) {
            activity.onLocaleChanged( event.getValue().getLocale() );
        }
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        initJsHandlers(this);
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        terminateJsHandlers();
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        login.ensureDebugId(DebugIds.AUTH.INPUT_LOGIN);
        password.ensureDebugId(DebugIds.AUTH.INPUT_PASSWORD);
        loginButton.ensureDebugId(DebugIds.AUTH.LOGIN_BUTTON);
        errorText.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.AUTH.ERROR_ALERT);
    }

    private void initHandlers() {
        loginContainer.sinkEvents(Event.ONKEYPRESS);
        loginContainer.addHandler(this, KeyPressEvent.getType());
    }

    public native void initJsHandlers(AuthView authView) /*-{
        $wnd.onfocus = function() { authView.@ru.protei.portal.app.portal.client.view.auth.AuthView::notifyOnWindowFocus()(); }
    }-*/;

    public native void terminateJsHandlers() /*-{
        $wnd.onfocus = null;
    }-*/;

    private void initPlaceholders() {
        login.getElement().setAttribute("placeholder", lang.accountLogin() );
        password.getElement().setAttribute("placeholder", lang.accountPassword() );
    }

    @UiField
    TextBox login;
    @UiField
    TextBox password;
    @UiField
    CheckBox rememberMe;
    @UiField
    Button loginButton;
    @UiField
    Lang lang;
    @UiField
    HTMLPanel loginContainer;
    @UiField
    ParagraphElement footerText;
    @UiField
    SpanElement errorText;
    @UiField
    DivElement errorMessage;
    @UiField
    LocaleBtnGroup locale;
    @UiField
    ImageElement logo;

    AbstractAuthActivity activity;

    interface AuthViewUiBinder extends UiBinder<HTMLPanel, AuthView> {}
    private static AuthViewUiBinder ourUiBinder = GWT.create (AuthViewUiBinder.class);
}