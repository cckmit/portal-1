package ru.protei.portal.ui.sitefolder.client.view.platform.widget.selector;

import com.google.inject.Inject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.List;

public class PlatformButtonSelector extends ButtonSelector<EntityOption> implements ModelSelector<EntityOption> {

    @Inject
    void init() {
        platformModel.subscribe(this);
        setSearchEnabled(true);
        setHasNullValue(true);

        setDisplayOptionCreator(value -> new DisplayOption(value == null ? null : value.getDisplayText()));
    }

    @Override
    public void fillOptions(List<EntityOption> options) {
        clearOptions();
        options.forEach(this::addOption);
    }

    @Override
    public void setValue(EntityOption value) {
        if (value != null && value.getId() != null && value.getDisplayText() == null) {
            for (EntityOption option : itemToViewModel.keySet()) {
                if (value.getId().equals(option.getId())) {
                    value = option;
                    break;
                }
            }
        }
        super.setValue(value);
    }

    @Inject
    PlatformModel platformModel;
}
