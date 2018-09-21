package ru.protei.portal.ui.document.client.widget.executiontype;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DocumentExecutionType;
import ru.protei.portal.ui.common.client.lang.En_DocumentExecutionTypeLang;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.Arrays;
import java.util.List;

public class DocumentExecutionTypeSelector extends ButtonSelector<En_DocumentExecutionType> implements ModelSelector<En_DocumentExecutionType> {

    @Inject
    void init() {
        setSearchEnabled(false);
        setHasNullValue(false);
        setDisplayOptionCreator(val -> new DisplayOption(lang.getName(val)));
        fillOptions(Arrays.asList(En_DocumentExecutionType.values()));
    }

    @Override
    public void fillOptions(List<En_DocumentExecutionType> options) {
        clearOptions();
        options.forEach(this::addOption);
    }

    @Inject
    En_DocumentExecutionTypeLang lang;
}
