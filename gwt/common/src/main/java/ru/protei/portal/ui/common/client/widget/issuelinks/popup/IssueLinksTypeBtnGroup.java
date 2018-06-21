package ru.protei.portal.ui.common.client.widget.issuelinks.popup;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseLink;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.togglebtn.group.ToggleBtnGroup;

public class IssueLinksTypeBtnGroup extends ToggleBtnGroup<En_CaseLink> {

    public IssueLinksTypeBtnGroup() {
        super();
    }

    public void fillOptions() {
        clear();
        boolean isGranted = policyService.hasGrantAccessFor(En_Privilege.ISSUE_VIEW);
        for (En_CaseLink type : En_CaseLink.values()) {
            if (!isGranted && type.isForcePrivacy()) {
                continue;
            }
            addBtn(type.getName(lang), type, "btn btn-white");
        }
    }

    @Inject
    Lang lang;
    @Inject
    PolicyService policyService;
}
