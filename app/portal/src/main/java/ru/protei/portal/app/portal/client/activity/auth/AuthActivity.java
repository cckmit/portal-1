package ru.protei.portal.app.portal.client.activity.auth;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.app.portal.client.service.AuthControllerAsync;
import ru.protei.portal.app.portal.client.widget.locale.LocaleImage;
import ru.protei.portal.ui.common.client.common.LocalStorageService;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.util.LocaleUtils;
import ru.protei.portal.ui.common.client.util.PasswordUtils;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.Profile;
import ru.protei.winter.web.common.client.events.MenuEvents;

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
        authService.authentificate(login, pwd, new FluentCallback<Profile>()
                .withError(throwable -> view.showError(lang.errLoginOrPwd()))
                .withSuccess(profile -> {
                    view.hideError();
                    fireAuthSuccess(profile);
                    fireEvent(new NotifyEvents.Show(lang.msgHello(), NotifyEvents.NotifyType.SUCCESS));
                    if (view.rememberMe().getValue()) {
                        String pwdCrypt = encrypt(pwd);
                        storage.set(REMEMBER_ME_PREFIX + "login", login);
                        storage.set(REMEMBER_ME_PREFIX + "pwd", pwdCrypt);
                    }
                }));
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
        authService.authentificate(login, pwd, new FluentCallback<Profile>()
                .withError(throwable -> {
                    resetRememberMe();
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

    private AuthEvents.Init init;
}
