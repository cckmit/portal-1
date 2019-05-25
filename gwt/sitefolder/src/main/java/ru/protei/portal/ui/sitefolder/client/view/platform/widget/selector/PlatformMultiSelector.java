package ru.protei.portal.ui.sitefolder.client.view.platform.widget.selector;

import com.google.inject.Inject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.client.widget.selector.input.MultipleInputSelector;

import java.util.List;

public class PlatformMultiSelector extends MultipleInputSelector<EntityOption> implements SelectorWithModel<EntityOption> {

    @Inject
    public void init(PlatformModel model, Lang lang) {
        setSelectorModel(model);
        setAddName(lang.buttonAdd());
        setClearName( lang.buttonClear() );
    }

    public void fillOptions(List<EntityOption> options) {
        clearOptions();
        for (EntityOption option : options) {
            addOption(option.getDisplayText(), option);
        }
        reselectValuesIfNeeded();
    }
}
