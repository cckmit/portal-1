package ru.protei.portal.ui.common.client.widget.selector.text;

import com.google.inject.Inject;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOptionCreator;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.Collection;

public class RawTextButtonSelector extends ButtonSelector<String> {

    @Inject
    public void init() {
        setDisplayOptionCreator(value -> new DisplayOption(value));
    }

    public void fillOptions(Collection<String> values) {
        clearOptions();
        CollectionUtils.stream(values).forEach(this::addOption);
    }
}
