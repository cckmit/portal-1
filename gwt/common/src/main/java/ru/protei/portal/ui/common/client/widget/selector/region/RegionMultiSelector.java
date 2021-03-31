package ru.protei.portal.ui.common.client.widget.selector.region;

import com.google.inject.Inject;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.widget.selector.input.InputPopupMultiSelector;

/**
 * Мультиселектор регионов
 */
public class RegionMultiSelector extends InputPopupMultiSelector<EntityOption> {
    @Inject
    public void init(RegionModelAsync model, Lang lang) {
        setAsyncModel(model);
        setAddName(lang.buttonAdd());
        setClearName(lang.buttonClear());

        setHasNullValue(true);
        setItemRenderer(EntityOption::getDisplayText);

        setNullItem(() -> new EntityOption(lang.regionNotSpecified(), CrmConstants.Product.UNDEFINED));
    }
}
