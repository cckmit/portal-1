package ru.protei.portal.ui.sitefolder.client.activity.app.table;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.ent.Application;
import ru.protei.portal.core.model.ent.Server;
import ru.protei.portal.core.model.query.ApplicationQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.ProductShortView;
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
import ru.protei.portal.ui.sitefolder.client.activity.app.filter.AbstractApplicationFilterActivity;
import ru.protei.portal.ui.sitefolder.client.activity.app.filter.AbstractApplicationFilterView;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class ApplicationTableActivity implements
        AbstractApplicationTableActivity, AbstractApplicationFilterActivity,
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
    public void onShow(SiteFolderAppEvents.Show event) {
        if (!policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_VIEW)) {
            fireEvent(new ErrorPageEvents.ShowForbidden());
            return;
        }

        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());
        view.getPagerContainer().add(pagerView.asWidget());

        server = event.server;

        fireEvent(new ActionBarEvents.Clear());
        if (policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_CREATE)) {
            fireEvent(new ActionBarEvents.Add(lang.siteFolderAppCreate(), null, UiConstants.ActionBarIdentity.SITE_FOLDER_APP));
        }

        if (server != null) {
            Set<EntityOption> options = new HashSet<>();
            options.add(server);
            filterView.servers().setValue(options);
        }

        loadTable();
    }

    @Event
    public void onCreateClicked(ActionBarEvents.Clicked event) {
        if (!UiConstants.ActionBarIdentity.SITE_FOLDER_APP.equals(event.identity)) {
            return;
        }

        if (!policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_CREATE)) {
            return;
        }

        fireEvent(new SiteFolderAppEvents.Edit());
    }

    @Event
    public void onAppChanged(SiteFolderAppEvents.Changed event) {
        view.updateRow(event.app);
    }

    @Override
    public void onItemClicked(Application value) {
        if (value == null) {
            animation.closeDetails();
        } else {
            animation.showDetails();
            fireEvent(new SiteFolderAppEvents.ShowPreview(view.getPreviewContainer(), value));
        }
    }

    @Override
    public void onEditClicked(Application value) {
        persistScroll();

        if (!policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_EDIT)) {
            return;
        }

        if (value == null) {
            return;
        }

        fireEvent(new SiteFolderAppEvents.Edit(value.getId()).withBackEvent(() -> fireEvent(new SiteFolderAppEvents.Show(makeEntityOption(value.getServer()), true))));
    }

    @Override
    public void onRemoveClicked(Application value) {
        if (!policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_REMOVE)) {
            return;
        }

        if (value == null) {
            return;
        }

        fireEvent(new ConfirmDialogEvents.Show(lang.siteFolderAppConfirmRemove(), removeAction(value.getId())));
    }

    @Override
    public void loadData(int offset, int limit, AsyncCallback<List<Application>> asyncCallback) {
        boolean isFirstChunk = offset == 0;
        ApplicationQuery query = getQuery();
        query.setOffset(offset);
        query.setLimit(limit);
        siteFolderController.getApplications(query, new FluentCallback<SearchResult<Application>>()
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

    private EntityOption makeEntityOption(Server server) {
        return server == null ? null : new EntityOption(server.getName(), server.getId());
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

    private void loadTable() {
        animation.closeDetails();
        view.clearRecords();
        view.triggerTableLoad();
    }

    private ApplicationQuery getQuery() {
        ApplicationQuery query = new ApplicationQuery();
        query.setSearchString(filterView.name().getValue());
        query.setSortField(filterView.sortField().getValue());
        query.setSortDir(filterView.sortDir().getValue() ? En_SortDir.ASC : En_SortDir.DESC);
        query.setServerIds(filterView.servers().getValue() == null
                ? null
                : filterView.servers().getValue().stream()
                .map(EntityOption::getId)
                .collect(Collectors.toList())
        );
        query.setComponentIds(filterView.components().getValue() == null
                ? null
                : filterView.components().getValue().stream()
                .map(ProductShortView::getId)
                .collect(Collectors.toList())
        );
        query.setComment(filterView.comment().getValue());
        return query;
    }

    private Runnable removeAction(Long applicationId) {
        return () -> siteFolderController.removeApplication(applicationId, new RequestCallback<Long>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.siteFolderAppNotRemoved(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(Long result) {
                fireEvent(new SiteFolderAppEvents.ChangeModel());
                fireEvent(new SiteFolderAppEvents.Show(server, false));
                fireEvent(new NotifyEvents.Show(lang.siteFolderAppRemoved(), NotifyEvents.NotifyType.SUCCESS));
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
    AbstractApplicationTableView view;
    @Inject
    AbstractApplicationFilterView filterView;
    @Inject
    AbstractPagerView pagerView;

    private EntityOption server = null;
    private AppEvents.InitDetails initDetails;

    private Integer scrollTo = 0;
    private Boolean preScroll = false;
}
