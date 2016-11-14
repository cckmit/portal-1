package ru.protei.portal.ui.issue.client.activity.table;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.IssueEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.issue.client.service.IssueServiceAsync;

import java.util.List;

/**
 * Активность таблицы обращений
 */
public abstract class IssueTableActivity implements AbstractIssueTableActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity( this );
        view.setAnimation( animation );
    }

    @Event
    public void onAuthSuccess (AuthEvents.Success event) {
        view.resetFilter();
    }

    @Event
    public void onShow( IssueEvents.Show event ) {

        this.fireEvent( new AppEvents.InitPanelName( lang.issues() ) );
        initDetails.parent.clear();
        initDetails.parent.add( view.asWidget() );

        requestIssues();
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
    public void onEditClicked(CaseObject value ) {
        fireEvent(IssueEvents.Edit.byId(value.getId()));
    }

    @Override
    public void onCreateClick() {
        fireEvent(new IssueEvents.Edit());
        //fireEvent(IssueEvents.Edit.newItem(view.company().getValue()));
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
        return new CaseQuery(
                En_CaseType.CRM_SUPPORT, view.company().getValue(), view.searchPattern().getValue(),
                view.sortField().getValue(), view.sortDir().getValue() ? En_SortDir.ASC : En_SortDir.DESC
        );
    }

    @Inject
    Lang lang;

    @Inject
    AbstractIssueTableView view;

    @Inject
    IssueServiceAsync issueService;

    @Inject
    TableAnimation animation;

    private AppEvents.InitDetails initDetails;
}
