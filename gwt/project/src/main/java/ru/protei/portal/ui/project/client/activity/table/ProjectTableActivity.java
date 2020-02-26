package ru.protei.portal.ui.project.client.activity.table;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.core.model.struct.Project;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.RegionControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.project.client.activity.filter.AbstractProjectFilterActivity;
import ru.protei.portal.ui.project.client.activity.filter.AbstractProjectFilterView;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Активность таблицы проектов
 */
public abstract class ProjectTableActivity
        implements AbstractProjectTableActivity, AbstractProjectFilterActivity, Activity
{

    @PostConstruct
    public void onInit() {
        CREATE_ACTION = lang.buttonCreate();

        view.setActivity( this );
        view.setAnimation( animation );

        filterView.setActivity( this );
        view.getFilterContainer().add( filterView.asWidget() );
    }

    @Event
    public void onAuthSuccess (AuthEvents.Success event) {
        filterView.resetFilter();
    }

    @Event
    public void onShow( ProjectEvents.Show event ) {
        if (!policyService.hasPrivilegeFor(En_Privilege.PROJECT_VIEW)) {
            fireEvent(new ForbiddenEvents.Show());
            return;
        }

        initDetails.parent.clear();
        initDetails.parent.add( view.asWidget() );

        fireEvent( policyService.hasPrivilegeFor( En_Privilege.PROJECT_CREATE ) ?
            new ActionBarEvents.Add( CREATE_ACTION, null, UiConstants.ActionBarIdentity.PROJECT ) :
            new ActionBarEvents.Clear()
        );

        clearScroll(event);

        requestProjects( null );
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
                .withSuccess(result -> {
                    view.updateRow(result);
                }));
    }

    @Override
    public void onItemClicked( Project value ) {
        showPreview( value );
    }

    @Override
    public void onEditClicked( Project value ) {
        persistScrollTopPosition();
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
    public void onFilterChanged() {
        requestProjects( null );
    }

    private void requestProjects( Project rowToSelect ) {
        if ( rowToSelect == null ) {
            view.clearRecords();
            animation.closeDetails();
        }

        regionService.getProjectsByRegions( getQuery(), new RequestCallback<Map<String, List<Project>>>() {
                @Override
                public void onError( Throwable throwable ) {
                    fireEvent( new NotifyEvents.Show( lang.errGetList(), NotifyEvents.NotifyType.ERROR ) );
                }

                @Override
                public void onSuccess( Map<String, List<Project>> result ) {
                    fillRows( result );
                    if ( rowToSelect != null ) {
                        view.updateRow( rowToSelect );
                    }
                    restoreScrollTopPositionOrClearSelection();
                }
            } );
    }

    private void fillRows( Map<String, List<Project>> result ) {
        view.clearRecords();
        for ( Map.Entry<String, List<Project>> entry : result.entrySet() ) {
            view.addSeparator( entry.getKey() );

            for ( Project project : entry.getValue() ) {
                view.addRow(project);
            }
        }
    }

    private void showPreview ( Project value ) {
        if ( value == null ) {
            animation.closeDetails();
        } else {
            animation.showDetails();
            fireEvent( new ProjectEvents.ShowPreview( view.getPreviewContainer(), value.getId() ) );
        }
    }

    private void persistScrollTopPosition() {
        scrollTop = Window.getScrollTop();
    }

    private void restoreScrollTopPositionOrClearSelection() {
        if (scrollTop == null) {
            view.clearSelection();
            return;
        }
        int trh = RootPanel.get(DebugIds.DEBUG_ID_PREFIX + DebugIds.APP_VIEW.GLOBAL_CONTAINER).getOffsetHeight() - Window.getClientHeight();
        if (scrollTop <= trh) {
            Window.scrollTo(0, scrollTop);
            scrollTop = null;
        }
    }

    private ProjectQuery getQuery() {
        ProjectQuery query = new ProjectQuery();
        query.setSearchString(filterView.searchPattern().getValue());
        query.setStates( filterView.states().getValue() );
        query.setDistrictIds(
                filterView.districts().getValue().stream()
                        .map( (district)-> district.id )
                        .collect( Collectors.toSet() )
        );
        query.setDirectionId(
                filterView.direction().getValue() == null
                        ? null
                        : filterView.direction().getValue().id
        );
        query.setSortField(filterView.sortField().getValue());
        query.setSortDir(filterView.sortDir().getValue() ? En_SortDir.ASC : En_SortDir.DESC);
        query.setOnlyMineProjects(filterView.onlyMineProjects().getValue());
        return query;
    }

    private void clearScroll(ProjectEvents.Show event) {
        if (event.clearScroll) {
            event.clearScroll = false;
            this.scrollTop = null;
        }
    }

    private Runnable removeAction(Long projectId) {
        return () -> regionService.removeProject(projectId, new FluentCallback<Boolean>()
                .withSuccess(result -> {
                    fireEvent(new NotifyEvents.Show(lang.projectRemoveSucceeded(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new ProjectEvents.ChangeModel());
                    fireEvent(new ProjectEvents.Show());
                })
        );
    }

    @Inject
    Lang lang;
    @Inject
    AbstractProjectTableView view;
    @Inject
    AbstractProjectFilterView filterView;
    @Inject
    RegionControllerAsync regionService;
    @Inject
    TableAnimation animation;
    @Inject
    PolicyService policyService;

    private static String CREATE_ACTION;
    private AppEvents.InitDetails initDetails;
    private Integer scrollTop;
}
