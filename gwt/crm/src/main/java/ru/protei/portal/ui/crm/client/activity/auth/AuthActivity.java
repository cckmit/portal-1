package ru.protei.portal.ui.crm.client.activity.auth;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.ui.common.shared.model.Profile;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
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
            Element errorPanel = view.getErrorPanel();
            @Override
            public void onError( Throwable caught ) {
                errorPanel.setInnerText("Ошибка! Неправильный логин или пароль");
                errorPanel.addClassName("active");
            }

            @Override
            public void onSuccess( Profile profile ) {
                errorPanel.removeClassName("active"); // убираем ошибку если таковая была
                fireEvent(new AuthEvents.Success(profile ) );
                fireEvent( new AppEvents.Show() );
                fireEvent(new NotifyEvents.Show("Hello, darling!", NotifyEvents.NotifyType.INFO));
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
