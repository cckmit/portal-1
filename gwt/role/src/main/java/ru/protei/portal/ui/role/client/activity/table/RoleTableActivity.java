package ru.protei.portal.ui.role.client.activity.table;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.query.UserRoleQuery;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.RoleControllerAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.role.client.activity.filter.AbstractRoleFilterActivity;
import ru.protei.portal.ui.role.client.activity.filter.AbstractRoleFilterView;

import java.util.List;

/**
 * Активность таблицы роли
 */
public abstract class RoleTableActivity
        implements AbstractRoleTableActivity, AbstractRoleFilterActivity,
        Activity
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
    public void onShow( RoleEvents.Show event ) {
        init.parent.clear();
        init.parent.add( view.asWidget() );

        fireEvent( policyService.hasPrivilegeFor( En_Privilege.ROLE_CREATE ) ?
                new ActionBarEvents.Add( CREATE_ACTION, null, UiConstants.ActionBarIdentity.ROLE ) :
                new ActionBarEvents.Clear()
        );

        isShowTable = false;

        query = makeQuery();
        requestRecords();
    }

    @Event
    public void onCreateClicked( ActionBarEvents.Clicked event ) {
        if ( !UiConstants.ActionBarIdentity.ROLE.equals( event.identity ) ) {
            return;
        }

        fireEvent(new RoleEvents.Edit(null));
    }

    @Event
    public void onShowTable( RoleEvents.ShowTable event ) {
        event.parent.clear();
        event.parent.add( view.asWidget() );

        isShowTable = true;

        query = makeQuery();
        requestRecords();
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.init = initDetails;
    }

    @Event
    public void onConfirmRemove( ConfirmDialogEvents.Confirm event ) {
        if ( !event.identity.equals( getClass().getName() ) ) {
            return;
        }
        roleService.removeRole( roleIdForRemove, new RequestCallback< Boolean >() {
            @Override
            public void onError( Throwable throwable ) {}

            @Override
            public void onSuccess( Boolean aBoolean ) {
                fireEvent( new RoleEvents.Show() );
                fireEvent( new NotifyEvents.Show( lang.roleRemoveSuccessed(), NotifyEvents.NotifyType.SUCCESS ) );
                roleIdForRemove = null;
            }
        } );
    }

    @Override
    public void onItemClicked (UserRole value ) {
        if ( !isShowTable ) {
            showPreview( value );
        }
    }

    @Override
    public void onEditClicked(UserRole value ) {
        fireEvent(new RoleEvents.Edit(value.getId()));
    }

    @Override
    public void onRemoveClicked( UserRole value ) {
        if ( value != null ) {
            roleIdForRemove = value.getId();
            fireEvent( new ConfirmDialogEvents.Show( getClass().getName(), lang.roleRemoveConfirmMessage() ) );
        }
    }

    @Override
    public void onFilterChanged() {
        query = makeQuery();
        requestRecords();
    }

    private void requestRecords() {
        view.clearRecords();
        animation.closeDetails();

        roleService.getRoles( query, new RequestCallback<List<UserRole>>() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess( List<UserRole> roles ) {
                view.setData(roles);
            }
        } );
    }

    private void showPreview ( UserRole value ) {
        if ( value == null ) {
            animation.closeDetails();
        } else {
            animation.showDetails();
            fireEvent(new RoleEvents.ShowPreview(view.getPreviewContainer(), value));
        }
    }

    private UserRoleQuery makeQuery() {
        query = new UserRoleQuery();
        query.setSearchString(filterView.searchPattern().getValue());
        query.setSortField(filterView.sortField().getValue());
        query.setSortDir(filterView.sortDir().getValue() ? En_SortDir.ASC : En_SortDir.DESC);

        return query;
    }


    @Inject
    Lang lang;

    @Inject
    AbstractRoleTableView view;
    @Inject
    AbstractRoleFilterView filterView;

    @Inject
    RoleControllerAsync roleService;

    @Inject
    TableAnimation animation;

    @Inject
    PolicyService policyService;

    private boolean isShowTable = false;
    private Long roleIdForRemove;

    private AppEvents.InitDetails init;
    private UserRoleQuery query;

    private static String CREATE_ACTION;
}
