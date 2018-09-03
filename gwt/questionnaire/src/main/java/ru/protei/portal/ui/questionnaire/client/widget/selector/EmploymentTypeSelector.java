package ru.protei.portal.ui.questionnaire.client.widget.selector;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_EmploymentType;
import ru.protei.portal.ui.common.client.lang.En_EmploymentTypeLang;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

public class EmploymentTypeSelector extends ButtonSelector<En_EmploymentType> {

    @Inject
    public void init() {
        setDisplayOptionCreator(o -> new DisplayOption(lang.getName(o)));
        fillOptions();
    }

    public void fillOptions() {
        clearOptions();

        for(En_EmploymentType ct : En_EmploymentType.values())
            addOption(ct);
    }

    @Inject
    private En_EmploymentTypeLang lang;
}
