package ru.protei.portal.app.portal.client.activity.auth;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.shared.model.Profile;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.app.portal.client.service.AuthControllerAsync;
import ru.protei.winter.web.common.client.events.MenuEvents;

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
        checkSession();
    }

    @Event
    public void onLogout( AppEvents.Logout event ) {
        authService.logout( new RequestCallback< Void >() {
            @Override
            public void onError( Throwable throwable ) {
            }

            @Override
            public void onSuccess( Void result ) {
                view.reset();
                fireEvent( new AuthEvents.Show() );
            }
        } );
    }

    @Override
    public void onLoginClicked() {
        authService.authentificate( view.getUserName(), view.getPassword(), new RequestCallback< Profile >() {
            @Override
            public void onError( Throwable caught ) {
                view.showError( lang.errLoginOrPwd());
            }

            @Override
            public void onSuccess( Profile profile ) {
                view.hideError();
                fireAuthSuccess(profile);
                fireEvent(new NotifyEvents.Show(lang.msgHello(), NotifyEvents.NotifyType.SUCCESS));
            }
        } );
    }

    public void onResetClicked() {

    }

    private void checkSession() {
        authService.authentificate( null, null, new RequestCallback<Profile>() {
            @Override
            public void onError(Throwable throwable) {
                placeView();
            }

            @Override
            public void onSuccess( Profile profile ) {
                if (profile == null) {
                    placeView();
                    return;
                }

                fireAuthSuccess(profile);
            }
        });
    }

    private void placeView() {
        init.parent.clear();
        init.parent.add( view.asWidget() );

        view.setFocus();
    }

    private void fireAuthSuccess(Profile profile) {
        fireEvent(new MenuEvents.Clear());
        fireEvent(new AuthEvents.Success(profile));
    }

    @Inject
    AbstractAuthView view;

    @Inject
    AuthControllerAsync authService;

    @Inject
    Lang lang;

    private AuthEvents.Init init;
}
