package ru.protei.portal.ui.common.client.widget.selector.contractor.filtercontractor;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.Contractor;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.client.widget.selector.input.MultipleInputSelector;

import java.util.List;

public class MultiContractorSelector extends MultipleInputSelector<Contractor> implements SelectorWithModel<Contractor> {
    @Inject
    public void init( MultiContractorModel model, Lang lang ) {
        setSelectorModel(model);
        setAddName(lang.buttonAdd());
        setClearName(lang.buttonClear());
    }

    @Override
    public void fillOptions(List<Contractor> options) {
        clearOptions();
        options.forEach(option -> addOption(option.getName(), option));
    }
}
