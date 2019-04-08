package ru.protei.portal.ui.sitefolder.client.activity.plaform.table.concise;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Platform;
import ru.protei.portal.core.model.query.PlatformQuery;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.CompanyEvents;
import ru.protei.portal.ui.common.client.events.ConfirmDialogEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.SiteFolderPlatformEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.SiteFolderControllerAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.Arrays;
import java.util.List;

public abstract class PlatformConciseTableActivity implements AbstractPlatformConciseTableActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onShow(SiteFolderPlatformEvents.ShowConciseTable event) {
        event.parent.clear();
        event.parent.add(view.asWidget());

        platformId = null;
        query = makeQuery(event.companyId);

        request();
    }

    @Event
    public void onConfirmRemove(ConfirmDialogEvents.Confirm event) {
        if (!event.identity.equals(getClass().getName())) {
            return;
        }

        if (platformId == null) {
            return;
        }

        platformController.removePlatform(platformId, new RequestCallback<Boolean>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.siteFolderPlatformNotRemoved(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(Boolean result) {
                platformId = null;
                if (result) {
                    fireEvent(new CompanyEvents.Show());
                    fireEvent(new NotifyEvents.Show(lang.siteFolderPlatformRemoved(), NotifyEvents.NotifyType.SUCCESS));
                } else {
                    fireEvent(new NotifyEvents.Show(lang.siteFolderPlatformNotRemoved(), NotifyEvents.NotifyType.ERROR));
                }
            }
        });
    }

    @Event
    public void onCancelRemove(ConfirmDialogEvents.Cancel event) {
        if (!event.identity.equals(getClass().getName())) {
            return;
        }
        platformId = null;
    }

    @Override
    public void onItemClicked(Platform value) {}

    @Override
    public void onEditClicked(Platform value) {
        if (!policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_EDIT)) {
            return;
        }

        if (value == null) {
            return;
        }

        fireEvent(new SiteFolderPlatformEvents.Edit(value.getId()));
    }

    @Override
    public void onRemoveClicked(Platform value) {
        if (!policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_REMOVE)) {
            return;
        }

        if (value == null) {
            return;
        }

        platformId = value.getId();
        fireEvent(new ConfirmDialogEvents.Show(getClass().getName(), lang.siteFolderPlatformConfirmRemove()));
    }

    private PlatformQuery makeQuery(Long companyId) {
        return new PlatformQuery(Arrays.asList(companyId));
    }

    private void request() {
        view.clearRecords();

        platformController.getPlatforms(query, new RequestCallback<List<Platform>>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(List<Platform> result) {
                view.setData(result);
            }
        });
    }

    @Inject
    Lang lang;
    @Inject
    AbstractPlatformConciseTableView view;
    @Inject
    SiteFolderControllerAsync platformController;
    @Inject
    PolicyService policyService;

    private Long platformId = null;
    private PlatformQuery query;
}
