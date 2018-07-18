package ru.protei.portal.ui.sitefolder.client.activity.plaform.table;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.ent.Platform;
import ru.protei.portal.core.model.query.PlatformQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerActivity;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.SiteFolderControllerAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.sitefolder.client.activity.plaform.filter.AbstractPlatformFilterActivity;
import ru.protei.portal.ui.sitefolder.client.activity.plaform.filter.AbstractPlatformFilterView;

import java.util.List;
import java.util.stream.Collectors;

public abstract class PlatformTableActivity implements
        AbstractPlatformTableActivity, AbstractPlatformFilterActivity,
        AbstractPagerActivity, Activity
{

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        view.setAnimation(animation);

        pagerView.setActivity(this);

        filterView.setActivity(this);
        view.getFilterContainer().add(filterView.asWidget());
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
    }

    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        filterView.resetFilter();
    }

    @Event
    public void onShow(SiteFolderPlatformEvents.Show event) {
        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());
        initDetails.parent.add(pagerView.asWidget());

        fireEvent(new ActionBarEvents.Clear());
        if (policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_CREATE)) {
            fireEvent(new ActionBarEvents.Add(lang.siteFolderPlatformCreate(), UiConstants.ActionBarIcons.CREATE, UiConstants.ActionBarIdentity.SITE_FOLDER_PLATFORM));
        }

        requestPlatformsCount();
    }

    @Event
    public void onCreateClicked(ActionBarEvents.Clicked event) {
        if (!UiConstants.ActionBarIdentity.SITE_FOLDER_PLATFORM.equals(event.identity)) {
            return;
        }

        if (!policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_CREATE)) {
            return;
        }

        fireEvent(new SiteFolderPlatformEvents.Edit());
    }

    @Event
    public void onPlatformChanged(SiteFolderPlatformEvents.Changed event) {
        view.updateRow(event.platform);
    }

    @Event
    public void onPlatformConfirmRemove(ConfirmDialogEvents.Confirm event) {
        if (!event.identity.equals(getClass().getName())) {
            return;
        }

        if (platformIdForRemove == null) {
            return;
        }

        siteFolderController.removePlatform(platformIdForRemove, new RequestCallback<Boolean>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.siteFolderPlatformNotRemoved(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(Boolean result) {
                platformIdForRemove = null;
                if (result) {
                    fireEvent(new SiteFolderPlatformEvents.Show());
                    fireEvent(new NotifyEvents.Show(lang.siteFolderPlatformRemoved(), NotifyEvents.NotifyType.SUCCESS));
                } else {
                    fireEvent(new NotifyEvents.Show(lang.siteFolderPlatformNotRemoved(), NotifyEvents.NotifyType.ERROR));
                }
            }
        });
    }

    @Event
    public void onPlatformCancelRemove(ConfirmDialogEvents.Cancel event) {
        if (!event.identity.equals(getClass().getName())) {
            return;
        }
        platformIdForRemove = null;
    }

    @Override
    public void onItemClicked(Platform value) {
        if (value == null) {
            animation.closeDetails();
        } else {
            animation.showDetails();
            fireEvent(new SiteFolderPlatformEvents.ShowPreview(view.getPreviewContainer(), value));
        }
    }

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

        platformIdForRemove = value.getId();
        fireEvent(new ConfirmDialogEvents.Show(getClass().getName(), lang.siteFolderPlatformConfirmRemove()));
    }

    @Override
    public void loadData(int offset, int limit, AsyncCallback<List<Platform>> asyncCallback) {
        PlatformQuery query = getQuery();
        query.setOffset(offset);
        query.setLimit(limit);
        siteFolderController.getPlatforms(query, new RequestCallback<List<Platform>>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                asyncCallback.onFailure(throwable);
            }

            @Override
            public void onSuccess(List<Platform> result) {
                asyncCallback.onSuccess(result);
            }
        });
    }

    @Override
    public void onPageChanged(int page) {
        pagerView.setCurrentPage(page + 1);
    }

    @Override
    public void onFirstClicked() {
        view.scrollTo(0);
    }

    @Override
    public void onLastClicked() {
        view.scrollTo(view.getPageCount() - 1);
    }

    @Override
    public void onFilterChanged() {
        requestPlatformsCount();
    }

    private void requestPlatformsCount() {
        view.clearRecords();
        animation.closeDetails();
        siteFolderController.getPlatformsCount(getQuery(), new RequestCallback<Long>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(Long result) {
                view.setPlatformsCount(result);
                pagerView.setTotalPages(view.getPageCount());
                pagerView.setTotalCount(result);
            }
        });
    }

    private PlatformQuery getQuery() {
        PlatformQuery query = new PlatformQuery();
        query.setSearchString(filterView.name().getValue());
        query.setSortField(filterView.sortField().getValue());
        query.setSortDir(filterView.sortDir().getValue() ? En_SortDir.ASC : En_SortDir.DESC);
        query.setCompanyIds(filterView.companies().getValue() == null
                ? null
                : filterView.companies().getValue().stream()
                        .map(EntityOption::getId)
                        .collect(Collectors.toList())
        );
        query.setParams(filterView.parameters().getValue());
        query.setComment(filterView.comment().getValue());
        return query;
    }

    @Inject
    PolicyService policyService;
    @Inject
    SiteFolderControllerAsync siteFolderController;
    @Inject
    Lang lang;
    @Inject
    TableAnimation animation;
    @Inject
    AbstractPlatformTableView view;
    @Inject
    AbstractPlatformFilterView filterView;
    @Inject
    AbstractPagerView pagerView;

    private Long platformIdForRemove = null;
    private AppEvents.InitDetails initDetails;
}
