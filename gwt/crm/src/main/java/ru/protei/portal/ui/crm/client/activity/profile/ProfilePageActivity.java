package ru.protei.portal.ui.crm.client.activity.profile;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.CompanySubscription;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.UserIconUtils;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CompanyService;
import ru.protei.portal.ui.common.client.service.CompanyServiceAsync;
import ru.protei.portal.ui.common.client.service.ContactServiceAsync;
import ru.protei.portal.ui.common.shared.model.Profile;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.List;
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
    public void onShow( AppEvents.ShowProfile event ) {
        initDetails.parent.clear();
        initDetails.parent.add( view.asWidget() );

        fireEvent( new ActionBarEvents.Clear() );

        fillView( policyService.getProfile() );
    }

    @Override
    public void onSaveSubscriptionClicked() {
        companyService.updateSelfCompanySubscription( view.companySubscription().getValue(), new RequestCallback<List<CompanySubscription>>() {
            @Override
            public void onError( Throwable throwable ) {}

            @Override
            public void onSuccess( List<CompanySubscription> subscriptions ) {
                policyService.getUserCompany().setSubscriptions( subscriptions );
                fireEvent( new NotifyEvents.Show( lang.companySubscriptionUpdatedSuccessful(), NotifyEvents.NotifyType.SUCCESS ) );
            }
        });
    }

    private void fillView( Profile value ) {
        view.setName( value.getFullName() );
        view.setIcon( UserIconUtils.getGenderIcon(value.getGender() ) );
        view.setRoles( value.getRoles().stream().map( UserRole::getCode ).collect( Collectors.joining(", ") ));
        if ( value.getCompany() != null ) {
            view.setCompany( value.getCompany().getCname() );
            view.companySubscription().setValue( value.getCompany().getSubscriptions() );
        }

        view.saveButtonVisibility().setVisible( policyService.hasPrivilegeFor( En_Privilege.COMMON_PROFILE_EDIT ));
    }

    @Inject
    Lang lang;
    @Inject
    AbstractProfilePageView view;
    @Inject
    CompanyServiceAsync companyService;

    @Inject
    PolicyService policyService;

    private AppEvents.InitDetails initDetails;
}
