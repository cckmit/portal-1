package ru.protei.portal.ui.absencereport.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.absencereport.client.activity.create.AbsenceReportCreateActivity;
import ru.protei.portal.ui.absencereport.client.activity.create.AbstractAbsenceReportCreateView;
import ru.protei.portal.ui.absencereport.client.page.AbsenceReportPage;
import ru.protei.portal.ui.absencereport.client.view.create.AbsenceReportCreateView;

/**
 * Описание классов фабрики
 */
public class AbsenceReportClientModule extends AbstractGinModule {

    @Override
    protected void configure() {

        bind(AbsenceReportPage.class).asEagerSingleton();

        bind(AbsenceReportCreateActivity.class).asEagerSingleton();
        bind(AbstractAbsenceReportCreateView.class).to(AbsenceReportCreateView.class).in(Singleton.class);
    }
}
