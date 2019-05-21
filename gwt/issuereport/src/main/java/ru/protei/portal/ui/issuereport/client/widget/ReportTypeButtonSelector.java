package ru.protei.portal.ui.issuereport.client.widget;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_ReportType;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.lang.En_ReportTypeLang;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

public class ReportTypeButtonSelector extends ButtonSelector<En_ReportType> {

    @Inject
    public void init() {
        setDisplayOptionCreator(o -> new DisplayOption(lang.getType(o)));
        fillOptions();
    }

    public void fillOptions() {
        clearOptions();
        if (!policyService.hasGrantAccessFor(En_Privilege.ISSUE_VIEW)) {
            addOption(En_ReportType.CASE_OBJECTS);
            return;
        }
        for (En_ReportType rt : En_ReportType.values()) {
            addOption(rt);
        }
    }

    @Inject
    private En_ReportTypeLang lang;

    @Inject
    PolicyService policyService;
}
