package ru.protei.portal.ui.account.client.activity.table;

import com.google.gwt.user.client.Window;
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
import ru.protei.portal.core.model.struct.Project;
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

        this.preScroll = event.preScroll;

        loadTable();
    }

    @Event
    public void onCreateClicked( ActionBarEvents.Clicked event ) {
        if ( !UiConstants.ActionBarIdentity.ACCOUNT.equals( event.identity ) ) {
            return;
        }

        view.clearSelection();

        fireEvent( new AccountEvents.Edit() );
    }

    @Event
    public void onInitDetails( AppEvents.InitDetails initDetails ) {
        this.init = initDetails;
    }

    @Override
    public void onItemClicked ( UserLogin value ) {
        persistScrollPosition();
        showPreview( value );
    }

    @Override
    public void onEditClicked( UserLogin value ) {
        persistScrollPosition();
        fireEvent( new AccountEvents.Edit( value.getId() ) );
    }

    @Override
    public void onRemoveClicked( UserLogin value ) {
        if (value != null) {
            fireEvent(new ConfirmDialogEvents.Show(lang.accountRemoveConfirmMessage(), removeAction(value.getId())));
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
    public void onPageSelected( int page ) {
        view.scrollTo(page);
    }

    @Override
    public void loadData(int offset, int limit, AsyncCallback<List<UserLogin>> asyncCallback) {
        boolean isFirstChunk = offset == 0;
        marker = new Date().getTime();

        AccountQuery query = makeQuery();
        query.setOffset(offset);
        query.setLimit(limit);

        accountService.getAccounts( query, new FluentCallback< SearchResult< UserLogin > >()
                .withMarkedSuccess( marker, ( m, sr ) -> {
                    if ( marker == m ) {
                        asyncCallback.onSuccess(sr.getResults());
                        if (isFirstChunk) {
                            view.setTotalRecords(sr.getTotalCount());
                            pagerView.setTotalPages(view.getPageCount());
                            pagerView.setTotalCount(sr.getTotalCount());
                            restoreScroll();
                        }
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

    private void persistScrollPosition() {
        scrollTo = Window.getScrollTop();
    }

    private void restoreScroll() {
        if (!preScroll) {
            view.clearSelection();
            return;
        }

        Window.scrollTo(0, scrollTo);
        preScroll = false;
        scrollTo = 0;
    }

    private void loadTable() {
        animation.closeDetails();
        view.clearRecords();
        view.triggerTableLoad();
    }

    private Runnable removeAction(Long accountId) {
        return () -> accountService.removeAccount(accountId, new FluentCallback<Boolean>().withSuccess(result -> {
            fireEvent(new AccountEvents.Show(false));
            fireEvent(new NotifyEvents.Show(lang.accountRemoveSuccessed(), NotifyEvents.NotifyType.SUCCESS));
        }));
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

    private AppEvents.InitDetails init;

    private static String CREATE_ACTION;

    private long marker;

    private Integer scrollTo = 0;

    private Boolean preScroll = false;
}
