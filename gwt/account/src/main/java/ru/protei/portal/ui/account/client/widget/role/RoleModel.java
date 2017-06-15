package ru.protei.portal.ui.account.client.widget.role;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.RoleEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.AccountServiceAsync;
import ru.protei.portal.ui.common.client.widget.optionlist.base.ModelList;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.ArrayList;
import java.util.List;

public abstract class RoleModel implements Activity {

    @Event
    public void onInit( AuthEvents.Success event ) {
        refreshOptions();
    }

    @Event
    public void onRoleListChanged( RoleEvents.ChangeModel event ) {
        refreshOptions();
    }

    public void subscribe( ModelList< UserRole > list ) {
        subscribers.add( list );
        list.fillOptions( this.list );
    }

    private void notifySubscribers() {
        for ( ModelList< UserRole > list : subscribers ) {
            list.fillOptions( this.list );
        }
    }

    private void refreshOptions() {

        accountService.getRoles( new RequestCallback< List< UserRole > >() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess( List< UserRole > options ) {
                list.clear();
                list.addAll( options );

                notifySubscribers();
            }
        } );
    }

    @Inject
    AccountServiceAsync accountService;

    @Inject
    Lang lang;

    private List< UserRole > list = new ArrayList<>();

    List< ModelList< UserRole > > subscribers = new ArrayList<>();
}
