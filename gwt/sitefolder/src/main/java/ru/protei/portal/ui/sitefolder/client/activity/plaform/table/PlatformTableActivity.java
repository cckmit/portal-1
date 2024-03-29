package ru.protei.portal.ui.sitefolder.client.activity.plaform.table;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.ent.Platform;
import ru.protei.portal.core.model.query.PlatformQuery;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
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
import ru.protei.portal.ui.sitefolder.client.activity.plaform.filter.AbstractPlatformFilterActivity;
import ru.protei.portal.ui.sitefolder.client.activity.plaform.filter.AbstractPlatformFilterView;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.Collections;
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

    @Event(Type.FILL_CONTENT)
    public void onShow(SiteFolderPlatformEvents.Show event) {
        if (!policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_VIEW)) {
            fireEvent(new ErrorPageEvents.ShowForbidden());
            return;
        }

        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());
        view.getPagerContainer().add( pagerView.asWidget() );

        fireEvent(new ActionBarEvents.Clear());
        if (policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_CREATE)) {
            fireEvent(new ActionBarEvents.Add(lang.siteFolderPlatformCreate(), null, UiConstants.ActionBarIdentity.SITE_FOLDER_PLATFORM));
        }

        this.preScroll = event.preScroll;

        loadTable();
    }

    @Event
    public void onCreateClicked(ActionBarEvents.Clicked event) {
        if (!UiConstants.ActionBarIdentity.SITE_FOLDER_PLATFORM.equals(event.identity)) {
            return;
        }

        if (!policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_CREATE)) {
            return;
        }

        view.clearSelection();

        fireEvent(new SiteFolderPlatformEvents.Edit());
    }

    @Event
    public void onPlatformChanged(SiteFolderPlatformEvents.Changed event) {
        view.updateRow(event.platform);
    }

    @Override
    public void onItemClicked(Platform value) {
        persistScroll();

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

        persistScroll();
        fireEvent(new SiteFolderPlatformEvents.Edit(value.getId()).withBackEvent(() -> fireEvent(new SiteFolderPlatformEvents.Show(true))));
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

    @Override
    public void onOpenServersClicked(Platform value) {
        if (value != null) {
            fireEvent(new SiteFolderServerEvents.ShowSummaryTable(value.getId(), false));
        }
    }

    @Override
    public void loadData(int offset, int limit, AsyncCallback<List<Platform>> asyncCallback) {
        boolean isFirstChunk = offset == 0;
        PlatformQuery query = getQuery();
        query.setOffset(offset);
        query.setLimit(limit);
        siteFolderController.getPlatforms(query, new FluentCallback<SearchResult<Platform>>()
                .withError(throwable -> {
                    fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                    asyncCallback.onFailure(throwable);
                })
                .withSuccess(sr -> {
                    if (isFirstChunk) {
                        view.setTotalRecords(sr.getTotalCount());
                        pagerView.setTotalPages(view.getPageCount());
                        pagerView.setTotalCount(sr.getTotalCount());
                        restoreScroll();
                    }

                    asyncCallback.onSuccess(sr.getResults());
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
        query.setManagerIds(filterView.managers().getValue() == null
                ? Collections.singletonList( CrmConstants.Employee.UNDEFINED )
                : filterView.managers().getValue().stream()
                        .map(PersonShortView::getId)
                        .collect(Collectors.toList())
        );
        query.setParams(filterView.parameters().getValue());
        query.setServerIp(filterView.serverIp().getValue());
        query.setComment(filterView.comment().getValue());
        return query;
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

    private Runnable removeAction(Long platformId) {
        return () -> siteFolderController.removePlatform(platformId, new RequestCallback<Long>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.siteFolderPlatformNotRemoved(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(Long result) {
                fireEvent(new SiteFolderPlatformEvents.Show(false));
                fireEvent(new NotifyEvents.Show(lang.siteFolderPlatformRemoved(), NotifyEvents.NotifyType.SUCCESS));
            }
        });
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

    private Integer scrollTo = 0;
    private Boolean preScroll = false;
    private AppEvents.InitDetails initDetails;
}
