package ru.protei.portal.ui.role.client.activity.edit;


import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_Scope;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.ForbiddenEvents;
import ru.protei.portal.ui.common.client.events.RoleEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.RoleControllerAsync;

import java.util.HashSet;

/**
 * Активность создания и редактирования роли
 */
public abstract class RoleEditActivity implements AbstractRoleEditActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity( this );
    }

    @Event
    public void onInitDetails( AppEvents.InitDetails initDetails ) {
        this.initDetails = initDetails;
    }

    @Event
    public void onShow( RoleEvents.Edit event ) {
        if (!hasPrivileges(event.id)) {
            fireEvent(new ForbiddenEvents.Show());
            return;
        }

        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());

        if(event.id == null) {
            role = new UserRole();
            fillView();
            return;
        }

        requestData(event.id);
    }

    @Override
    public void onSaveClicked() {
        if (!validate()) {
            return;
        }
        applyChanges();
        roleService.saveRole(role, new AsyncCallback<UserRole>() {
            @Override
            public void onFailure(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(throwable.getMessage(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(UserRole role) {
                fireEvent(new RoleEvents.ChangeModel());
                fireEvent(new Back());
            }
        });
    }

    @Override
    public void onCancelClicked() {
        fireEvent(new Back());
    }


    private void requestData(Long id) {
        roleService.getRole(id, new AsyncCallback<UserRole>() {
            @Override
            public void onFailure(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(UserRole value) {
                role = value;
                fillView();
            }
        });
    }

    private void applyChanges() {
        role.setCode( view.name().getValue() );
        role.setScope( view.scope().getValue() );
        role.setDefaultForContact(view.defaultForContact().getValue());
        role.setInfo( view.description().getValue() );
    }

    private boolean validate() {
       return HelperFunc.isNotEmpty(view.name().getValue());
    }

    private void fillView(){
        if ( role.getPrivileges() == null ) {
            role.setPrivileges( new HashSet<>() );
        }
        if ( role.getScope() == null ) {
            role.setScope( En_Scope.SYSTEM );
        }
        view.name().setValue(role.getCode());
        view.description().setValue(role.getInfo());
        view.privileges().setValue(role.getPrivileges());
        view.scope().setValue( role.getScope() );
        view.defaultForContact().setValue(role.isDefaultForContact());
    }

    private boolean hasPrivileges(Long roleId) {
        if (roleId == null && policyService.hasPrivilegeFor(En_Privilege.ROLE_CREATE)) {
            return true;
        }

        if (roleId != null && policyService.hasPrivilegeFor(En_Privilege.ROLE_EDIT)) {
            return true;
        }

        return false;
    }

    @Inject
    AbstractRoleEditView view;

    @Inject
    Lang lang;

    @Inject
    RoleControllerAsync roleService;

    @Inject
    PolicyService policyService;

    private UserRole role;
    private AppEvents.InitDetails initDetails;
}
