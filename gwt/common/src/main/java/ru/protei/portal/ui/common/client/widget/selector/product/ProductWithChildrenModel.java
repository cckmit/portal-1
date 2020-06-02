package ru.protei.portal.ui.common.client.widget.selector.product;

import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.query.ProductQuery;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.selector.cache.SelectorDataCacheLoadHandler;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.List;

public abstract class ProductWithChildrenModel extends ProductModel {
    protected SelectorDataCacheLoadHandler<ProductShortView> makeLoadHandler(final ProductQuery query) {
        return (offset, limit, asyncCallback) -> {
            query.setOffset(offset);
            query.setLimit(limit);
            productService.getProductViewListWithChildren(query, En_DevUnitType.PRODUCT, new RequestCallback<List<ProductShortView>>() {
                @Override
                public void onError(Throwable throwable) {
                    fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                }

                @Override
                public void onSuccess(List<ProductShortView> options) {
                    asyncCallback.onSuccess(options);
                }
            });
        };
    }
}
