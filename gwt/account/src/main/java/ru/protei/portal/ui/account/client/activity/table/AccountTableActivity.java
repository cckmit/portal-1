package ru.protei.portal.ui.account.client.activity.table;

import com.google.gwt.user.client.rpc.AsyncCallback;
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
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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
    public void onAuthSuccess (AuthEvents.Success event) {
        filterView.resetFilter();
    }

    @Event(Type.FILL_CONTENT)
    public void onShow( AccountEvents.Show event ) {

        this.fireEvent( new AppEvents.InitPanelName( lang.accounts() ) );
        init.parent.clear();
        init.parent.add( view.asWidget() );
        view.getPagerContainer().add( pagerView.asWidget() );

        fireEvent( policyService.hasPrivilegeFor( En_Privilege.ACCOUNT_CREATE ) ?
                new ActionBarEvents.Add( CREATE_ACTION, UiConstants.ActionBarIcons.CREATE, UiConstants.ActionBarIdentity.ACCOUNT ) :
                new ActionBarEvents.Clear()
        );

        loadTable();
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
        loadTable();
    }

    @Override
    public void onPageChanged(int page) {
        pagerView.setCurrentPage(page);
    }

    @Override
    public void onPageSelected(int page) {
        view.scrollTo(page);
    }

    @Override
    public void loadData( int offset, int limit, AsyncCallback< List< UserLogin > > asyncCallback ) {
        boolean isFirstChunk = offset == 0;
        AccountQuery query = makeQuery();
        query.setOffset(offset);
        query.setLimit(limit);
        accountService.getAccounts(query, new FluentCallback<SearchResult<UserLogin>>()
                .withError(throwable -> {
                    fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                    asyncCallback.onFailure(throwable);
                })
                .withSuccess(sr -> {
                    asyncCallback.onSuccess(sr.getResults());
                    if (isFirstChunk) {
                        view.setTotalRecords(sr.getTotalCount());
                        pagerView.setTotalPages(view.getPageCount());
                        pagerView.setTotalCount(sr.getTotalCount());
                    }
                }));
    }

    private void loadTable() {
        animation.closeDetails();
        view.clearRecords();
        view.triggerTableLoad();
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
}
