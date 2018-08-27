package ru.protei.portal.ui.common.client.widget.selector.product.product;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.query.ProductQuery;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.ProductEvents;
import ru.protei.portal.ui.common.client.widget.selector.product.BaseModel;

public abstract class ProductModel extends BaseModel implements Activity {

    @Inject
    public void init() {
        query = new ProductQuery();
        query.addType(En_DevUnitType.PRODUCT);
        query.setState(En_DevUnitState.ACTIVE);
        query.setSortField(En_SortField.prod_name);
        query.setSortDir(En_SortDir.ASC);
    }

    @Event
    public void onInit(AuthEvents.Success event) {
        refreshOptions();
    }

    @Event
    public void onProductListChanged( ProductEvents.ChangeModel event ) {
        refreshOptions();
    }

    @Override
    protected void failedToLoad() {
        fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
    }

    @Override
    protected ProductQuery getQuery() {
        return query;
    }

    private ProductQuery query;
}
