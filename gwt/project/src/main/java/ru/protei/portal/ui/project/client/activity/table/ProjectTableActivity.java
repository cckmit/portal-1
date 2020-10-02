package ru.protei.portal.ui.project.client.activity.table;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.core.model.dto.Project;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerActivity;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.RegionControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.portal.ui.common.client.activity.projectfilter.AbstractProjectFilterActivity;
import ru.protei.portal.ui.common.client.activity.projectfilter.AbstractProjectFilterView;

import java.util.List;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.StringUtils.isBlank;
import static ru.protei.portal.ui.common.client.util.IssueFilterUtils.searchCaseNumber;

/**
 * Активность таблицы проектов
 */
public abstract class ProjectTableActivity
        implements AbstractProjectTableActivity, AbstractProjectFilterActivity,
            AbstractPagerActivity, Activity
{

    @PostConstruct
    public void onInit() {
        CREATE_ACTION = lang.buttonCreate();

        view.setActivity( this );
        view.setAnimation( animation );

        filterView.setActivity( this );
        view.getFilterContainer().add( filterView.asWidget() );

        pagerView.setActivity( this );
    }

    @Event
    public void onAuthSuccess (AuthEvents.Success event) {
        filterView.resetFilter();
    }

    @Event(Type.FILL_CONTENT)
    public void onShow( ProjectEvents.Show event ) {
        if (!policyService.hasPrivilegeFor(En_Privilege.PROJECT_VIEW)) {
            fireEvent(new ErrorPageEvents.ShowForbidden());
            return;
        }

        initDetails.parent.clear();
        initDetails.parent.add( view.asWidget() );
        view.getPagerContainer().add( pagerView.asWidget() );

        fireEvent( policyService.hasPrivilegeFor( En_Privilege.PROJECT_CREATE ) ?
            new ActionBarEvents.Add( CREATE_ACTION, null, UiConstants.ActionBarIdentity.PROJECT ) :
            new ActionBarEvents.Clear()
        );

        this.preScroll = event.preScroll;

        loadTable();
    }

    @Event
    public void onCreateClicked( ActionBarEvents.Clicked event ) {
        if ( !UiConstants.ActionBarIdentity.PROJECT.equals( event.identity ) ) {
            return;
        }

        view.clearSelection();

        fireEvent(new ProjectEvents.Edit());
    }

    @Event
    public void onInitDetails( AppEvents.InitDetails initDetails ) {
        this.initDetails = initDetails;
    }

    @Event
    public void onChangeRow( ProjectEvents.ChangeProject event ) {
        regionService.getProject(event.id, new FluentCallback<Project>()
                .withSuccess(view::updateRow)
        );
    }

    @Override
    public void onItemClicked( Project value ) {
        persistScroll();
        showPreview( value );
    }

    @Override
    public void onEditClicked( Project value ) {
        persistScroll();
        fireEvent(new ProjectEvents.Edit(value.getId()));
    }

    @Override
    public void onRemoveClicked(Project value) {
        if (!policyService.hasPrivilegeFor(En_Privilege.PROJECT_REMOVE)) {
            return;
        }

        if (value == null) {
            return;
        }

        fireEvent(new ConfirmDialogEvents.Show(lang.projectRemoveConfirmMessage(value.getName()), removeAction(value.getId())));
    }

    @Override
    public void onProjectFilterChanged() {
        loadTable();
    }

    @Override
    public void loadData(int offset, int limit,
                          final AsyncCallback<List<Project>> asyncCallback ) {
        boolean isFirstChunk = offset == 0;

        query = getQuery();
        query.setOffset(offset);
        query.setLimit(limit);

        regionService.getProjects(query, new RequestCallback<SearchResult<Project>> () {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                asyncCallback.onFailure(throwable);
            }

            @Override
            public void onSuccess( SearchResult<Project> sr ) {
                if (!query.equals(getQuery())) {
                    loadData( offset, limit, asyncCallback );
                } else {
                    if (isFirstChunk) {
                        view.setTotalRecords(sr.getTotalCount());
                        pagerView.setTotalPages(view.getPageCount());
                        pagerView.setTotalCount(sr.getTotalCount());
                        restoreScroll();
                    }

                    asyncCallback.onSuccess(sr.getResults());
                }
            }
        } );
    }


    @Override
    public void onPageChanged(int page) {
        pagerView.setCurrentPage(page);
    }

    @Override
    public void onPageSelected(int page) {
        view.scrollTo(page);
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

    private void showPreview ( Project value ) {
        if ( value == null ) {
            animation.closeDetails();
        } else {
            animation.showDetails();
            fireEvent( new ProjectEvents.ShowPreview( view.getPreviewContainer(), value.getId() ) );
        }
    }

    private ProjectQuery getQuery() {
        ProjectQuery query = new ProjectQuery();

        String searchString = filterView.searchPattern().getValue();
        query.setCaseIds(searchCaseNumber(searchString, false));
        if (query.getCaseIds() == null) {
            query.setSearchString(isBlank(searchString) ? null : searchString);
        }

        query.setStates(filterView.states().getValue());
        query.setRegions(filterView.regions().getValue());
        query.setHeadManagers(filterView.headManagers().getValue());
        query.setCaseMembers(filterView.caseMembers().getValue());
        query.setDirections(filterView.direction().getValue());
        query.setSortField(filterView.sortField().getValue());
        query.setSortDir(filterView.sortDir().getValue() ? En_SortDir.ASC : En_SortDir.DESC);
        query.setMemberId(policyService.getProfile().getId());
        query.setInitiatorCompanyIds(filterView.initiatorCompanies().getValue().stream()
                .map(entityOption -> entityOption.getId()).collect(Collectors.toSet()));
        return query;
    }

    private Runnable removeAction(Long projectId) {
        return () -> regionService.removeProject(projectId, new FluentCallback<Boolean>()
                .withSuccess(result -> {
                    fireEvent(new NotifyEvents.Show(lang.projectRemoveSucceeded(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new ProjectEvents.ChangeModel());
                    fireEvent(new ProjectEvents.Show(false));
                })
        );
    }

    private void loadTable() {
        animation.closeDetails();
        view.clearRecords();
        view.triggerTableLoad();
    }

    @Inject
    Lang lang;
    @Inject
    AbstractProjectTableView view;
    @Inject
    AbstractProjectFilterView filterView;
    @Inject
    AbstractPagerView pagerView;
    @Inject
    RegionControllerAsync regionService;
    @Inject
    TableAnimation animation;
    @Inject
    PolicyService policyService;

    private ProjectQuery query = null;

    private static String CREATE_ACTION;
    private AppEvents.InitDetails initDetails;
    private Integer scrollTo = 0;
    private Boolean preScroll = false;
}
