package ru.protei.portal.ui.common.client.widget.selector.product;

import com.google.inject.Inject;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.input.InputPopupMultiSelector;

/**
 * Мультиселектор комплексов/продуктов
 */
public class ProductMultiSelector extends InputPopupMultiSelector<ProductShortView> {

    @Inject
    public void init(ProductModel model, Lang lang) {
        setAsyncModel(model);
        setItemRenderer(ProductShortView::getName);
        setAddName(lang.buttonAdd());
        setClearName(lang.buttonClear());
    }
}