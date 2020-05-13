package ru.protei.portal.ui.absence.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.absence.client.activity.edit.AbsenceEditActivity;
import ru.protei.portal.ui.absence.client.activity.edit.AbstractAbsenceEditView;
import ru.protei.portal.ui.absence.client.view.edit.AbsenceEditView;
import ru.protei.portal.ui.absence.client.widget.selector.AbsenceReasonModel;

/**
 * Описание классов фабрики
 */
public class AbsenceClientModule extends AbstractGinModule {

    @Override
    protected void configure() {

        bind(AbsenceReasonModel.class).asEagerSingleton();

        bind(AbsenceEditActivity.class).asEagerSingleton();
        bind(AbstractAbsenceEditView.class).to(AbsenceEditView.class).in(Singleton.class);
    }
}
