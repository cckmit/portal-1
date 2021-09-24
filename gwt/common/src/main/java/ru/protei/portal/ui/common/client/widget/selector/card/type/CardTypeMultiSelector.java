package ru.protei.portal.ui.common.client.widget.selector.card.type;

import com.google.inject.Inject;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.input.InputPopupMultiSelector;

public class CardTypeMultiSelector extends InputPopupMultiSelector<EntityOption>{

    @Inject
    public void init(CardTypeModel model, Lang lang) {
        setAsyncModel(model);
        setAddName(lang.buttonAdd());
        setClearName( lang.buttonClear() );
        setItemRenderer( value -> value == null ? "" : value.getDisplayText() );
    }

    @Override
    public boolean isValid() {
        return CollectionUtils.isNotEmpty(getValue());
    }

    public void setShowAll(boolean value) {
        if (value) {
            setFilter(null);
        }
    }
}
