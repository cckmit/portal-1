package ru.protei.portal.ui.company.client.activity.preview;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.CompanySubscription;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.struct.Pair;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.EmailRender;
import ru.protei.portal.ui.common.client.events.CompanyEvents;
import ru.protei.portal.ui.common.client.events.ContactEvents;
import ru.protei.portal.ui.common.client.events.SiteFolderPlatformEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CompanyControllerAsync;
import ru.protei.portal.ui.common.shared.model.ShortRequestCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        view.setName("#" + value.getId() + " " + value.getCname());

        String categoryImage = null;
        if ( value.getCategory() != null ) {
            categoryImage = "./images/company_" + value.getCategory().name().toLowerCase() + ".svg";
        }
        view.setCategory( categoryImage );

        PlainContactInfoFacade infoFacade = new PlainContactInfoFacade(value.getContactInfo());

        view.setPhone( infoFacade.allPhonesAsString() );

        view.setSite( infoFacade.getWebSite() );
        view.setEmail( EmailRender.renderToHtml(infoFacade.emailsStream()) );

        view.setAddressDejure( infoFacade.getLegalAddress() );
        view.setAddressFact( infoFacade.getFactAddress() );
        view.setInfo( value.getInfo() );

        requestAndFillSubscriptionEmails(value.getId());
        requestAndFillParentAndChildCompanies(value.getId());

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

    private void requestAndFillParentAndChildCompanies(Long companyId ) {
        view.setCompanyLinksMessage(null);

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

    private void requestAndFillSubscriptionEmails(Long companyId) {
        companyController.getCompanySubscription(companyId, new ShortRequestCallback<List<CompanySubscription>>()
                .setOnSuccess(subscriptions -> {

                    if (subscriptions.isEmpty()) {
                        view.setSubscriptionEmails(lang.issueCompanySubscriptionNotDefined());
                        return;
                    }

                    Map<Pair<String, String>, List<CompanySubscription>> groupsMap = fillGroupMap(subscriptions);

                    String subscriptionsHTML = null;

                    for (Map.Entry<Pair<String, String>, List<CompanySubscription>> group : groupsMap.entrySet()) {
                        if (group.getKey().equals(Pair.of(null,null))){
                            subscriptionsHTML = generateCommonSubscriptionsGroupHTML(group.getValue());
                        }
                        else {
                            subscriptionsHTML += generateSubscriptionsGroupHTML(group);
                        }
                    }

                    view.setSubscriptionEmails(subscriptionsHTML);
                }));
    }

    private String generateSubscriptionsGroupHTML(Map.Entry<Pair<String, String>, List<CompanySubscription>> group) {
        String platformName = group.getKey().getA() == null
               ? lang.companySubscriptionGroupAnyValuePlatform()
               : group.getKey().getA();

        String productName = group.getKey().getB() == null
                ? lang.companySubscriptionGroupAnyValueProduct()
                : group.getKey().getB();

        StringBuilder subscriptionsGroupHTML = new StringBuilder();

        subscriptionsGroupHTML.append("<br/>")
                .append("<b>")
                .append(lang.siteFolderPlatform())
                .append("</b> ")
                .append(platformName)
                .append(", <b>")
                .append(lang.product())
                .append("</b> ")
                .append(productName)
                .append(": ")
                .append(group.getValue().stream()
                .map(CompanySubscription::getEmail)
                .collect(Collectors.joining(", ")));
        return subscriptionsGroupHTML.toString();
    }

    private String generateCommonSubscriptionsGroupHTML(List<CompanySubscription> commonGroupList) {
        return commonGroupList.stream()
                .map(CompanySubscription::getEmail)
                .collect(Collectors.joining(", "));
    }

    private Map<Pair<String, String>, List<CompanySubscription>> fillGroupMap(List<CompanySubscription> subscriptions){

        Map<Pair<String, String>, List<CompanySubscription>> groupsMap = new HashMap<>();

        addCommonSubscriptionGroup(groupsMap);

        subscriptions.forEach(companySubscription -> {
            if (groupsMap.containsKey(Pair.of(companySubscription.getPlatformName(), companySubscription.getProductName()))){
                groupsMap.get(Pair.of(companySubscription.getPlatformName(), companySubscription.getProductName())).add (companySubscription);
            } else {
                List<CompanySubscription> newGroup = new ArrayList<>();
                newGroup.add(companySubscription);
                groupsMap.put(Pair.of(companySubscription.getPlatformName(), companySubscription.getProductName()), newGroup);
            }
        });

        return groupsMap;
    }

    private void addCommonSubscriptionGroup(Map<Pair<String, String>, List<CompanySubscription>> groupsMap) {
        groupsMap.put(Pair.of(null, null), new ArrayList<>());
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
