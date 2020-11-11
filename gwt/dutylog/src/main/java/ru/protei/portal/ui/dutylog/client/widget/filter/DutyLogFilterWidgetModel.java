package ru.protei.portal.ui.dutylog.client.widget.filter;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.DutyLogFilter;
import ru.protei.portal.ui.common.client.events.DutyLogFilterEvents;
import ru.protei.portal.ui.common.client.service.DutyLogFilterControllerAsync;
import ru.protei.portal.ui.common.client.widget.filterwidget.FilterWidgetModel;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.function.Consumer;

public abstract class DutyLogFilterWidgetModel extends FilterWidgetModel<DutyLogFilter> {

    @Override
    protected void saveFilter(DutyLogFilter filter, Consumer<Throwable> onError, Consumer<DutyLogFilter> onSuccess) {
        filterService.saveFilter(filter, new FluentCallback<DutyLogFilter>()
                .withError(onError)
                .withSuccess(onSuccess.andThen(f -> fireEvent(new DutyLogFilterEvents.Changed())))
        );
    }

    @Override
    protected void changeFilter(Long filter, Consumer<Throwable> onError, Consumer<DutyLogFilter> onSuccess) {
        filterService.getFilter(filter, new FluentCallback<DutyLogFilter>()
                .withError(onError)
                .withSuccess(onSuccess.andThen(f -> fireEvent(new DutyLogFilterEvents.Changed())))
        );
    }

    @Override
    protected void removeFilter(Long filter, Consumer<Throwable> onError, Consumer<Long> onSuccess) {
        filterService.removeFilter(filter, new FluentCallback<Long>()
                .withError(onError)
                .withSuccess(onSuccess.andThen(f -> fireEvent(new DutyLogFilterEvents.Changed())))
        );
    }

    @Inject
    DutyLogFilterControllerAsync filterService;
}
