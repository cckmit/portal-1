package ru.protei.portal.ui.project.client.activity.table;

import com.google.gwt.regexp.shared.RegExp;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.core.model.struct.ProjectInfo;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.RegionServiceAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.project.client.activity.filter.AbstractProjectFilterActivity;
import ru.protei.portal.ui.project.client.activity.filter.AbstractProjectFilterView;
import ru.protei.winter.web.common.client.events.SectionEvents;

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

        this.fireEvent( new AppEvents.InitPanelName( lang.issues() ) );
        initDetails.parent.clear();
        initDetails.parent.add( view.asWidget() );

        fireEvent( new ActionBarEvents.Add( CREATE_ACTION, UiConstants.ActionBarIcons.CREATE, UiConstants.ActionBarIdentity.PROJECT ) );

        requestProjects();
    }

    @Event
    public void onCreateClicked( SectionEvents.Clicked event ) {
        if ( !UiConstants.ActionBarIdentity.PROJECT.equals( event.identity ) ) {
            return;
        }

        regionService.createNewProject( new RequestCallback<Long>(){
            @Override
            public void onError( Throwable throwable ) {

            }

            @Override
            public void onSuccess( Long aLong ) {
                updateListAndSelect( aLong );
            }
        });
//
//        fireEvent( new ProjectEvents.Edit() );
    }

    @Event
    public void onInitDetails( AppEvents.InitDetails initDetails ) {
        this.initDetails = initDetails;
    }

    @Event
    public void onChanged( ProjectEvents.Changed event ) {
        view.updateRow( event.project );
    }

    @Override
    public void onItemClicked( ProjectInfo value ) {
        showPreview( value );
    }

    @Override
    public void onEditClicked( ProjectInfo value ) {
        fireEvent(new ProjectEvents.Edit(value.getId()));
    }

    @Override
    public void onFilterChanged() {
        requestProjects();
    }

    private void requestProjects() {
        view.clearRecords();
        animation.closeDetails();

        regionService.getProjectsByRegions( getQuery(), new RequestCallback<Map<String, List<ProjectInfo>>>() {
                @Override
                public void onError( Throwable throwable ) {
                    fireEvent( new NotifyEvents.Show( lang.errGetList(), NotifyEvents.NotifyType.ERROR ) );
                }

                @Override
                public void onSuccess( Map<String, List<ProjectInfo>> result ) {
                    fillRows( result );
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

        return query;

//        query.setCompanyId( filterView.company().getValue() == null ? null : filterView.company().getValue().getId() );
//        query.setManagerId( filterView.manager().getValue() == null ? null : filterView.manager().getValue().getId() );

//        DateInterval interval = filterView.dateRange().getValue();
//        if(interval != null) {
//            query.setFrom( interval.from );
//            query.setTo( interval.to );
//        }
//
//        return query;
    }

    @Inject
    Lang lang;

    @Inject
    AbstractProjectTableView view;
    @Inject
    AbstractProjectFilterView filterView;

    @Inject
    RegionServiceAsync regionService;

    @Inject
    TableAnimation animation;

    private static String CREATE_ACTION;
    private AppEvents.InitDetails initDetails;

    private final RegExp caseNoPattern = RegExp.compile("\\d+");
}
