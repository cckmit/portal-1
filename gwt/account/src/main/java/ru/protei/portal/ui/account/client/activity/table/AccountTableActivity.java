package ru.protei.portal.ui.account.client.activity.table;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.ent.UserLogin;
import ru.protei.portal.core.model.query.AccountQuery;
import ru.protei.portal.ui.account.client.activity.filter.AbstractAccountFilterActivity;
import ru.protei.portal.ui.account.client.activity.filter.AbstractAccountFilterView;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerActivity;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerView;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.AccountServiceAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.winter.web.common.client.events.SectionEvents;

import java.util.List;

/**
 * Активность создания и редактирования учетной записи
 */
public abstract class AccountTableActivity implements AbstractAccountTableActivity, AbstractAccountFilterActivity,
        AbstractPagerActivity, Activity {

    @PostConstruct
    public void onInit() {
        CREATE_ACTION = lang.buttonCreate();

        view.setActivity( this );
        view.setAnimation( animation );

        filterView.setActivity( this );
        view.getFilterContainer().add( filterView.asWidget() );

        pagerView.setPageSize( view.getPageSize() );
        pagerView.setActivity( this );
    }

    @Event
    public void onAuthSuccess (AuthEvents.Success event) {
        filterView.resetFilter();
    }

    @Event
    public void onShow( AccountEvents.Show event ) {

        this.fireEvent( new AppEvents.InitPanelName( lang.accounts() ) );
        init.parent.clear();
        init.parent.add( view.asWidget() );
        init.parent.add( pagerView.asWidget() );

        fireEvent( new ActionBarEvents.Add( CREATE_ACTION, UiConstants.ActionBarIcons.CREATE, UiConstants.ActionBarIdentity.ACCOUNT ) );

        view.showElements();

        requestTotalCount();
    }

    @Event
    public void onCreateClicked( SectionEvents.Clicked event ) {
        if ( !UiConstants.ActionBarIdentity.ACCOUNT.equals( event.identity ) ) {
            return;
        }

        fireEvent( new AccountEvents.Edit() );
    }

    @Event
    public void onInitDetails( AppEvents.InitDetails initDetails ) {
        this.init = initDetails;
    }

    @Override
    public void onItemClicked ( UserLogin value ) {
        showPreview( value );
    }

    @Override
    public void onEditClicked( UserLogin value ) {
        fireEvent( new AccountEvents.Edit( value.getId() ) );
    }

    @Override
    public void onFilterChanged() {
        requestTotalCount();
    }

    @Override
    public void loadData( int offset, int limit, AsyncCallback< List< UserLogin > > asyncCallback ) {
        AccountQuery query = makeQuery();
        query.setOffset( offset );
        query.setLimit( limit );

        accountService.getAccounts( query, new RequestCallback< List< UserLogin > >() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent( new NotifyEvents.Show( lang.errGetList(), NotifyEvents.NotifyType.ERROR ) );
                asyncCallback.onFailure( throwable );
            }

            @Override
            public void onSuccess( List< UserLogin > logins ) {
                asyncCallback.onSuccess( logins );
            }
        } );
    }

    @Override
    public void onPageChanged( int page ) {
        pagerView.setCurrentPage( page+1 );
    }

    @Override
    public void onFirstClicked() {
        view.scrollTo( 0 );
    }

    @Override
    public void onLastClicked() {
        view.scrollTo( view.getPageCount()-1 );
    }

    private void requestTotalCount() {
        view.clearRecords();
        animation.closeDetails();

        accountService.getAccountsCount( makeQuery(), new RequestCallback< Long >() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent( new NotifyEvents.Show( lang.errGetList(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess( Long count ) {
                view.setRecordCount( count );
                pagerView.setTotalPages( view.getPageCount() );
            }
        });
    }

    private void showPreview ( UserLogin value ) {

        if ( value == null ) {
            animation.closeDetails();
        } else {
            animation.showDetails();
            fireEvent( new AccountEvents.ShowPreview( view.getPreviewContainer(), value ) );
        }
    }

    private AccountQuery makeQuery() {
        return new AccountQuery( filterView.types().getValue(), filterView.searchPattern().getValue(), filterView.sortField().getValue(),
                filterView.sortDir().getValue()? En_SortDir.ASC: En_SortDir.DESC );

    };

    @Inject
    Lang lang;

    @Inject
    AbstractAccountTableView view;

    @Inject
    AbstractAccountFilterView filterView;

    @Inject
    AccountServiceAsync accountService;

    @Inject
    TableAnimation animation;

    @Inject
    AbstractPagerView pagerView;

    private AppEvents.InitDetails init;

    private static String CREATE_ACTION;
}
