package ru.protei.portal.ui.common.client.widget.selector.product.devunit;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.query.ProductQuery;

/**
 * Модель селектора продуктов
 */
public abstract class DevUnitModelEdit extends DevUnitModel implements Activity {
    @Inject
    public void init() {
        query = new ProductQuery();
        query.setSortField(En_SortField.prod_name);
        query.setSortDir(En_SortDir.ASC);
        query.setState(En_DevUnitState.ACTIVE);
    }
}
