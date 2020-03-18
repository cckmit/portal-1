package ru.protei.portal.ui.education.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.education.client.activity.education.AbstractEducationView;
import ru.protei.portal.ui.education.client.activity.education.EducationActivity;
import ru.protei.portal.ui.education.client.activity.page.EducationPage;
import ru.protei.portal.ui.education.client.view.education.EducationView;

public class EducationClientModule extends AbstractGinModule {
    @Override
    protected void configure() {

        bind(EducationPage.class).asEagerSingleton();

        bind(EducationActivity.class).asEagerSingleton();
        bind(AbstractEducationView.class).to(EducationView.class).in(Singleton.class);

    }
}
