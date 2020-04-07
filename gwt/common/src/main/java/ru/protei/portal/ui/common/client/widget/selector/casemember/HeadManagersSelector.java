package ru.protei.portal.ui.common.client.widget.selector.casemember;

import com.google.inject.Inject;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.input.InputPopupMultiSelector;

/**
 * Мультиселектор регионов
 */
public class HeadManagersSelector extends InputPopupMultiSelector<PersonShortView> {

    @Inject
    public void init(HeadManagersModelAsync model, Lang lang) {
        setAsyncModel(model);
        setAddName(lang.buttonAdd());
        setClearName(lang.buttonClear());

        setHasNullValue(true);
        setItemRenderer(option -> {
            if (option == null) {
                if (hasNullValue()) {
                    return lang.membersNotSpecified();
                }
                return null;
            }
            return option.getName();
        });
    }
}