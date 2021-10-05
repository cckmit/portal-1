package ru.protei.portal.app.portal.client.activity.profile.general;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.app.portal.client.activity.profile.general.changepassword.AbstractChangePasswordView;
import ru.protei.portal.core.model.dict.En_AuthType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_Scope;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsActivity;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.LocalStorageService;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.AccountControllerAsync;
import ru.protei.portal.ui.common.client.util.PasswordUtils;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.Profile;

import java.util.Objects;

import static ru.protei.portal.ui.common.client.common.UiConstants.REMEMBER_ME_PREFIX;

public abstract class ProfileGeneralActivity implements AbstractProfileGeneralActivity,
        AbstractDialogDetailsActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        prepareDialog(dialogView);
    }

    @Event
    public void onShow(AppEvents.ShowProfileGeneral event) {
        event.parent.clear();
        event.parent.add(view.asWidget());
        fillView(policyService.getProfile());
    }

    @Override
    public void onChangePasswordButtonClicked() {
        dialogView.showPopup();
    }

    @Override
    public void onCancelClicked() {
        dialogView.hidePopup();
    }

    @Override
    public void onSaveClicked() {
        if (profile.getAuthType() != En_AuthType.LOCAL) {
            fireEvent(new NotifyEvents.Show(lang.errPermissionDenied(), NotifyEvents.NotifyType.ERROR));
            return;
        }

        if (!isConfirmValidate()) {
            fireEvent(new NotifyEvents.Show(lang.errEditProfile(), NotifyEvents.NotifyType.ERROR));
        } else if (!HelperFunc.isEmpty(changePasswordView.currentPassword().getValue())) {
            accountService.updateAccountPassword(profile.getLoginId(), changePasswordView.currentPassword().getValue(), changePasswordView.newPassword().getValue(), new FluentCallback<Void>()
                    .withSuccess(res -> {
                        if (storage.contains(REMEMBER_ME_PREFIX + "login")) {
                            storage.set(REMEMBER_ME_PREFIX + "pwd", PasswordUtils.encrypt(changePasswordView.newPassword().getValue()));
                        }

                        fireEvent(new NotifyEvents.Show(lang.passwordUpdatedSuccessful(), NotifyEvents.NotifyType.SUCCESS));
                        view.changePasswordButtonVisibility().setVisible(isAvailableChangePassword());
                        dialogView.hidePopup();
                    }));
        }
    }

    private void prepareDialog(AbstractDialogDetailsView dialog) {
        dialog.setActivity(this);
        dialog.getBodyContainer().clear();
        dialog.getBodyContainer().add(changePasswordView.asWidget());
        dialog.setHeader(lang.accountPasswordChange());
        dialog.removeButtonVisibility().setVisible(false);
    }

    private boolean isConfirmValidate() {
        return !HelperFunc.isEmpty(changePasswordView.currentPassword().getValue()) &&
                !HelperFunc.isEmpty(changePasswordView.newPassword().getValue()) &&
                Objects.equals(changePasswordView.newPassword().getValue(), changePasswordView.confirmPassword().getValue());
    }

    private void fillView(Profile value) {
        this.profile = value;
        view.setLogin(value.getLogin());
        view.changePasswordButtonVisibility().setVisible(isAvailableChangePassword());
        view.newEmployeeBookContainerVisibility().setVisible(isShowNewEmployeeBook());
    }

    private boolean isAvailableChangePassword() {
        return profile.getAuthType() == En_AuthType.LOCAL;
    }

    private boolean isShowNewEmployeeBook() {
        return policyService.hasSystemScopeForPrivilege(En_Privilege.COMMON_PROFILE_VIEW) ||
                policyService.hasScopeForPrivilege(En_Privilege.COMMON_PROFILE_VIEW, En_Scope.USER);
    }

    @Inject
    Lang lang;

    @Inject
    AbstractProfileGeneralView view;

    @Inject
    PolicyService policyService;

    @Inject
    AccountControllerAsync accountService;

    @Inject
    LocalStorageService storage;

    @Inject
    AbstractDialogDetailsView dialogView;

    @Inject
    AbstractChangePasswordView changePasswordView;

    private Profile profile;
}
