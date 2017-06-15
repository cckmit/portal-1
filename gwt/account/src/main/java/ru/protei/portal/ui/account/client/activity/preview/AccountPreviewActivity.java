package ru.protei.portal.ui.account.client.activity.preview;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.UserLogin;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.ui.common.client.events.AccountEvents;

import java.util.stream.Collectors;

public abstract class AccountPreviewActivity implements AbstractAccountPreviewActivity, Activity {
    @PostConstruct
    public void onInit() {
        view.setActivity( this );
    }

    @Event
    public void onShow( AccountEvents.ShowPreview event ) {
        event.parent.clear();
        event.parent.add( view.asWidget() );

        fillView( event.account );
    }

    private void fillView( UserLogin value ) {

        view.setLogin( value.getUlogin() );
        view.setLastName( value.getPerson() == null ? "" : value.getPerson().getLastName() );
        view.setFirstName( value.getPerson() == null ? "" : value.getPerson().getFirstName() );
        view.setSecondName( value.getPerson() == null ? "" : value.getPerson().getSecondName() );
        view.setCompany( value.getPerson() == null ? "" : value.getPerson().getCompany().getCname() );
        if( value.getRoles() != null ) {
            view.setRoles( value.getRoles().stream().map( UserRole::getCode ).collect( Collectors.joining(", ")) );
        }
    }

    @Inject
    AbstractAccountPreviewView view;
}
