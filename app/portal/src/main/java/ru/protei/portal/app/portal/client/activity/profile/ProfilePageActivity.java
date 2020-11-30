package ru.protei.portal.app.portal.client.activity.profile;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_Scope;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.ActionBarEvents;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.util.AvatarUtils;
import ru.protei.portal.ui.common.shared.model.Profile;

/**
 * Активность профиля
 */
public abstract class ProfilePageActivity implements Activity, AbstractProfilePageActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity( this );
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

        view.getGeneralContainer().clear();
        fireEvent(new AppEvents.ShowProfileGeneral(view.getGeneralContainer()));

        view.getSubscriptionsContainer().clear();
        if (policyService.hasSystemScopeForPrivilege(En_Privilege.COMMON_PROFILE_VIEW) ||
                policyService.hasScopeForPrivilege(En_Privilege.COMMON_PROFILE_VIEW, En_Scope.USER)) {
            fireEvent(new AppEvents.ShowProfileSubscriptions(view.getSubscriptionsContainer()));
        }

        view.selectFirstTab();
    }

    private void fillView(Profile value) {
        view.setName(value.getFullName());
        view.setIcon(AvatarUtils.getAvatarUrl(value));
        view.setCompany(value.getCompany() == null ? "" : value.getCompany().getCname());
    }

    @Inject
    AbstractProfilePageView view;
    @Inject
    PolicyService policyService;

    private AppEvents.InitDetails initDetails;
}
