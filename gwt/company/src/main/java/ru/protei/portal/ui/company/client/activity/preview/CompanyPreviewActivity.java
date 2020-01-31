package ru.protei.portal.ui.company.client.activity.preview;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.CompanySubscription;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.EmailRender;
import ru.protei.portal.ui.common.client.events.CompanyEvents;
import ru.protei.portal.ui.common.client.events.ContactEvents;
import ru.protei.portal.ui.common.client.events.SiteFolderPlatformEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CompanyControllerAsync;
import ru.protei.portal.ui.common.shared.model.ShortRequestCallback;

import java.util.HashSet;
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
    }

    private void fillView( Company value ) {
        view.setId("ID-" + value.getId());
        view.setName(value.getCname());

        String categoryImage = null;
        if ( value.getCategory() != null ) {
            En_CompanyCategory enCategory = En_CompanyCategory.findById(value.getCategory().getId());
            categoryImage = "./images/company_" + enCategory.name().toLowerCase() + ".svg";
        }
        view.setCategory( categoryImage );

        PlainContactInfoFacade infoFacade = new PlainContactInfoFacade(value.getContactInfo());

        view.setPhone( infoFacade.allPhonesAsString() );

        view.setSite( infoFacade.getWebSite() );
        view.setEmail( EmailRender.renderToHtml(infoFacade.emailsStream()) );

        view.setAddressDejure( infoFacade.getLegalAddress() );
        view.setAddressFact( infoFacade.getFactAddress() );
        view.setInfo( value.getInfo() );

        requestSubscriptionEmails(value.getId());
        requestParentAndChildCompanies(value.getId());

        view.getContactsContainer().clear();
        if (policyService.hasPrivilegeFor(En_Privilege.CONTACT_VIEW)) {
            fireEvent(new ContactEvents.ShowConciseTable(view.getContactsContainer(), value.getId()).readOnly());
            view.getContactsContainerVisibility().setVisible(true);
        } else {
            view.getContactsContainerVisibility().setVisible(false);
        }

        view.getSiteFolderContainer().clear();
        if (policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_VIEW)) {
            fireEvent(new SiteFolderPlatformEvents.ShowConciseTable(view.getSiteFolderContainer(), value.getId()));
            view.getSiteFolderContainerVisibility().setVisible(true);
        } else {
            view.getSiteFolderContainerVisibility().setVisible(false);
        }
    }

    private void requestParentAndChildCompanies( Long companyId ) {
        companyController.getCompany( companyId, new ShortRequestCallback<Company>()
                .setOnSuccess( company-> {

                    if (StringUtils.isNotEmpty( company.getParentCompanyName()) ) {
                        view.setCompanyLinksMessage( lang.companyIsAPartOfCompany( company.getParentCompanyName() ));
                    }

                    if ( CollectionUtils.isNotEmpty(company.getChildCompanies() )) {
                        String companyNames = CollectionUtils.stream(company.getChildCompanies()).map(Company::getCname).collect(Collectors.joining(", "));
                        view.setCompanyLinksMessage( lang.companyIsAHeadOfCompany( companyNames ));
                    }
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
