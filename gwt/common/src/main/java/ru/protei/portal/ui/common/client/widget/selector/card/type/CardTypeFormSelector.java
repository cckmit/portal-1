package ru.protei.portal.ui.common.client.widget.selector.card.type;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CardType;
import ru.protei.portal.ui.common.client.widget.form.FormPopupSingleSelector;

public class CardTypeFormSelector extends FormPopupSingleSelector<CardType> {
    @Inject
    public void init(CardTypeModel model) {
        setAsyncModel(model);
        setItemRenderer( value -> value == null ? defaultValue : value.getName() );
    }
}
