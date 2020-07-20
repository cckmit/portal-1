package ru.protei.portal.ui.common.client.widget.selector.contractor.multicontractor;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.Contractor;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.input.InputPopupMultiSelector;

public class MultiContractorSelector extends InputPopupMultiSelector<Contractor> {
    @Inject
    public void init( MultiContractorModel model, Lang lang ) {
        setAsyncModel(model);
        setAddName(lang.buttonAdd());
        setClearName(lang.buttonClear());

        setItemRenderer( contractor -> contractor.getName() );
    }
}
