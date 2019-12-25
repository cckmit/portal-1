package ru.protei.portal.app.portal.client.activity.profile;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_AuthType;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.LocalStorageService;
import ru.protei.portal.ui.common.client.events.ActionBarEvents;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.AccountControllerAsync;
import ru.protei.portal.ui.common.client.service.AvatarUtils;
import ru.protei.portal.ui.common.client.util.PasswordUtils;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.Profile;

import java.util.Objects;

import static ru.protei.portal.ui.common.client.common.UiConstants.REMEMBER_ME_PREFIX;

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
    public void onChangePasswordButtonClicked() {
        view.passwordContainerVisibility().setVisible(!view.passwordContainerVisibility().isVisible());
        view.changePasswordButtonVisibility().setVisible(false);
    }

    @Override
    public void onSavePasswordButtonClicked() {
        if (profile.getAuthType() != En_AuthType.LOCAL) {
            fireEvent(new NotifyEvents.Show(lang.errPermissionDenied(), NotifyEvents.NotifyType.ERROR));
            return;
        }

        if (!isConfirmValidate()) {
            fireEvent(new NotifyEvents.Show(lang.errEditProfile(), NotifyEvents.NotifyType.ERROR));
        } else if (!HelperFunc.isEmpty(view.currentPassword().getValue())) {
            accountService.updateAccountPassword(profile.getLoginId(), view.currentPassword().getValue(), view.newPassword().getValue(), new FluentCallback<Void>()
                    .withSuccess(res -> {
                        if (storage.contains(REMEMBER_ME_PREFIX + "login")) {
                            storage.set(REMEMBER_ME_PREFIX + "pwd", PasswordUtils.encrypt(view.newPassword().getValue()));
                        }

                        fireEvent(new NotifyEvents.Show(lang.passwordUpdatedSuccessful(), NotifyEvents.NotifyType.SUCCESS));
                        view.changePasswordButtonVisibility().setVisible(isAvailableChangePassword());
                        view.passwordContainerVisibility().setVisible(false);
                    }));
        }
    }

    private boolean isConfirmValidate() {
        return !HelperFunc.isEmpty(view.currentPassword().getValue()) &&
                !HelperFunc.isEmpty(view.newPassword().getValue()) &&
                Objects.equals(view.newPassword().getValue(), view.confirmPassword().getValue());
    }

    private void fillView(Profile value ) {
        this.profile = value;
        view.setName( value.getFullName() );
        view.setIcon( AvatarUtils.getAvatarUrl(value) );
        view.setCompany( value.getCompany() == null ? "" : value.getCompany().getCname() );
        view.changePasswordButtonVisibility().setVisible(isAvailableChangePassword());
        view.passwordContainerVisibility().setVisible(false);
    }

    private boolean isAvailableChangePassword() {
        return profile.getAuthType() == En_AuthType.LOCAL;
    }

    @Inject
    Lang lang;
    @Inject
    AbstractProfilePageView view;
    @Inject
    PolicyService policyService;

    @Inject
    AccountControllerAsync accountService;

    @Inject
    LocalStorageService storage;

    private Profile profile;
    private AppEvents.InitDetails initDetails;
}
