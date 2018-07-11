package ru.protei.portal.ui.common.client.widget.organization;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_OrganizationCode;
import ru.protei.portal.ui.common.client.lang.En_OrganizationCodeLang;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

public class OrganizationButtonSelector
        extends ButtonSelector<En_OrganizationCode> {

    @Inject
    public void init() {
        setSearchEnabled(false);
        setHasNullValue(false);
        setDisplayOptionCreator(code -> new DisplayOption(this.useLongNames ? lang.getCompanyName(code) : lang.getName(code)));

        fillOptions();
    }

    public void fillOptions() {
        clearOptions();
        for (En_OrganizationCode code : En_OrganizationCode.values())
            addOption(code);
    }

    public void setUseLongNames(boolean useLongNames) {
        this.useLongNames = useLongNames;
        fillOptions();
    }

    @Inject
    En_OrganizationCodeLang lang;

    private boolean useLongNames= false;
}
