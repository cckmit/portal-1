package ru.protei.portal.ui.role.client.activity.table;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.query.RoleQuery;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.RoleServiceAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.role.client.activity.filter.AbstractRoleFilterActivity;
import ru.protei.portal.ui.role.client.activity.filter.AbstractRoleFilterView;
import ru.protei.winter.web.common.client.events.SectionEvents;

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

        fireEvent( new ActionBarEvents.Add( CREATE_ACTION, UiConstants.ActionBarIcons.CREATE, UiConstants.ActionBarIdentity.CONTACT ) );

        isShowTable = false;

        query = makeQuery();
        requestRecords();
    }

    @Event
    public void onCreateClicked( SectionEvents.Clicked event ) {
        if ( !UiConstants.ActionBarIdentity.CONTACT.equals( event.identity ) ) {
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

    private RoleQuery makeQuery() {
        return new RoleQuery();
    }


    @Inject
    Lang lang;

    @Inject
    AbstractRoleTableView view;
    @Inject
    AbstractRoleFilterView filterView;

    @Inject
    RoleServiceAsync roleService;

    @Inject
    TableAnimation animation;

    private boolean isShowTable = false;

    private AppEvents.InitDetails init;
    private RoleQuery query;

    private static String CREATE_ACTION;
}
