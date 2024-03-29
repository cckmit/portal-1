package ru.protei.portal.ui.sitefolder.client.activity.server.summarytable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.ent.Platform;
import ru.protei.portal.core.model.ent.Server;
import ru.protei.portal.core.model.ent.ServerGroup;
import ru.protei.portal.core.model.query.ServerQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PlatformOption;
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
import java.util.Set;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.nullsLast;
import static ru.protei.portal.ui.common.client.util.PaginationUtils.PAGE_SIZE;
import static ru.protei.portal.ui.common.client.util.PaginationUtils.getTotalPages;

public abstract class ServerSummaryTableActivity implements
        AbstractServerSummaryTableActivity, AbstractServerFilterActivity,
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

    @Event(Type.FILL_CONTENT)
    public void onShow(SiteFolderServerEvents.ShowSummaryTable event) {
        if (!policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_VIEW)) {
            fireEvent(new ErrorPageEvents.ShowForbidden());
            return;
        }

        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());
        view.getPagerContainer().add(pagerView.asWidget());

        fireEvent(new ActionBarEvents.Clear());
        if (policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_CREATE)) {
            fireEvent(new ActionBarEvents.Add(lang.siteFolderServerCreate(), null, UiConstants.ActionBarIdentity.SITE_FOLDER_SERVER));
        }

        platformId = event.platformId;
        this.preScroll = event.preScroll;

        if (platformId != null) {
            requestPlatformAndLoadTable(this.page);
        }
    }


    @Event
    public void onCreateClicked(ActionBarEvents.Clicked event) {
        if (!UiConstants.ActionBarIdentity.SITE_FOLDER_SERVER.equals(event.identity)) {
            return;
        }

        if (!policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_CREATE)) {
            return;
        }

        view.clearSelection();

        fireEvent(new SiteFolderServerEvents.Edit());
    }

    @Event
    public void onServerChanged(SiteFolderServerEvents.Changed event) {
        view.updateRow(event.server);
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

        fireEvent(SiteFolderServerEvents.Edit.withClone(value.getId()).withBackEvent(
                () -> fireEvent(new SiteFolderServerEvents.ShowSummaryTable(value.getPlatformId(), true)))
        );
    }

    @Override
    public void onEditClicked(Server value) {
        persistScroll();

        if (!policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_EDIT)) {
            return;
        }

        if (value == null) {
            return;
        }

        fireEvent(new SiteFolderServerEvents.Edit(value.getId()).withBackEvent(() -> fireEvent(new SiteFolderServerEvents.ShowSummaryTable(value.getPlatformId(), true))));
    }

    @Override
    public void onRemoveClicked(Server value) {
        if (!policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_REMOVE)) {
            return;
        }

        if (value == null) {
            return;
        }

        fireEvent(new ConfirmDialogEvents.Show(lang.siteFolderServerConfirmRemove(), removeAction(value.getId())));
    }

    @Override
    public void onOpenAppsClicked(Server value) {
        if (value != null) {
            fireEvent(new SiteFolderAppEvents.Show(makeEntityOption(value), false));
        }
    }

    @Override
    public void onPageSelected(int page) {
        this.page = page;
        requestServers(page);
    }

    @Override
    public void onFilterChanged() {
        reloadTable(0);
    }

    @Override
    public ServerGroup makeGroup(Server server) {
        return makeServerGroup(server);
    }

    @Override
    public String makeGroupName(ServerGroup group) {
        return group == null ? lang.siteFolderServerGroupWithoutGroup() : group.getName();
    }

    private void requestServers(int page) {
        boolean isFirstChunk = page == 0;
        ServerQuery query = getQuery();
        query.setOffset(page * PAGE_SIZE);
        query.setLimit(PAGE_SIZE);
        siteFolderController.getServers(query, new FluentCallback<SearchResult<Server>>()
                .withSuccess(sr -> {
                    if (isFirstChunk) {
                        pagerView.setTotalPages(getTotalPages( sr.getTotalCount() ));
                        pagerView.setTotalCount(sr.getTotalCount());
                        restoreScroll();
                    }

                    pagerView.setCurrentPage( page );
                    view.addRecords(nullsLast(sr.getResults(), Server::getServerGroupId));
                }));
    }

    private EntityOption makeEntityOption(Server server) {
        return server == null ? null : new EntityOption(server.getName(), server.getId());
    }

    private void requestPlatformAndLoadTable(int page) {
        Set<PlatformOption> options = new HashSet<>();
        PlatformOption option = new PlatformOption();
        siteFolderController.getPlatform(platformId, new FluentCallback<Platform>()
                .withError(throwable -> {
                    option.setId(platformId);
                    options.add(option);
                    filterView.platforms().setValue(options);
                    reloadTable(page);
                    fireEvent(new NotifyEvents.Show(lang.siteFolderPlatformRequestError(), NotifyEvents.NotifyType.ERROR));
                })
                .withSuccess(platform -> {
                    option.setId(platformId);
                    option.setDisplayText(platform.getName());
                    options.add(option);
                    filterView.platforms().setValue(options);
                    reloadTable(page);
                }));
    }

    private void persistScroll() {
        scrollTo = Window.getScrollTop();
    }

    private void restoreScroll() {
        if (!preScroll) {
            view.clearSelection();
            return;
        }

        Window.scrollTo(0, scrollTo);
        preScroll = false;
        scrollTo = 0;
    }

    private void reloadTable(int page) {
        animation.closeDetails();
        view.clearRecords();
        requestServers(page);
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
                .map(PlatformOption::getId)
                .collect(Collectors.toList())
        );
        query.setIp(filterView.ip().getValue());
        query.setParams(filterView.parameters().getValue());
        query.setComment(filterView.comment().getValue());
        return query;
    }

    private Runnable removeAction(Long serverId) {
        return () -> siteFolderController.removeServer(serverId, new RequestCallback<Long>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.siteFolderServerNotRemoved(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(Long result) {
                fireEvent(new SiteFolderServerEvents.ChangeModel());
                fireEvent(new SiteFolderServerEvents.ShowSummaryTable(platformId, false));
                fireEvent(new NotifyEvents.Show(lang.siteFolderServerRemoved(), NotifyEvents.NotifyType.SUCCESS));
            }
        });
    }

    private ServerGroup makeServerGroup(Server server) {
        if (server.getServerGroupId() == null) {
            return null;
        }

        ServerGroup serverGroup = new ServerGroup();

        serverGroup.setId(server.getServerGroupId());
        serverGroup.setName(server.getServerGroupName());
        serverGroup.setPlatformId(server.getPlatformId());

        return serverGroup;
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
    AbstractServerSummaryTableView view;
    @Inject
    AbstractServerFilterView filterView;
    @Inject
    AbstractPagerView pagerView;

    private Long platformId = null;
    private AppEvents.InitDetails initDetails;

    private Integer scrollTo = 0;
    private Boolean preScroll = false;

    private int page = 0;
}
