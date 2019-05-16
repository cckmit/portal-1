package ru.protei.portal.ui.sitefolder.client.activity.server.table;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.ent.Server;
import ru.protei.portal.core.model.query.ServerQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerActivity;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.SiteFolderControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.sitefolder.client.activity.server.filter.AbstractServerFilterActivity;
import ru.protei.portal.ui.sitefolder.client.activity.server.filter.AbstractServerFilterView;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class ServerTableActivity implements
        AbstractServerTableActivity, AbstractServerFilterActivity,
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
    public void onShow(SiteFolderServerEvents.Show event) {
        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());
        view.getPagerContainer().add(pagerView.asWidget());

        platformId = event.platformId;

        fireEvent(new ActionBarEvents.Clear());
        if (policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_CREATE)) {
            fireEvent(new ActionBarEvents.Add(lang.siteFolderServerCreate(), UiConstants.ActionBarIcons.CREATE, UiConstants.ActionBarIdentity.SITE_FOLDER_SERVER));
        }

        if (platformId != null) {
            Set<EntityOption> options = new HashSet<>();
            EntityOption option = new EntityOption();
            option.setId(platformId);
            options.add(option);
            filterView.platforms().setValue(options);
        }

        loadTable();
    }

    @Event
    public void onCreateClicked(ActionBarEvents.Clicked event) {
        if (!UiConstants.ActionBarIdentity.SITE_FOLDER_SERVER.equals(event.identity)) {
            return;
        }

        if (!policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_CREATE)) {
            return;
        }

        fireEvent(new SiteFolderServerEvents.Edit());
    }

    @Event
    public void onServerChanged(SiteFolderServerEvents.Changed event) {
        view.updateRow(event.server);
    }

    @Event
    public void onServerConfirmRemove(ConfirmDialogEvents.Confirm event) {
        if (!event.identity.equals(getClass().getName())) {
            return;
        }

        if (serverIdForRemove == null) {
            return;
        }

        siteFolderController.removeServer(serverIdForRemove, new RequestCallback<Boolean>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.siteFolderServerNotRemoved(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(Boolean result) {
                serverIdForRemove = null;
                if (result) {
                    fireEvent(new SiteFolderServerEvents.ChangeModel());
                    fireEvent(new SiteFolderServerEvents.Show(platformId));
                    fireEvent(new NotifyEvents.Show(lang.siteFolderServerRemoved(), NotifyEvents.NotifyType.SUCCESS));
                } else {
                    fireEvent(new NotifyEvents.Show(lang.siteFolderServerNotRemoved(), NotifyEvents.NotifyType.ERROR));
                }
            }
        });
    }

    @Event
    public void onServerCancelRemove(ConfirmDialogEvents.Cancel event) {
        if (!event.identity.equals(getClass().getName())) {
            return;
        }
        serverIdForRemove = null;
    }

    @Override
    public void onItemClicked(Server value) {
        if (value == null) {
            animation.closeDetails();
        } else {
            animation.showDetails();
            fireEvent(new SiteFolderServerEvents.ShowPreview(view.getPreviewContainer(), value));
        }
    }

    @Override
    public void onCopyClicked(Server value) {
        if (!policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_CREATE)) {
            return;
        }

        if (value == null) {
            return;
        }

        fireEvent(SiteFolderServerEvents.Edit.withClone(value.getId()));
    }

    @Override
    public void onEditClicked(Server value) {
        if (!policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_EDIT)) {
            return;
        }

        if (value == null) {
            return;
        }

        fireEvent(new SiteFolderServerEvents.Edit(value.getId()));
    }

    @Override
    public void onRemoveClicked(Server value) {
        if (!policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_REMOVE)) {
            return;
        }

        if (value == null) {
            return;
        }

        serverIdForRemove = value.getId();
        fireEvent(new ConfirmDialogEvents.Show(getClass().getName(), lang.siteFolderServerConfirmRemove()));
    }

    @Override
    public void onOpenAppsClicked(Server value) {
        if (value != null) {
            fireEvent(new SiteFolderAppEvents.Show(value.getId()));
        }
    }

    @Override
    public void loadData(int offset, int limit, AsyncCallback<List<Server>> asyncCallback) {
        boolean isFirstChunk = offset == 0;
        ServerQuery query = getQuery();
        query.setOffset(offset);
        query.setLimit(limit);
        siteFolderController.getServers(query, new FluentCallback<SearchResult<Server>>()
                .withError(throwable -> {
                    fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                    asyncCallback.onFailure(throwable);
                })
                .withSuccess(sr -> {
                    asyncCallback.onSuccess(sr.getResults());
                    if (isFirstChunk) {
                        view.setTotalRecords(sr.getTotalCount());
                        pagerView.setTotalPages(view.getPageCount());
                        pagerView.setTotalCount(sr.getTotalCount());
                    }
                }));
    }

    @Override
    public void onPageChanged(int page) {
        pagerView.setCurrentPage(page);
    }

    @Override
    public void onPageSelected(int page) {
        view.scrollTo(page);
    }

    @Override
    public void onFilterChanged() {
        loadTable();
    }

    private void loadTable() {
        animation.closeDetails();
        view.clearRecords();
        view.triggerTableLoad();
    }

    private ServerQuery getQuery() {
        ServerQuery query = new ServerQuery();
        query.setSearchString(filterView.name().getValue());
        query.setSortField(filterView.sortField().getValue());
        query.setSortDir(filterView.sortDir().getValue() ? En_SortDir.ASC : En_SortDir.DESC);
        query.setCompanyIds(filterView.companies().getValue() == null
                ? null
                : filterView.companies().getValue().stream()
                .map(EntityOption::getId)
                .collect(Collectors.toList())
        );
        query.setPlatformIds(filterView.platforms().getValue() == null
                ? null
                : filterView.platforms().getValue().stream()
                .map(EntityOption::getId)
                .collect(Collectors.toList())
        );
        query.setIp(filterView.ip().getValue());
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
    AbstractServerTableView view;
    @Inject
    AbstractServerFilterView filterView;
    @Inject
    AbstractPagerView pagerView;

    private Long platformId = null;
    private Long serverIdForRemove = null;
    private AppEvents.InitDetails initDetails;
}
