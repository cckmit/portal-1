package ru.protei.portal.ui.role.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.role.client.activity.edit.AbstractRoleEditView;
import ru.protei.portal.ui.role.client.activity.edit.RoleEditActivity;
import ru.protei.portal.ui.role.client.activity.filter.AbstractRoleFilterView;
import ru.protei.portal.ui.role.client.activity.page.RolePage;
import ru.protei.portal.ui.role.client.activity.preview.AbstractRolePreviewView;
import ru.protei.portal.ui.role.client.activity.preview.RolePreviewActivity;
import ru.protei.portal.ui.role.client.activity.table.AbstractRoleTableView;
import ru.protei.portal.ui.role.client.activity.table.RoleTableActivity;
import ru.protei.portal.ui.role.client.view.edit.RoleEditView;
import ru.protei.portal.ui.role.client.view.filter.RoleFilterView;
import ru.protei.portal.ui.role.client.view.preview.RolePreviewView;
import ru.protei.portal.ui.role.client.view.table.RoleTableView;


/**
 * Описание классов фабрики
 */
public class RoleClientModule extends AbstractGinModule {
    @Override
    protected void configure() {
        bind( RolePage.class ).asEagerSingleton();

        bind ( RoleTableActivity.class ).asEagerSingleton();
        bind ( AbstractRoleTableView.class ).to( RoleTableView.class ).in( Singleton.class );

        bind( RolePreviewActivity.class ).asEagerSingleton();
        bind( AbstractRolePreviewView.class ).to( RolePreviewView.class ).in( Singleton.class );

        bind( RoleEditActivity.class ).asEagerSingleton();
        bind ( AbstractRoleEditView.class ).to(RoleEditView.class).in(Singleton.class);

        bind( AbstractRoleFilterView.class ).to( RoleFilterView.class ).in( Singleton.class );
    }
}

