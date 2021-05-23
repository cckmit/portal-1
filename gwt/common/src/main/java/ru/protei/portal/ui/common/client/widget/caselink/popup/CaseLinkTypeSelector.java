package ru.protei.portal.ui.common.client.widget.caselink.popup;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseLink;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.lang.En_CaseLinkLang;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOptionCreator;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

public class CaseLinkTypeSelector extends ButtonSelector<En_CaseLink> {

    @Inject
    public void init() {
        setDisplayOptionCreator(new DisplayOptionCreator<En_CaseLink>() {

            @Override
            public DisplayOption makeDisplayOption(En_CaseLink value) {
                return new DisplayOption(lang.getCaseLinkName(value));
            }

            @Override
            public DisplayOption makeDisplaySelectedOption(En_CaseLink value) {
                String name = lang.getCaseLinkShortName(value);
                String style = "type-selector-item";
                switch (value) {
                    case CRM: style += " type-selector-crm"; break;
                    case YT: style += " type-selector-youtrack"; break;
                    case UITS: style += " type-selector-uits"; break;
                }
                return new DisplayOption(name, style, null);
            }
        });
    }

    @Inject
    En_CaseLinkLang lang;
}
