package ru.protei.portal.ui.education.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.education.client.activity.admin.AbstractEducationAdminView;
import ru.protei.portal.ui.education.client.activity.admin.EducationAdminActivity;
import ru.protei.portal.ui.education.client.activity.admin.filter.AbstractEducationAdminFilterView;
import ru.protei.portal.ui.education.client.activity.education.AbstractEducationView;
import ru.protei.portal.ui.education.client.activity.education.EducationActivity;
import ru.protei.portal.ui.education.client.activity.entry.edit.AbstractEducationEntryEditView;
import ru.protei.portal.ui.education.client.activity.entry.edit.EducationEntryEditActivity;
import ru.protei.portal.ui.education.client.activity.page.EducationPage;
import ru.protei.portal.ui.education.client.activity.tableworker.AbstractEducationTableWorkerView;
import ru.protei.portal.ui.education.client.activity.tableworker.EducationTableWorkerActivity;
import ru.protei.portal.ui.education.client.activity.wallet.AbstractEducationWalletView;
import ru.protei.portal.ui.education.client.activity.worker.AbstractEducationWorkerView;
import ru.protei.portal.ui.education.client.activity.worker.EducationWorkerActivity;
import ru.protei.portal.ui.education.client.view.admin.EducationAdminView;
import ru.protei.portal.ui.education.client.view.admin.filter.EducationAdminFilterView;
import ru.protei.portal.ui.education.client.view.education.EducationView;
import ru.protei.portal.ui.education.client.view.entry.edit.EducationEntryEditView;
import ru.protei.portal.ui.education.client.view.tableworker.EducationTableWorkerView;
import ru.protei.portal.ui.education.client.view.wallet.EducationWalletView;
import ru.protei.portal.ui.education.client.view.worker.EducationWorkerView;

public class EducationClientModule extends AbstractGinModule {
    @Override
    protected void configure() {

        bind(EducationPage.class).asEagerSingleton();

        bind(EducationActivity.class).asEagerSingleton();
        bind(AbstractEducationView.class).to(EducationView.class).in(Singleton.class);

        bind(EducationWorkerActivity.class).asEagerSingleton();
        bind(AbstractEducationWorkerView.class).to(EducationWorkerView.class).in(Singleton.class);
        bind(AbstractEducationWalletView.class).to(EducationWalletView.class);
        bind(EducationTableWorkerActivity.class).asEagerSingleton();
        bind(AbstractEducationTableWorkerView.class).to(EducationTableWorkerView.class).in(Singleton.class);
        bind(EducationEntryEditActivity.class).asEagerSingleton();
        bind(AbstractEducationEntryEditView.class).to(EducationEntryEditView.class).in(Singleton.class);
        bind(EducationAdminActivity.class).asEagerSingleton();
        bind(AbstractEducationAdminView.class).to(EducationAdminView.class).in(Singleton.class);
        bind(AbstractEducationAdminFilterView.class).to(EducationAdminFilterView.class).in(Singleton.class);
    }
}
