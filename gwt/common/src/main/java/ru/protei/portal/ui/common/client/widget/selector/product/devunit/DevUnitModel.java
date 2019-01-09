package ru.protei.portal.ui.common.client.widget.selector.product.devunit;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.query.ProductQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.ProductEvents;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorModel;
import ru.protei.portal.ui.common.client.widget.selector.product.BaseModel;

/**
 * Модель селектора продуктов
 */
public abstract class DevUnitModel extends BaseModel implements Activity {

    @Inject
    public void init() {
        query = new ProductQuery();
        query.setSortField(En_SortField.prod_name);
        query.setSortDir(En_SortDir.ASC);
    }

    @Event
    public void onInit(AuthEvents.Success event) {
//        refreshOptions();
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
