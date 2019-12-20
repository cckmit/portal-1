package ru.protei.portal.ui.issue.client.activity.meta;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseStateWorkflow;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.dict.En_TimeElapsedType;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.struct.CaseObjectMetaJira;
import ru.protei.portal.core.model.view.PlatformOption;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

import java.util.Set;

public interface AbstractIssueMetaView extends IsWidget {

    void setActivity(AbstractIssueMetaActivity activity);


    void setCaseMetaNotifiers(Set<Person> caseObjectMetaNotifiers);
    void setCaseMetaJira(CaseObjectMetaJira caseObjectMetaJira);
    Set<Person> getCaseMetaNotifiers();

    HasValue<CaseObjectMetaJira> jiraSlaSelector();

    void setStateWorkflow( En_CaseStateWorkflow workflow);
    void setSubscriptionEmails(String value);
    void initiatorSelectorAllowAddNew(boolean isVisible);
    void initiatorUpdateCompany(Company company);
    void setStateFilter(Selector.SelectorFilter<En_CaseState> filter);
    void setPlatformFilter(Selector.SelectorFilter<PlatformOption> filter);
    void setTimeElapsed(Long timeElapsed);

    void setTimeElapsedType(En_TimeElapsedType timeElapsedType);

    void setInitiator( Person initiator);

    Person getInitiator();

    void setPlatform( PlatformOption platform);

    Long getPlatformId();

    HasVisibility timeElapsedHeaderVisibility();

    HasValidable stateValidator();
    HasValidable importanceValidator();
    HasValidable companyValidator();

    HasEnabled companyEnabled();
    HasEnabled productEnabled();
    HasEnabled managerEnabled();
    HasEnabled stateEnabled();

    HasVisibility caseSubscriptionContainer();
    HasVisibility timeElapsedContainerVisibility();
    HasVisibility timeElapsedEditContainerVisibility();
    HasVisibility platformVisibility();
    HasVisibility jiraSlaSelectorVisibility();

    HasValue<En_TimeElapsedType> timeElapsedType();

    HasValue<En_CaseState> state( );

    HasValue<En_ImportanceLevel> importance(  );


    void setProduct( DevUnit product );

    DevUnit getProduct();

    void setManager( Person manager );

    Person getManager();

    void setCompany( Company company );

    Company getCompany();

    Long getTimeElapsed();
}
