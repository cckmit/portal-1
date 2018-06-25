package ru.protei.portal.ui.common.client.widget.issuelinks.popup;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseLink;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.lang.En_CaseLinkLang;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;
import ru.protei.portal.ui.common.client.widget.togglebtn.group.ToggleBtnGroup;

public class IssueLinksTypeSelector extends ButtonSelector<En_CaseLink> {

    @Inject
    public void init() {
        setDisplayOptionCreator(value -> {
            String name = lang.getCaseLinkName(value);
            String style = "type-selector-item";
            switch (value) {
                case CRM: style += " type-selector-crm"; break;
                case CRM_OLD: style += " type-selector-crm-old"; break;
                case YT: style += " type-selector-youtrack"; break;
            }
            return new DisplayOption(name, style, null);
        });
    }

    public void fillOptions() {
        clearOptions();
        boolean isGranted = policyService.hasGrantAccessFor(En_Privilege.ISSUE_VIEW);
        for (En_CaseLink value : En_CaseLink.values()) {
            if (!isGranted && value.isForcePrivacy()) {
                continue;
            }
            addOption(value);
        }
    }

    @Inject
    En_CaseLinkLang lang;
    @Inject
    PolicyService policyService;
}
