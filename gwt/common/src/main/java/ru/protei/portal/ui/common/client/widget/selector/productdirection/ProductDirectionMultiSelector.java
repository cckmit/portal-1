package ru.protei.portal.ui.common.client.widget.selector.productdirection;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dto.ProductDirectionInfo;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.input.InputPopupMultiSelector;

public class ProductDirectionMultiSelector extends InputPopupMultiSelector<ProductDirectionInfo> {

    @Inject
    public void init(ProductDirectionModelAsync model, Lang lang) {
        setAsyncModel(model);
        setAddName(lang.buttonAdd());
        setClearName(lang.buttonClear());

        setHasNullValue(true);
        setItemRenderer(option -> {
            if (option == null) {
                if (hasNullValue()) {
                    return lang.productDirectionNotSpecified();
                }
                return null;
            }
            return option.name;
        });
    }
}