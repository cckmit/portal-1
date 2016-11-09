package ru.protei.portal.ui.issue.client.activity.table;

import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.PeriodicTaskService;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.issue.client.service.IssueServiceAsync;

import java.util.List;
import java.util.function.Consumer;

/**
 * Активность таблицы обращений
 */
public abstract class IssueTableActivity implements AbstractIssueTableActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity( this );
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
        Window.alert( "Clicked on issue!" );
    }

    @Override
    public void onEditClicked(CaseObject value ) {
//        fireEvent(IssueEvents.Edit.byId(value.getId()));
    }


    @Override
    public void onCreateClick() {
//        fireEvent(IssueEvents.Edit.newItem(view.company().getValue()));
    }

    @Override
    public void onFilterChanged() {
        requestIssues();
    }

    private void requestIssues() {
        view.clearRecords();

        issueService.getIssues( view.searchPattern().getValue(), null,
            view.showFired().getValue() ? null : view.showFired().getValue(),
            view.sortField().getValue(), view.sortDir().getValue(), new RequestCallback< List< CaseObject > >() {
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

    @Inject
    Lang lang;

    @Inject
    AbstractIssueTableView view;

    @Inject
    IssueServiceAsync issueService;

    private AppEvents.InitDetails initDetails;
}
