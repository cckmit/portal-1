package ru.protei.portal.ui.issue.client.activity.table;

import com.google.inject.Inject;
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

        requestIssues();
    }

    @Event
    public void onCreateClicked( SectionEvents.Clicked event ) {
/*        if ( !CREATE_ACTION.equals( event.identity ) ) {
            return;
        }*/

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
//        fireEvent(IssueEvents.Edit.byId(value.getId()));
    }

    @Override
    public void onFilterChanged() {
        requestIssues();
    }

    private void requestIssues() {
        view.clearRecords();
        animation.closeDetails();

        issueService.getIssues( getQuery(), new RequestCallback< List< CaseObject > >() {
                @Override
                public void onError( Throwable throwable ) {
                    fireEvent( new NotifyEvents.Show( lang.errGetList(), NotifyEvents.NotifyType.ERROR ) );
                }

                @Override
                public void onSuccess( List< CaseObject > issues ) {
                    issues.forEach( ( issue ) -> {
                        view.addRecord( issue );
                    } );
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
