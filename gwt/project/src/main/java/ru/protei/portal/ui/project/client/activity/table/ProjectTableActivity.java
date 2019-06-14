package ru.protei.portal.ui.project.client.activity.table;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.core.model.struct.ProjectInfo;
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
import java.util.Objects;
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

        this.fireEvent( new AppEvents.InitPanelName( lang.issues() ) );
        initDetails.parent.clear();
        initDetails.parent.add( view.asWidget() );

        fireEvent( policyService.hasPrivilegeFor( En_Privilege.PROJECT_CREATE ) ?
            new ActionBarEvents.Add( CREATE_ACTION, UiConstants.ActionBarIcons.CREATE, UiConstants.ActionBarIdentity.PROJECT ) :
            new ActionBarEvents.Clear()
        );

        projectIdForRemove = null;
        requestProjects( null );
    }

    @Event
    public void onCreateClicked( ActionBarEvents.Clicked event ) {
        if ( !UiConstants.ActionBarIdentity.PROJECT.equals( event.identity ) ) {
            return;
        }

        regionService.createProject(null, new FluentCallback<Long>()
                .withSuccess(projectId -> {
                    updateListAndSelect(projectId);
                    fireEvent(new ProjectEvents.ChangeModel());
                })
        );
    }

    @Event
    public void onInitDetails( AppEvents.InitDetails initDetails ) {
        this.initDetails = initDetails;
    }

    @Event
    public void onChanged( ProjectEvents.Changed event ) {
        view.updateRow( event.project );

        if ( currentValue == null ) {
            return;
        }

        // если выбрали регион в первый раз
        if ( currentValue.getRegion() == null ) {
            if ( event.project.getRegion() != null ) {
                requestProjects( currentValue );
                return;
            }
        }

        // если выбрали регион взамен выбранному ранее
        if ( !Objects.equals(currentValue.getRegion(), event.project.getRegion() ) ) {
            requestProjects( currentValue );
        }
    }

    @Event
    public void onConfirmRemove(ConfirmDialogEvents.Confirm event) {

        if (!getClass().getName().equals(event.identity)) {
            return;
        }

        if (!policyService.hasPrivilegeFor(En_Privilege.PROJECT_REMOVE)) {
            return;
        }

        regionService.removeProject(projectIdForRemove, new FluentCallback<Boolean>()
                .withResult(() -> {
                    projectIdForRemove = null;
                })
                .withSuccess(result -> {
                    fireEvent(new ProjectEvents.Show());
                    fireEvent(new NotifyEvents.Show(lang.projectRemoveSucceeded(), NotifyEvents.NotifyType.SUCCESS));
                })
        );
    }

    @Event
    public void onProjectCancelRemove(ConfirmDialogEvents.Cancel event) {
        if (!getClass().getName().equals(event.identity)) {
            return;
        }
        projectIdForRemove = null;
    }

    @Override
    public void onItemClicked( ProjectInfo value ) {
        showPreview( value );
    }

    @Override
    public void onEditClicked( ProjectInfo value ) {
        //fireEvent(new ProjectEvents.Edit(value.getId()));
        showPreview( value );
    }

    @Override
    public void onRemoveClicked(ProjectInfo value) {
        if (!policyService.hasPrivilegeFor(En_Privilege.PROJECT_REMOVE)) {
            return;
        }

        if (value == null) {
            return;
        }

        projectIdForRemove = value.getId();
        fireEvent(new ConfirmDialogEvents.Show(getClass().getName(), lang.projectRemoveConfirmMessage(value.getName())));
    }

    @Override
    public void onFilterChanged() {
        requestProjects( null );
    }

    private void requestProjects( ProjectInfo rowToSelect ) {
        if ( rowToSelect == null ) {
            view.clearRecords();
            animation.closeDetails();
        }

        regionService.getProjectsByRegions( getQuery(), new RequestCallback<Map<String, List<ProjectInfo>>>() {
                @Override
                public void onError( Throwable throwable ) {
                    fireEvent( new NotifyEvents.Show( lang.errGetList(), NotifyEvents.NotifyType.ERROR ) );
                }

                @Override
                public void onSuccess( Map<String, List<ProjectInfo>> result ) {
                    fillRows( result );
                    if ( rowToSelect != null ) {
                        view.updateRow( rowToSelect );
                    }
                }
            } );
    }

    private void updateListAndSelect( Long projectId ) {
        regionService.getProjectsByRegions( getQuery(), new RequestCallback<Map<String, List<ProjectInfo>>>() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent( new NotifyEvents.Show( lang.errGetList(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess( Map<String, List<ProjectInfo>> result ) {
                view.clearRecords();
                fillRows( result );
                ProjectInfo info = new ProjectInfo();
                info.setId( projectId );
                onItemClicked( info );
            }
        } );
    }

    private void fillRows( Map<String, List<ProjectInfo>> result ) {
        view.clearRecords();
        for ( Map.Entry<String, List<ProjectInfo>> entry : result.entrySet() ) {
            view.addSeparator( entry.getKey() );

            for ( ProjectInfo projectInfo : entry.getValue() ) {
                view.addRow( projectInfo );
            }
        }
    }

    private void showPreview ( ProjectInfo value ) {
        currentValue = value;
        if ( value == null ) {
            animation.closeDetails();
        } else {
            animation.showDetails();
            fireEvent( new ProjectEvents.ShowPreview( view.getPreviewContainer(), value.getId() ) );
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

    ProjectInfo currentValue = null;
    private Long projectIdForRemove = null;

    private static String CREATE_ACTION;
    private AppEvents.InitDetails initDetails;
}
