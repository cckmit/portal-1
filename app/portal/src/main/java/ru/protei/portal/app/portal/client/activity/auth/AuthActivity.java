package ru.protei.portal.app.portal.client.activity.auth;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.app.portal.client.service.AuthControllerAsync;
import ru.protei.portal.app.portal.client.widget.locale.LocaleImage;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.ui.common.client.common.LocalStorageService;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.util.LocaleUtils;
import ru.protei.portal.ui.common.client.util.PasswordUtils;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.Profile;
import ru.protei.winter.web.common.client.events.MenuEvents;

import java.util.Date;
import java.util.Objects;
import java.util.logging.Logger;

import static ru.protei.portal.ui.common.client.common.UiConstants.REMEMBER_ME_PREFIX;

/**
 * Активность окна авторизации
 */
public abstract class AuthActivity implements AbstractAuthActivity, Activity {

    @Event
    public void onInit( AuthEvents.Init init ) {
        this.init = init;
        view.setActivity (this );
    }

    @Event
    public void onShow( AuthEvents.Show event ) {
        tryAutoLogin();
    }

    @Event
    public void onLogout( AppEvents.Logout event ) {
        resetRememberMe();
        authService.logout(new FluentCallback<Void>().withSuccess(v -> {
            view.reset();
            fireEvent(new AuthEvents.Show());
        }));
    }

    @Override
    public void onLocaleChanged(String locale) {
        LocaleUtils.changeLocale( locale );
    }

    @Override
    public void onLoginClicked() {
        String login = view.login().getValue();
        String pwd = view.password().getValue();
        view.showError( "" );
        view.hideError();
        authService.authenticate(login, pwd, new FluentCallback<Profile>()
                .withError((throwable, defaultErrorHandler, status) -> {
                    log.warning( "onLoginClicked(): e: " + throwable );
                    if ( throwable instanceof StatusCodeException ) {
                        view.showError(lang.errServerUnavailable());
                        return;
                    }

                    if ( throwable instanceof IncompatibleRemoteServiceException) {
                        defaultErrorHandler.accept( throwable );
                        return;
                    }

                    if (En_ResultStatus.ACCOUNT_IS_LOCKED.equals(status)) {
                        view.showError( lang.errAccountIsLocked() );
                        return;
                    }

                    view.showError( lang.errLoginOrPwd() );
                } )
                .withSuccess(profile -> {
                    view.hideError();
                    fireAuthSuccess(profile);
                    fireEvent(new NotifyEvents.Show(lang.msgHello(), NotifyEvents.NotifyType.SUCCESS));
                    if (view.rememberMe().getValue()) {
                        String pwdCrypt = encrypt(pwd);
                        storage.set(REMEMBER_ME_PREFIX + "login", login);
                        storage.set(REMEMBER_ME_PREFIX + "pwd", pwdCrypt);
                    }
                    else {
                        String loginFromStorage = storage.getOrDefault(REMEMBER_ME_PREFIX + "login", null);
                        String pwdFromStorage = storage.getOrDefault(REMEMBER_ME_PREFIX + "pwd", null);

                        if (!Objects.equals(login, loginFromStorage) || !Objects.equals(encrypt(pwd), pwdFromStorage)) {
                            resetRememberMe();
                        }
                    }
                }));
    }

    @Override
    public void onWindowsFocus() {
        String loginFromStorage = storage.get(REMEMBER_ME_PREFIX + "login");
        String pwdFromStorage = storage.get(REMEMBER_ME_PREFIX + "pwd");

        if (loginFromStorage == null || pwdFromStorage == null) {
            return;
        }

        tryAutoLogin();
    }

    private void tryAutoLogin() {
        String login = storage.getOrDefault(REMEMBER_ME_PREFIX + "login", null);
        String pwd = storage.getOrDefault(REMEMBER_ME_PREFIX + "pwd", null);
        if (pwd != null) {
            pwd = decrypt(pwd);
        }
        if (login == null || pwd == null) {
           login = null;
           pwd = null;
        }
        authService.authenticate(login, pwd, new FluentCallback<Profile>()
                .withError(throwable -> {
                    if ( throwable instanceof StatusCodeException ) {
                        view.showError(lang.errServerUnavailable());
                    }

                    if(throwable instanceof RequestFailedException) {
                        resetRememberMe();
                    }
                    placeView();
                })
                .withSuccess(profile -> {
                    if (profile != null) {
                        fireAuthSuccess(profile);
                        return;
                    }
                    resetRememberMe();
                    placeView();
                }));
    }

    private void placeView() {
        init.parent.clear();
        init.parent.add( view.asWidget() );

        view.setFocus();

        String currentLocale = LocaleInfo.getCurrentLocale().getLocaleName();
        view.locale().setValue( LocaleImage.findByLocale( currentLocale ));
        view.setLogoByLocale(currentLocale);

        view.setYear(DateTimeFormat.getFormat("yyyy").format(new Date()));

        view.rememberMe().setValue(false);
    }

    private void fireAuthSuccess(Profile profile) {
        fireEvent(new MenuEvents.Clear());
        fireEvent(new AuthEvents.Success(profile));
    }

    private void resetRememberMe() {
        storage.remove(REMEMBER_ME_PREFIX + "login");
        storage.remove(REMEMBER_ME_PREFIX + "pwd");
    }

    private String encrypt(String pwd) {
        return PasswordUtils.encrypt(pwd);
    }

    private String decrypt(String pwd) {
        return PasswordUtils.decrypt(pwd);
    }

    @Inject
    AbstractAuthView view;
    @Inject
    AuthControllerAsync authService;
    @Inject
    Lang lang;
    @Inject
    LocalStorageService storage;
    @Inject
    DefaultErrorHandler defaultErrorHandler;

    private AuthEvents.Init init;
    private static final Logger log = Logger.getLogger( AuthActivity.class.getName() );
}
