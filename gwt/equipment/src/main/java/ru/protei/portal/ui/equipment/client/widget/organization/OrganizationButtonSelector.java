package ru.protei.portal.ui.equipment.client.widget.organization;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_OrganizationCode;
import ru.protei.portal.ui.common.client.lang.En_OrganizationCodeLang;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.Arrays;

public class OrganizationButtonSelector
        extends ButtonSelector<En_OrganizationCode> {

    @Inject
    public void init() {
        setSearchEnabled(false);
        setHasNullValue(false);
        setDisplayOptionCreator(code -> new DisplayOption(lang.getName(code)));

        fillOptions();
    }

    public void fillOptions() {
        Arrays.stream(En_OrganizationCode.values()).forEach(this::addOption);
    }

    @Inject
    En_OrganizationCodeLang lang;
}
