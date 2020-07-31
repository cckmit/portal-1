package ru.protei.portal.ui.absence.client.widget;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.AbsenceFilter;
import ru.protei.portal.ui.common.client.events.AbsenceFilterEvents;
import ru.protei.portal.ui.common.client.service.AbsenceFilterControllerAsync;
import ru.protei.portal.ui.common.client.widget.filterwidget.FilterWidgetModel;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.function.Consumer;

public abstract class AbsenceFilterWidgetModel extends FilterWidgetModel<AbsenceFilter> {

    @Override
    protected void saveFilter(AbsenceFilter filter, Consumer<Throwable> onError, Consumer<AbsenceFilter> onSuccess) {
        filterService.saveFilter(filter, new FluentCallback<AbsenceFilter>()
                .withError(onError)
                .withSuccess(onSuccess.andThen(f -> fireEvent(new AbsenceFilterEvents.Changed())))
        );
    }

    @Override
    protected void changeFilter(Long filter, Consumer<Throwable> onError, Consumer<AbsenceFilter> onSuccess) {
        filterService.getFilter(filter, new FluentCallback<AbsenceFilter>()
                .withError(onError)
                .withSuccess(onSuccess.andThen(f -> fireEvent(new AbsenceFilterEvents.Changed())))
        );
    }

    @Override
    protected void removeFilter(Long filter, Consumer<Throwable> onError, Consumer<Boolean> onSuccess) {
        filterService.removeFilter(filter, new FluentCallback<Boolean>()
                .withError(onError)
                .withSuccess(onSuccess.andThen(f -> fireEvent(new AbsenceFilterEvents.Changed())))
        );
    }

    @Inject
    AbsenceFilterControllerAsync filterService;
}
