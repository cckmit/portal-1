package ru.protei.portal.ui.crm.client.activity.auth;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.ui.crm.client.events.AuthEvents;

/**
 * Created by turik on 23.09.16.
 */
public abstract class AuthActivity implements AbstractAuthActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity (this);
    }

    @Event
    public void onShow( AuthEvents.Show event ) {
        event.parent.add ( view.asWidget () );
        view.setFocus();
    }

    public void onLoginClicked() {
        this.fireEvent (new AuthEvents.Success (view.getUserName ()));
    }

    /*@Event
    public void onSuccess(AuthEvents.Success event) {
        Window.alert ( "Success login with user name '" + view.getUserName () + "'");
    }*/

    public void onResetClicked() {

    }

    @Inject
    AbstractAuthView view;

}
