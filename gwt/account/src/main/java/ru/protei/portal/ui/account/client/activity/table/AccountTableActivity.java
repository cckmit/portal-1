package ru.protei.portal.ui.account.client.activity.table;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.ent.UserLogin;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.query.AccountQuery;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.account.client.activity.filter.AbstractAccountFilterActivity;
import ru.protei.portal.ui.account.client.activity.filter.AbstractAccountFilterView;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerActivity;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.AccountControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.protei.portal.ui.common.client.util.PaginationUtils.*;

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

        pagerView.setActivity( this );
    }

    @Event
    public void onAuthSuccess ( AuthEvents.Success event ) {
        filterView.resetFilter();
    }

    @Event( Type.FILL_CONTENT )
    public void onShow( AccountEvents.Show event ) {
        if (!policyService.hasPrivilegeFor(En_Privilege.ACCOUNT_VIEW)) {
            fireEvent(new ForbiddenEvents.Show());
            return;
        }

        init.parent.clear();
        init.parent.add( view.asWidget() );
        view.getPagerContainer().add( pagerView.asWidget() );

        fireEvent( policyService.hasPrivilegeFor( En_Privilege.ACCOUNT_CREATE ) ?
                new ActionBarEvents.Add( CREATE_ACTION, null, UiConstants.ActionBarIdentity.ACCOUNT ) :
                new ActionBarEvents.Clear()
        );

        clearScroll( event );

        requestAccounts( this.page );
    }

    @Event
    public void onCreateClicked( ActionBarEvents.Clicked event ) {
        if ( !UiConstants.ActionBarIdentity.ACCOUNT.equals( event.identity ) ) {
            return;
        }

        fireEvent( new AccountEvents.Edit() );
    }

    @Event
    public void onInitDetails( AppEvents.InitDetails initDetails ) {
        this.init = initDetails;
    }

    @Event
    public void onConfirmRemove( ConfirmDialogEvents.Confirm event ) {
        if ( !event.identity.equals( getClass().getName() ) ) {
            return;
        }
        accountService.removeAccount( accountId, new RequestCallback< Boolean >() {
            @Override
            public void onError( Throwable throwable ) {}

            @Override
            public void onSuccess( Boolean aBoolean ) {
                fireEvent( new AccountEvents.Show() );
                fireEvent( new NotifyEvents.Show( lang.accountRemoveSuccessed(), NotifyEvents.NotifyType.SUCCESS ) );
                accountId = null;
            }
        } );
    }

    @Event
    public void onCancelRemove( ConfirmDialogEvents.Cancel event ) {
        accountId = null;
    }

    @Override
    public void onItemClicked ( UserLogin value ) {
        showPreview( value );
    }

    @Override
    public void onEditClicked( UserLogin value ) {
        persistScrollTopPosition();
        fireEvent( new AccountEvents.Edit( value.getId() ) );
    }

    @Override
    public void onRemoveClicked( UserLogin value ) {
        if ( value != null ) {
            accountId = value.getId();
            fireEvent( new ConfirmDialogEvents.Show( getClass().getName(), lang.accountRemoveConfirmMessage() ) );
        }
    }

    @Override
    public void onFilterChanged() {
        this.page = 0;
        requestAccounts( this.page );
    }

    @Override
    public void onPageSelected( int page ) {
        this.page = page;
        requestAccounts( this.page );
    }

    private void requestAccounts( int page ) {
        view.clearRecords();
        animation.closeDetails();

        boolean isFirstChunk = page == 0;
        marker = new Date().getTime();

        AccountQuery query = makeQuery();
        query.setOffset( page*PAGE_SIZE );
        query.setLimit( PAGE_SIZE );

        accountService.getAccounts( query, new FluentCallback< SearchResult< UserLogin > >()
                .withMarkedSuccess( marker, ( m, r ) -> {
                    if ( marker == m ) {
                        if ( isFirstChunk ) {
                            pagerView.setTotalCount( r.getTotalCount() );
                            pagerView.setTotalPages( getTotalPages( r.getTotalCount() ) );
                        }
                        pagerView.setCurrentPage( page );
                        view.addRecords( r.getResults() );
                        restoreScrollTopPositionOrClearSelection();
                    }
                } )
                .withErrorMessage( lang.errGetList() ) );
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
        List<Long> roles = Optional.ofNullable( filterView.roles().getValue() )
                .orElse( Collections.emptySet() )
                .stream()
                .map( UserRole::getId )
                .collect( Collectors.toList());

        Long companyId = null;
        if(filterView.company().getValue()!=null){
            companyId = filterView.company().getValue().getId();
        }

        return new AccountQuery(
                filterView.types().getValue(),
                roles,
                filterView.searchPattern().getValue(),
                filterView.sortField().getValue(),
                filterView.sortDir().getValue() ? En_SortDir.ASC: En_SortDir.DESC,
                companyId
        );
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

    private void clearScroll(AccountEvents.Show event) {
        if (event.clearScroll) {
            event.clearScroll = false;
            this.scrollTop = null;
            this.page = 0;
        }
    }

    @Inject
    Lang lang;

    @Inject
    AbstractAccountTableView view;

    @Inject
    AbstractAccountFilterView filterView;

    @Inject
    AccountControllerAsync accountService;

    @Inject
    TableAnimation animation;

    @Inject
    AbstractPagerView pagerView;

    @Inject
    PolicyService policyService;

    private Long accountId;

    private AppEvents.InitDetails init;

    private static String CREATE_ACTION;

    private long marker;

    private Integer scrollTop;

    private int page = 0;
}
