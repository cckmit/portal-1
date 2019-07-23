package ru.protei.portal.app.portal.client.activity.profile;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_AuthType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.CompanySubscription;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.UserIconUtils;
import ru.protei.portal.ui.common.client.events.ActionBarEvents;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.AccountControllerAsync;
import ru.protei.portal.ui.common.client.service.CompanyControllerAsync;
import ru.protei.portal.ui.common.client.widget.subscription.model.Subscription;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.portal.ui.common.shared.model.Profile;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.winter.web.common.client.events.MenuEvents;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Активность превью контакта
 */
public abstract class ProfilePageActivity implements Activity, AbstractProfilePageActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onInit(AppEvents.InitDetails event) {
        this.initDetails = event;
    }

    @Event
    public void onShow(AppEvents.ShowProfile event) {
        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());

        fireEvent(new ActionBarEvents.Clear());

        fillView(policyService.getProfile());
    }

    @Override
    public void onSaveSubscriptionClicked() {
        companyService.updateSelfCompanySubscription(view.companySubscription().getValue().stream()
                        .map(Subscription::toCompanySubscription)
                        .collect(Collectors.toList()),
                new RequestCallback<List<CompanySubscription>>() {
                    @Override
                    public void onError(Throwable throwable) {
                    }

                    @Override
                    public void onSuccess(List<CompanySubscription> subscriptions) {
                        policyService.getUserCompany().setSubscriptions(subscriptions);
                        fireEvent(new NotifyEvents.Show(lang.companySubscriptionUpdatedSuccessful(), NotifyEvents.NotifyType.SUCCESS));
                    }
                });
    }

    @Override
    public void onChangePasswordButtonClicked() {
        view.passwordContainerVisibility().setVisible(!view.passwordContainerVisibility().isVisible());
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
            accountService.updateAccountPassword(profile.getLoginId(), view.currentPassword().getValue(), view.newPassword().getValue(), new AsyncCallback<Void>() {
                @Override
                public void onFailure(Throwable caught) {
                    if (caught instanceof RequestFailedException) {
                        RequestFailedException rfe = (RequestFailedException) caught;
                        fireEvent(new NotifyEvents.Show(rfe.status == En_ResultStatus.INVALID_CURRENT_PASSWORD ? lang.errInvalidCurrentPassword() : lang.errInternalError(), NotifyEvents.NotifyType.ERROR));
                    }
                }

                @Override
                public void onSuccess(Void result) {
                    fireEvent(new AppEvents.Logout());
                    fireEvent(new MenuEvents.Clear());
                }
            });
        }
    }

    private boolean isConfirmValidate() {
        return !HelperFunc.isEmpty(view.currentPassword().getValue()) &&
                !HelperFunc.isEmpty(view.newPassword().getValue()) &&
                Objects.equals(view.newPassword().getValue(), view.confirmPassword().getValue());
    }

    private void fillView(Profile value) {
        this.profile = value;
        view.setName(value.getFullName());
        view.setIcon(UserIconUtils.getGenderIcon(value.getGender()));
        if (value.getCompany() != null) {
            view.setCompany(value.getCompany().getCname());
            view.companySubscription().setValue(value.getCompany().getSubscriptions().stream()
                    .map(Subscription::fromCompanySubscription)
                    .collect(Collectors.toList())
            );
        }

        view.companySubscriptionEnabled().setEnabled(policyService.hasPrivilegeFor(En_Privilege.COMMON_PROFILE_EDIT));
        view.saveButtonVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.COMMON_PROFILE_EDIT));
        view.changePasswordButtonVisibility().setVisible(value.getAuthType() == En_AuthType.LOCAL);
        view.passwordContainerVisibility().setVisible(false);
    }

    @Inject
    Lang lang;
    @Inject
    AbstractProfilePageView view;
    @Inject
    CompanyControllerAsync companyService;

    @Inject
    PolicyService policyService;

    @Inject
    AccountControllerAsync accountService;


    private Profile profile;
    private AppEvents.InitDetails initDetails;
}
