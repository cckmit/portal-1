package ru.protei.portal.ui.account.client.activity.preview;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_AuthType;
import ru.protei.portal.core.model.ent.UserLogin;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.ui.common.client.events.AccountEvents;
import ru.protei.portal.ui.common.client.lang.Lang;

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

        String personInfo = "";
        if (value.getPerson() != null) {
            personInfo += value.getPerson().getDisplayShortName();
            if ( value.getPerson().getCompany() != null ) {
                personInfo += " (" + value.getPerson().getCompany().getCname() + ")";
            }
        }
        view.setPersonInfo(personInfo);

        String roles = lang.accountRolesNotFound();
        if (value.getRoles() != null) {
            roles = value.getRoles().stream().map(UserRole::getCode).collect(Collectors.joining(", "));
        }
        view.setRoles(roles);

        En_AuthType type = En_AuthType.find(value.getAuthTypeId());
        view.setTypeImage( type == null ? null : type.getImageSrc() );
    }

    @Inject
    Lang lang;
    @Inject
    AbstractAccountPreviewView view;
}
