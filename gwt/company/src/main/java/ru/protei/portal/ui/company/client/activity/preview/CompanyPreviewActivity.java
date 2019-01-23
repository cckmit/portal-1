package ru.protei.portal.ui.company.client.activity.preview;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.CompanySubscription;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.ui.common.client.events.CompanyEvents;
import ru.protei.portal.ui.common.client.events.ContactEvents;
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
        if(value.getParentCompanyId()!=null)
        requestParentCompanynamr(value.getParentCompanyId());

        PlainContactInfoFacade infoFacade = new PlainContactInfoFacade(value.getContactInfo());

        view.setPhone( infoFacade.allPhonesAsString() );

        view.setSite( infoFacade.getWebSite() );
        view.setEmail( infoFacade.allEmailsAsString() );

        view.setAddressDejure( infoFacade.getLegalAddress() );
        view.setAddressFact( infoFacade.getFactAddress() );
        view.setInfo( value.getInfo() );

        requestSubscriptionEmails(value.getId());

        fireEvent( new ContactEvents.ShowConciseTable(view.getContactsContainer(), value.getId()).readOnly() );
    }

    private void requestParentCompanynamr( Long parentCompanyId ) {
        companyController.getCompanyName( parentCompanyId, new ShortRequestCallback<String>()
                .setOnSuccess( view::setParentCompany ) );

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
}
