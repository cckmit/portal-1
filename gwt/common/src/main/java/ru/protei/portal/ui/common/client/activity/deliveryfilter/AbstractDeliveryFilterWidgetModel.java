package ru.protei.portal.ui.common.client.activity.deliveryfilter;

import ru.protei.portal.core.model.dto.DeliveryFilterDto;
import ru.protei.portal.core.model.query.DeliveryQuery;

import java.util.function.Consumer;

public interface AbstractDeliveryFilterWidgetModel {
    void onRemoveClicked(Long id, Runnable afterRemove);
    void onOkSavingFilterClicked(String filterName, DeliveryFilterDto<DeliveryQuery> filledUserFilter,
                                 Consumer<DeliveryFilterDto<DeliveryQuery>> afterSave);
    void onUserFilterChanged(Long id, Consumer<DeliveryFilterDto<DeliveryQuery>> afterChange);
}
