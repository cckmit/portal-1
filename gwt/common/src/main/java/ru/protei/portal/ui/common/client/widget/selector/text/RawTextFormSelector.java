package ru.protei.portal.ui.common.client.widget.selector.text;

import com.google.inject.Inject;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.ui.common.client.widget.form.FormSelector;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.Collection;

public class RawTextFormSelector extends FormSelector<String> {

    @Inject
    public void init() {
        setDisplayOptionCreator(DisplayOption::new);
    }

    public void fillOptions(Collection<String> values) {
        clearOptions();
        CollectionUtils.stream(values).forEach(this::addOption);
    }
}
