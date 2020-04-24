package ru.protei.portal.ui.issue.client.activity.meta;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.struct.CaseObjectMetaJira;
import ru.protei.portal.core.model.view.PlatformOption;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;
import ru.protei.portal.ui.common.client.widget.timefield.HasTime;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface AbstractIssueMetaView extends IsWidget {

    void setActivity(AbstractIssueMetaActivity activity);


    void setCaseMetaNotifiers(Set<Person> caseObjectMetaNotifiers);
    void setCaseMetaJira(CaseObjectMetaJira caseObjectMetaJira);
    Set<Person> getCaseMetaNotifiers();

    HasValue<CaseObjectMetaJira> jiraSlaSelector();

    void setStateWorkflow(En_CaseStateWorkflow workflow);
    void setSubscriptionEmails(String value);
    void initiatorSelectorAllowAddNew(boolean isVisible);
    void initiatorUpdateCompany(Company company);
    void setStateFilter(Selector.SelectorFilter<CaseState> filter);
    void setPlatformFilter(Selector.SelectorFilter<PlatformOption> filter);
    void fillImportanceOptions(List<En_ImportanceLevel> options);

    void setTimeElapsedType(En_TimeElapsedType timeElapsedType);
    void setProductTypes(En_DevUnitType... enDevUnitTypes);

    void setInitiator(Person initiator);
    Person getInitiator();

    HasValue<PlatformOption> platform();

    HasVisibility timeElapsedHeaderVisibility();

    HasValidable stateValidator();
    HasValidable importanceValidator();
    HasValidable companyValidator();
    HasValidable initiatorValidator();
    HasValidable managerValidator();
    HasValidable productValidator();

    HasEnabled stateEnabled();
    HasEnabled importanceEnabled();
    HasEnabled productEnabled();
    HasEnabled managerEnabled();
    HasEnabled companyEnabled();
    HasEnabled initiatorEnabled();
    HasEnabled platformEnabled();
    HasEnabled caseMetaNotifiersEnabled();
    HasEnabled caseMetaJiraEnabled();

    HasVisibility caseSubscriptionContainer();
    HasVisibility timeElapsedContainerVisibility();
    HasVisibility timeElapsedEditContainerVisibility();
    HasVisibility platformVisibility();
    HasVisibility jiraSlaSelectorVisibility();

    HasValue<En_TimeElapsedType> timeElapsedType();
    HasValue<CaseState> state();
    HasValue<En_ImportanceLevel> importance();

    void setProduct(DevUnit product);
    DevUnit getProduct();

    void setManager(Person manager);
    Person getManager();

    void setCompany(Company company);
    Company getCompany();

    void setTimeElapsed(Long timeElapsed);
    Long getTimeElapsed();

    void setJiraInfoLink(String link);

    HasTime slaReactionTime();

    HasTime slaTemporarySolutionTime();

    HasTime slaFullSolutionTime();

    HasVisibility slaContainerVisibility();

    void setValuesContainerWarning(boolean isWarning);

    void setSlaTimesContainerTitle(String title);

    HasVisibility pauseDateContainerVisibility();

    HasValue<Date> pauseDate();

    void setPauseDateValid(boolean isValid);
}
