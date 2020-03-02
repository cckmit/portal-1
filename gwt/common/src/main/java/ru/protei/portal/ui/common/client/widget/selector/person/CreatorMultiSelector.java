package ru.protei.portal.ui.common.client.widget.selector.person;

import com.google.inject.Inject;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.input.InputPopupMultiSelector;

public class CreatorMultiSelector extends InputPopupMultiSelector<PersonShortView> {
    @Inject
    public void init(PersonModel model, Lang lang) {
        setAsyncModel(model);
        setItemRenderer( value -> value == null ? "" : value.getName() );
        setAddName( lang.buttonAdd() );
        setClearName( lang.buttonClear() );
    }
}
