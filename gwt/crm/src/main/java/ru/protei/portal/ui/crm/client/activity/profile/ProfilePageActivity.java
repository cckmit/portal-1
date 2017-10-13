package ru.protei.portal.ui.crm.client.activity.profile;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.ContactEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CompanyService;
import ru.protei.portal.ui.common.client.service.CompanyServiceAsync;
import ru.protei.portal.ui.common.client.service.ContactServiceAsync;
import ru.protei.portal.ui.common.shared.model.Profile;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.stream.Collectors;

/**
 * Активность превью контакта
 */
public abstract class ProfilePageActivity implements Activity, AbstractProfilePageActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity( this );
    }

    @Event
    public void onInit( AppEvents.InitDetails event ) {
        this.initDetails = event;
    }

    @Event
    public void onAuthSuccess( AuthEvents.Success event ) {
        this.authEvent = event;
    }

    @Event
    public void onShow( AppEvents.ShowProfile event ) {
        initDetails.parent.clear();
        initDetails.parent.add( view.asWidget() );

        fillView( authEvent.profile );
    }

    @Override
    public void onSaveSubscriptionClicked() {
        authEvent.profile.setCompanySubscriptios( view.companySubscription().getValue() );
        companyService.updateSelfCompanySubscription( authEvent.profile.getCompanySubscriptions(), new RequestCallback<Void>() {
            @Override
            public void onError( Throwable throwable ) {}

            @Override
            public void onSuccess( Void result ) {
                fireEvent( new NotifyEvents.Show( lang.companySubscriptionUpdatedSuccessful(), NotifyEvents.NotifyType.SUCCESS ) );
            }
        });
    }

    private void fillView( Profile value ) {
        view.setName( value.getName() );
        view.setRoles( value.getRoles().stream().map( UserRole::getCode ).collect( Collectors.joining(",") ));
        view.companySubscription().setValue( value.getCompanySubscriptions() );
    }

    @Inject
    Lang lang;
    @Inject
    AbstractProfilePageView view;
    @Inject
    CompanyServiceAsync companyService;

    private AppEvents.InitDetails initDetails;
    private AuthEvents.Success authEvent;
}
