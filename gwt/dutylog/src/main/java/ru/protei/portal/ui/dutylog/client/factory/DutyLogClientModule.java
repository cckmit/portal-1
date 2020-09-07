package ru.protei.portal.ui.dutylog.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.dutylog.client.activity.edit.AbstractDutyLogEditView;
import ru.protei.portal.ui.dutylog.client.activity.edit.DutyLogEditActivity;
import ru.protei.portal.ui.dutylog.client.activity.filter.AbstractDutyLogFilterView;
import ru.protei.portal.ui.dutylog.client.activity.page.DutyLogPage;
import ru.protei.portal.ui.dutylog.client.activity.table.DutyLogTableActivity;
import ru.protei.portal.ui.dutylog.client.activity.table.AbstractDutyLogTableView;
import ru.protei.portal.ui.dutylog.client.view.edit.DutyLogEditView;
import ru.protei.portal.ui.dutylog.client.view.filter.DutyLogFilterView;
import ru.protei.portal.ui.dutylog.client.view.table.DutyLogTableView;

/**
 * Описание классов фабрики
 */
public class DutyLogClientModule extends AbstractGinModule {

    @Override
    protected void configure() {
        bind(DutyLogPage.class).asEagerSingleton();

        bind(DutyLogTableActivity.class).asEagerSingleton();
        bind(AbstractDutyLogTableView.class).to(DutyLogTableView.class).in(Singleton.class);

        bind(DutyLogEditActivity.class).asEagerSingleton();
        bind(AbstractDutyLogEditView.class).to(DutyLogEditView.class).in(Singleton.class);

        bind(AbstractDutyLogFilterView.class).to(DutyLogFilterView.class).in(Singleton.class);
    }
}
