package ru.protei.portal.ui.contact.client.activity.preview;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.UserLoginShortView;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.UserLoginShortViewQuery;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.common.EmailRender;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.ContactEvents;
import ru.protei.portal.ui.common.client.events.ErrorPageEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.AccountControllerAsync;
import ru.protei.portal.ui.common.client.util.AvatarUtils;
import ru.protei.portal.ui.common.client.service.ContactControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static ru.protei.portal.core.model.helper.CollectionUtils.joining;

/**
 * Активность превью контакта
 */
public abstract class ContactPreviewActivity implements Activity, AbstractContactPreviewActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity( this );
    }

    @Event
    public void onInit( AppEvents.InitDetails event ) {
        this.initDetails = event;
    }

    @Event
    public void onShow( ContactEvents.ShowPreview event ) {
        event.parent.clear();
        event.parent.add( view.asWidget() );

        this.contactId = event.contact.getId();

        fillView( event.contact );
        view.showFullScreen(false);
    }

    @Event
    public void onShow( ContactEvents.ShowFullScreen event ) {
        if (!policyService.hasPrivilegeFor(En_Privilege.CONTACT_VIEW)) {
            fireEvent(new ErrorPageEvents.ShowForbidden());
            return;
        }

        initDetails.parent.clear();
        initDetails.parent.add( view.asWidget() );

        this.contactId = event.contactId;

        fillView( contactId );
        view.showFullScreen(true);
    }

    @Override
    public void onFullScreenPreviewClicked () {
        fireEvent( new ContactEvents.ShowFullScreen( contactId ) );
    }

    @Override
    public void onBackButtonClicked() {
        fireEvent(new ContactEvents.Show(true));
    }

    private void fillView( Person value ) {
        view.firedMsgVisibility().setVisible(value.isFired());
        view.deletedMsgVisibility().setVisible(value.isDeleted());
        view.setDisplayName( value.getDisplayName() );
        view.setCompany( value.getCompany().getCname() );
        String positionDisplay = StringUtils.isEmpty(value.getPosition()) ? "" : ", " + value.getPosition();
        if (!StringUtils.isEmpty( value.getDepartment() )) {
            positionDisplay += " (" + lang.department() + " " + value.getDepartment() + " )";
        }
        view.setPosition( positionDisplay );

        PlainContactInfoFacade infoFacade = new PlainContactInfoFacade(value.getContactInfo());

        view.setPhone( infoFacade.allPhonesAsString() );
        view.setEmail(EmailRender.renderToHtml(infoFacade.emailsStream()));
        view.setAddress( infoFacade.getFactAddress() );
        view.setHomeAddress( infoFacade.getHomeAddress() );
        view.setBirthday( value.getBirthday() != null ? DateFormatter.formatDateMonth(value.getBirthday()) : "" );

        requestLogins(value.getId());

        view.setGenderImage( AvatarUtils.getAvatarUrlByGender(value.getGender()));
        view.setInfo( value.getInfo() );
    }

    private void fillView( Long id ) {
        if (id == null) {
            fireEvent( new NotifyEvents.Show( lang.errIncorrectParams(), NotifyEvents.NotifyType.ERROR ) );
            return;
        }

        contactService.getContact( id, new RequestCallback<Person>() {
            @Override
            public void onError ( Throwable throwable ) {
                fireEvent( new NotifyEvents.Show( lang.errNotFound(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess ( Person value ) {
                fillView( value );
            }
        } );
    }

    private void requestLogins(Long personId) {
        UserLoginShortViewQuery accountQuery = new UserLoginShortViewQuery();
        accountQuery.setPersonIds(new HashSet<>(Collections.singleton(personId)));
        accountService.getUserLoginShortViewList(accountQuery, new FluentCallback<List<UserLoginShortView>>()
                .withSuccess(userLoginShortViews ->
                        view.setLogins(
                                joining(userLoginShortViews, ", ", UserLoginShortView::getUlogin))));
    }

    @Inject
    Lang lang;
    @Inject
    AbstractContactPreviewView view;

    @Inject
    ContactControllerAsync contactService;
    @Inject
    AccountControllerAsync accountService;

    @Inject
    PolicyService policyService;

    private Long contactId;
    private AppEvents.InitDetails initDetails;
}
