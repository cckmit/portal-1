package ru.protei.portal.ui.common.client.widget.selector.card.type;

import com.google.inject.Inject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.selector.SelectorItem;
import ru.protei.portal.ui.common.client.selector.popup.item.PopupSelectorItem;
import ru.protei.portal.ui.common.client.widget.form.FormPopupSingleSelector;

public class CardTypeOptionSelector extends FormPopupSingleSelector<EntityOption>
{

    @Inject
    public void init(CardTypeOptionModel model) {
        model.setDisplay(true);
        setAsyncModel(model);
        setItemRenderer( value -> value == null ? defaultValue : value.getDisplayText() );
    }

    @Override
    protected SelectorItem<EntityOption> makeSelectorItem(EntityOption value, String elementHtml ) {
        PopupSelectorItem<EntityOption> item = new PopupSelectorItem<>();
        item.setName(elementHtml);
        return item;
    }

}
