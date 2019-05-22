package ru.protei.portal.ui.company.client.activity.preview;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.CompanySubscription;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.CompanyEvents;
import ru.protei.portal.ui.common.client.events.ContactEvents;
import ru.protei.portal.ui.common.client.events.SiteFolderPlatformEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CompanyControllerAsync;
import ru.protei.portal.ui.common.shared.model.ShortRequestCallback;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Активность превью компании
 */
public abstract class CompanyPreviewActivity
        implements Activity,
        AbstractCompanyPreviewActivity
{
    @PostConstruct
    public void onInit() {
        view.setActivity( this );
    }

    @Event
    public void onShow( CompanyEvents.ShowPreview event ) {
        event.parent.clear();
        event.parent.add(view.asWidget(event.isShouldWrap));

        fillView( event.company );
        view.watchForScroll( event.isWatchForScroll);
    }

    private void fillView( Company value ) {

        if ( value.getCompanyGroup() == null ) {
            view.setGroupVisible( false );
        }

        view.setName(value.getCname());

        view.setCategory( value.getCategory() == null ? "" : value.getCategory().getName() );
        view.setParentCompany(  value.getParentCompanyName() );

        PlainContactInfoFacade infoFacade = new PlainContactInfoFacade(value.getContactInfo());

        view.setPhone( infoFacade.allPhonesAsString() );

        view.setSite( infoFacade.getWebSite() );
        view.setEmail( infoFacade.allEmailsAsString() );

        view.setAddressDejure( infoFacade.getLegalAddress() );
        view.setAddressFact( infoFacade.getFactAddress() );
        view.setInfo( value.getInfo() );

        requestSubscriptionEmails(value.getId());
        requestParentAndChildCompanies(value.getId());

        if (policyService.hasPrivilegeFor(En_Privilege.CONTACT_VIEW)) {
            fireEvent(new ContactEvents.ShowConciseTable(view.getContactsContainer(), value.getId()).readOnly());
        }
        if (policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_VIEW)) {
            fireEvent(new SiteFolderPlatformEvents.ShowConciseTable(view.getSiteFolderContainer(), value.getId()));
        }
    }

    private void requestParentAndChildCompanies( Long parentCompanyId ) {
        companyController.getCompany( parentCompanyId, new ShortRequestCallback<Company>()
                .setOnSuccess( company-> {
                    view.setParentCompany( company.getParentCompanyName() );
                    view.setChildrenCompanies(  CollectionUtils.stream( company.getChildCompanies() ).map( Company::getCname).collect( Collectors.joining(", ")) );
                } ) );

    }

    private void requestSubscriptionEmails(Long companyId) {
        companyController.getCompanySubscription(companyId, new ShortRequestCallback<List<CompanySubscription>>()
                .setOnSuccess(subscriptions -> {
                    String subscriptionsStr =  lang.issueCompanySubscriptionNotDefined();
                    if (!CollectionUtils.isEmpty(subscriptions) ) {
                        subscriptionsStr = subscriptions.stream()
                                .map(CompanySubscription::getEmail)
                                .collect(Collectors.joining(", "));
                    }
                    view.setSubscriptionEmails(subscriptionsStr);
                }));

    }

    @Inject
    Lang lang;
    @Inject
    AbstractCompanyPreviewView view;
    @Inject
    CompanyControllerAsync companyController;
    @Inject
    PolicyService policyService;
}
