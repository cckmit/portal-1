package ru.protei.portal.ui.common.client.widget.deliveryfilter;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dto.CaseFilterDto;
import ru.protei.portal.core.model.query.DeliveryQuery;
import ru.protei.portal.ui.common.client.events.DeliveryEvents;
import ru.protei.portal.ui.common.client.service.CaseFilterControllerAsync;
import ru.protei.portal.ui.common.client.widget.filterwidget.FilterWidgetModel;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.function.Consumer;

public abstract class DeliveryFilterWidgetModel extends FilterWidgetModel<CaseFilterDto<DeliveryQuery>> {
    @Override
    protected void saveFilter(CaseFilterDto<DeliveryQuery> filter,
                              Consumer<Throwable> onError,
                              Consumer<CaseFilterDto<DeliveryQuery>> onSuccess) {

        issueFilterService.saveDeliveryFilter(filter, new FluentCallback<CaseFilterDto<DeliveryQuery>>()
                .withError(onError)
                .withSuccess(onSuccess.andThen( caseFilterDto -> fireEvent(new DeliveryEvents.ChangeUserFilterModel()) ))
        );

    }

    @Override
    protected void changeFilter(Long filter, Consumer<Throwable> onError, Consumer<CaseFilterDto<DeliveryQuery>> onSuccess) {
        issueFilterService.getCaseFilter(filter, new FluentCallback<CaseFilterDto<DeliveryQuery>>()
                .withError(onError)
                .withSuccess(onSuccess.andThen( caseFilterDto -> fireEvent(new DeliveryEvents.ChangeUserFilterModel()) ))
        );
    }

    @Override
    protected void removeFilter(Long filter, Consumer<Throwable> onError, Consumer<Long> onSuccess) {
        issueFilterService.removeCaseFilter(filter, new FluentCallback<Long>()
                .withError(onError)
                .withSuccess(onSuccess.andThen( caseFilterDto -> fireEvent(new DeliveryEvents.ChangeUserFilterModel()) ))
        );
    }

    @Inject
    CaseFilterControllerAsync issueFilterService;
}
