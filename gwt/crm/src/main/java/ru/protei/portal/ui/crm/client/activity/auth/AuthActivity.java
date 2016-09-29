package ru.protei.portal.ui.crm.client.activity.auth;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.ui.common.shared.model.Profile;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.crm.client.service.AuthServiceAsync;

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
                fireEvent( new AuthEvents.Show() );
            }
        } );
    }

    public void onLoginClicked() {
        authService.authentificate( view.getUserName(), view.getPassword(), new RequestCallback< Profile >() {
            @Override
            public void onError( Throwable caught ) {
            }

            @Override
            public void onSuccess( Profile profile ) {
                fireEvent( new AuthEvents.Success( profile ) );
                fireEvent( new AppEvents.Show() );
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

                fireEvent( new AuthEvents.Success( profile ) );
            }
        });
    }

    private void placeView() {
        init.parent.clear();
        init.parent.add( view.asWidget() );

        view.setFocus();
    }

    @Inject
    AbstractAuthView view;

    @Inject
    AuthServiceAsync authService;

    private AuthEvents.Init init;
}
