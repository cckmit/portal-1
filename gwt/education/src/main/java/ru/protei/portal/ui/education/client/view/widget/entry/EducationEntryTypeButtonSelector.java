package ru.protei.portal.ui.education.client.view.widget.entry;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.EducationEntryType;
import ru.protei.portal.ui.common.client.lang.EducationEntryTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

public class EducationEntryTypeButtonSelector extends ButtonSelector<EducationEntryType> {

    @Inject
    public void init(Lang lang, EducationEntryTypeLang educationEntryTypeLang) {
        setDisplayOptionCreator(value -> new DisplayOption(value == null ? lang.selectValue() : educationEntryTypeLang.getName(value)));
        fillOptions();
    }

    public void fillOptions() {
        clearOptions();
        for (EducationEntryType type : EducationEntryType.values()) {
            addOption(type);
        }
    }
}
