package ru.protei.portal.ui.issue.client.activity.table;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.issue.client.activity.filter.AbstractIssueFilterActivity;
import ru.protei.portal.ui.issue.client.activity.filter.AbstractIssueFilterView;
import ru.protei.portal.ui.issue.client.service.IssueServiceAsync;
import ru.protei.winter.web.common.client.events.SectionEvents;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Активность таблицы обращений
 */
public abstract class IssueTableActivity implements AbstractIssueTableActivity, AbstractIssueFilterActivity, Activity {

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
    public void onShow( IssueEvents.Show event ) {

        this.fireEvent( new AppEvents.InitPanelName( lang.issues() ) );
        initDetails.parent.clear();
        initDetails.parent.add( view.asWidget() );

        fireEvent( new ActionBarEvents.Add( CREATE_ACTION, UiConstants.ActionBarIcons.CREATE, UiConstants.ActionBarIdentity.ISSUE ) );

        requestIssuesCount();
    }

    @Event
    public void onCreateClicked( SectionEvents.Clicked event ) {
        if ( !UiConstants.ActionBarIdentity.ISSUE.equals( event.identity ) ) {
            return;
        }

        fireEvent(new IssueEvents.Edit());
    }

    @Event
    public void onInitDetails( AppEvents.InitDetails initDetails ) {
        this.initDetails = initDetails;
    }

    @Override
    public void onItemClicked( CaseObject value ) {
        showPreview( value );
    }

    @Override
    public void onEditClicked( CaseObject value ) {
        fireEvent(new IssueEvents.Edit(value.getId(), null));
    }

    @Override
    public void onFilterChanged() {
        requestIssuesCount();
    }

    @Override
    public void loadData( int offset, int limit, AsyncCallback<List<CaseObject>> asyncCallback ) {
        CaseQuery query = getQuery();
        query.setOffset( offset );
        query.setLimit( limit );

        issueService.getIssues( query, new RequestCallback<List<CaseObject>>() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent( new NotifyEvents.Show( lang.errGetList(), NotifyEvents.NotifyType.ERROR ) );
                asyncCallback.onFailure( throwable );
            }

            @Override
            public void onSuccess( List<CaseObject> caseObjects ) {
                asyncCallback.onSuccess( caseObjects );
            }
        } );
    }

    private void requestIssuesCount() {
        view.clearRecords();
        animation.closeDetails();

        issueService.getIssuesCount( getQuery(), new RequestCallback< Long >() {
                @Override
                public void onError( Throwable throwable ) {
                    fireEvent( new NotifyEvents.Show( lang.errGetList(), NotifyEvents.NotifyType.ERROR ) );
                }

                @Override
                public void onSuccess( Long issuesCount ) {
                    view.setIssuesCount( issuesCount );
                }
            } );
    }

    private void showPreview ( CaseObject value ) {

        if ( value == null ) {
            animation.closeDetails();
        } else {
            animation.showDetails();
            fireEvent( new IssueEvents.ShowPreview( view.getPreviewContainer(), value ) );
        }
    }

    private CaseQuery getQuery() {
        CaseQuery query = new CaseQuery(En_CaseType.CRM_SUPPORT, filterView.searchPattern().getValue(), filterView.sortField().getValue(), filterView.sortDir().getValue() ? En_SortDir.ASC : En_SortDir.DESC );

        query.setCompanyId( filterView.company().getValue() == null? null : filterView.company().getValue().getId() );
        query.setProductId( filterView.product().getValue() == null? null : filterView.product().getValue().getId() );
        query.setManagerId( filterView.manager().getValue() == null? null : filterView.manager().getValue().getId() );

        if(filterView.states().getValue() != null)
            query.setStateIds(
                    filterView.states().getValue()
                            .stream()
                            .map( En_CaseState::getId )
                            .collect( Collectors.toList() ));

        if(filterView.importances().getValue() != null)
            query.setImportanceIds(
                    filterView.importances().getValue()
                            .stream()
                            .map( En_ImportanceLevel::getId )
                            .collect( Collectors.toList() ));

        DateInterval interval = filterView.dateRange().getValue();

        if(interval != null) {
            query.setFrom( interval == null ? null : interval.from );
            query.setTo( interval == null ? null : interval.to );
        }

        return query;
    }

    @Inject
    Lang lang;

    @Inject
    AbstractIssueTableView view;
    @Inject
    AbstractIssueFilterView filterView;

    @Inject
    IssueServiceAsync issueService;

    @Inject
    TableAnimation animation;

    private static String CREATE_ACTION;
    private AppEvents.InitDetails initDetails;
}
