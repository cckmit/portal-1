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
import ru.protei.winter.core.utils.beans.SearchResult;

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

        query = makeQuery(event.companyId);

        request();
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

        fireEvent(new ConfirmDialogEvents.Show(lang.siteFolderPlatformConfirmRemove(), removeAction(value.getId())));
    }

    private PlatformQuery makeQuery(Long companyId) {
        return new PlatformQuery(Arrays.asList(companyId));
    }

    private void request() {
        view.clearRecords();

        platformController.getPlatforms(query, new RequestCallback<SearchResult<Platform>>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(SearchResult<Platform> result) {
                view.setData(result.getResults());
            }
        });
    }

    private Runnable removeAction(Long platformId) {
        return () -> platformController.removePlatform(platformId, new RequestCallback<Long>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.siteFolderPlatformNotRemoved(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(Long result) {
                fireEvent(new CompanyEvents.Show());
                fireEvent(new NotifyEvents.Show(lang.siteFolderPlatformRemoved(), NotifyEvents.NotifyType.SUCCESS));
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

    private PlatformQuery query;
}
