package ru.protei.portal.ui.common.client.activity.deliveryfilter;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.protei.portal.core.model.dto.DeliveryFilterDto;
import ru.protei.portal.core.model.query.DeliveryQuery;
import ru.protei.portal.ui.common.client.events.ConfirmDialogEvents;
import ru.protei.portal.ui.common.client.events.DeliveryEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CaseFilterControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.function.Consumer;

import static ru.protei.portal.core.model.helper.StringUtils.isEmpty;

public abstract class DeliveryFilterWidgetModel implements Activity, AbstractDeliveryFilterWidgetModel {

//    @Inject
//    public void onInit() {
//
//    }

    @Override
    public void onRemoveClicked(Long id, Runnable afterRemove) {
        fireEvent(new ConfirmDialogEvents.Show(lang.deliveryFilterRemoveConfirmMessage(), removeAction(id, afterRemove)));
    }

    @Override
    public void onOkSavingFilterClicked(String filterName, DeliveryFilterDto<DeliveryQuery> filledUserFilter,
                                        Consumer<DeliveryFilterDto<DeliveryQuery>> afterSave) {
        if (isEmpty(filterName)) {
            fireEvent(new NotifyEvents.Show(lang.errFilterNameRequired(), NotifyEvents.NotifyType.ERROR));
            return;
        }

        //TODO create DeliveryFilterService
//        filterService.saveIssueFilter(filledUserFilter, new FluentCallback<DeliveryFilterDto<DeliveryQuery>>()
//                .withError((throwable, defaultErrorHandler, status) -> {
//                    fireEvent(new NotifyEvents.Show(lang.errSaveIssueFilter(), NotifyEvents.NotifyType.ERROR));
//                    defaultErrorHandler.accept(throwable);
//                })
//                .withSuccess(filter -> {
//                    fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
//                    fireEvent(new DeliveryEvents.ChangeUserFilterModel());
//
//                    afterSave.accept(filter);
//                })
//        );
    }

    @Override
    public void onUserFilterChanged(Long id, Consumer<DeliveryFilterDto<DeliveryQuery>> afterChange) {
        //TODO create DeliveryFilterService
//        filterService.getCaseFilter(id, new FluentCallback<DeliveryFilterDto<DeliveryQuery>>()
//                .withErrorMessage(lang.errNotFound())
//                .withSuccess(afterChange)
//        );
    }

    private Runnable removeAction(Long filterId, Runnable afterRemove) {
        return () -> filterService.removeCaseFilter(filterId, new FluentCallback<Long>()
                .withSuccess(result -> {
                    fireEvent(new NotifyEvents.Show(lang.deliveryFilterRemoveSuccessed(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new DeliveryEvents.ChangeUserFilterModel());
                    afterRemove.run();
                }));
    }

    @Inject
    Lang lang;
    @Inject
    CaseFilterControllerAsync filterService;
}
