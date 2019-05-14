package ru.protei.portal.ui.account.client.widget.role;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.query.UserRoleQuery;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.RoleEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.RoleControllerAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.LifecycleSelectorModel;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.List;

public abstract class RoleModel extends LifecycleSelectorModel<UserRole> {

    @Event
    public void onInit( AuthEvents.Success event ) {
        clear();
    }

    @Event
    public void onRoleListChanged( RoleEvents.ChangeModel event ) {
        refreshOptions();
    }

    @Override
    protected void refreshOptions() {
        UserRoleQuery query = new UserRoleQuery();
        query.setSortField( En_SortField.role_name );
        query.setSortDir( En_SortDir.ASC );
        roleService.getRoles(query, new FluentCallback<List<UserRole>>()
                .withError(throwable -> {
                    fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                })
                .withSuccess(this::notifySubscribers));
    }

    @Inject
    RoleControllerAsync roleService;
    @Inject
    Lang lang;
}
