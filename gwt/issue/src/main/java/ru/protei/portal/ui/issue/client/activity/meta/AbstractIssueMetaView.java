package ru.protei.portal.ui.issue.client.activity.meta;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_CaseStateWorkflow;
import ru.protei.portal.core.model.dict.En_TimeElapsedType;
import ru.protei.portal.core.model.dict.En_WorkTrigger;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.ImportanceLevel;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.struct.CaseObjectMetaJira;
import ru.protei.portal.core.model.view.*;
import ru.protei.portal.ui.common.client.selector.AsyncSelectorModel;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;
import ru.protei.portal.ui.common.client.widget.selector.product.ProductModel;
import ru.protei.portal.ui.common.client.widget.timefield.HasTime;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface AbstractIssueMetaView extends IsWidget {

    void setActivity(AbstractIssueMetaActivity activity);

    void setManagerCompany(EntityOption managerCompany);

    EntityOption getManagerCompany();

    void setManagerMandatory(boolean isMandatory);

    void setCaseMetaNotifiers(Set<Person> caseObjectMetaNotifiers);
    void setCaseMetaJira(CaseObjectMetaJira caseObjectMetaJira);
    Set<Person> getCaseMetaNotifiers();

    HasValue<CaseObjectMetaJira> jiraSlaSelector();

    void setSubscriptionEmails(String value);
    void initiatorSelectorAllowAddNew(boolean isVisible);
    void setInitiatorFilter( Long companyId);
    void updateManagersCompanyFilter(Long managerCompanyId);
    void setStateFilter(Selector.SelectorFilter<CaseState> filter);
    void setPlatformFilter(Selector.SelectorFilter<PlatformOption> filter);
    void fillImportanceOptions(List<ImportanceLevel> options);

    void setTimeElapsedType(En_TimeElapsedType timeElapsedType);

    void setInitiator(Person initiator);
    Person getInitiator();

    HasValue<PlatformOption> platform();

    HasVisibility timeElapsedHeaderVisibility();

    void setStateWorkflow(En_CaseStateWorkflow workflow);

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

    void setInitiatorBorderBottomVisible(boolean isVisible);
    void setProductBorderBottomVisible(boolean isVisible);

    HasValue<En_TimeElapsedType> timeElapsedType();
    HasValue<CaseState> state();
    HasValue<ImportanceLevel> importance();

    HasValue<ProductShortView> product();

    void setManager(PersonShortView manager);
    PersonShortView getManager();

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

    HasEnabled managerCompanyEnabled();

    void updateProductsByPlatformIds(Set<Long> platformIds);

    void setProductModel(ProductModel productModel);

    void setProductMandatory(boolean isProductMandatory);

    void setPlanCreatorId(Long creatorId);

    HasValue<Set<PlanOption>> ownerPlans();

    HasVisibility ownerPlansContainerVisibility();

    HasVisibility otherPlansContainerVisibility();

    void setOtherPlans(String value);

    void setPlansLabelVisible(boolean isVisible);

    void setAutoCloseVisible(boolean isVisible);

    HasValue<Boolean> autoClose();

    HasVisibility deadlineContainerVisibility();
    HasValue<Date> deadline();
    boolean isDeadlineEmpty();
    void setDeadlineValid(boolean isValid);

    HasVisibility workTriggerVisibility();
    HasValue<En_WorkTrigger> workTrigger();

    void setCompanyModel(AsyncSelectorModel companyModel);
    void setManagerCompanyModel(AsyncSelectorModel companyModel);
}
