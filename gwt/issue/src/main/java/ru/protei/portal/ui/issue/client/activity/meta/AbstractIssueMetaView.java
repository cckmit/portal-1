package ru.protei.portal.ui.issue.client.activity.meta;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseStateWorkflow;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.struct.CaseObjectMetaJira;
import ru.protei.portal.core.model.view.PlatformOption;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

public interface AbstractIssueMetaView extends IsWidget {

    void setActivity(AbstractIssueMetaActivity activity);

    void setCaseMeta(CaseObjectMeta caseObjectMeta);
    void setCaseMetaNotifiers(CaseObjectMetaNotifiers caseObjectMetaNotifiers);
    void setCaseMetaJira(CaseObjectMetaJira caseObjectMetaJira);
    CaseObjectMeta getCaseMeta();
    CaseObjectMetaNotifiers getCaseMetaNotifiers();
    CaseObjectMetaJira getCaseMetaJira();

    void setStateWorkflow(En_CaseStateWorkflow workflow);
    void applyCompanyValueIfOneOption();
    void setSubscriptionEmails(String value);
    void initiatorSelectorAllowAddNew(boolean isVisible);
    void initiatorUpdateCompany(Company company);
    void setStateFilter(Selector.SelectorFilter<En_CaseState> filter);
    void setPlatformFilter(Selector.SelectorFilter<PlatformOption> filter);
    void setTimeElapsed(Long timeElapsed);
    void setInitiator(Person initiator);
    void setPlatform(Platform platform);

    Element timeElapsedHeader();

    HasValidable stateValidator();
    HasValidable importanceValidator();
    HasValidable companyValidator();

    HasEnabled companyEnabled();
    HasEnabled productEnabled();
    HasEnabled managerEnabled();
    HasEnabled stateEnabled();
    HasEnabled platformEnabled();
    HasEnabled initiatorEnabled();

    HasVisibility caseSubscriptionContainer();
    HasVisibility timeElapsedLabelVisibility();
    HasVisibility timeElapsedContainerVisibility();
    HasVisibility timeElapsedEditContainerVisibility();
    HasVisibility platformVisibility();
    HasVisibility jiraSlaSelectorVisibility();
}
