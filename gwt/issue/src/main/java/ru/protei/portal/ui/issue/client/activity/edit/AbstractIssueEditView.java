package ru.protei.portal.ui.issue.client.activity.edit;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseStateWorkflow;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.dict.En_TimeElapsedType;
import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.struct.JiraMetaData;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.PlatformOption;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.widget.attachment.list.HasAttachments;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;
import ru.protei.portal.ui.common.client.widget.timefield.HasTime;
import ru.protei.portal.ui.common.client.widget.uploader.AttachmentUploader;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

import java.util.Set;

/**
 * Представление создания и редактирования обращения
 */
public interface AbstractIssueEditView extends IsWidget {

    void setActivity( AbstractIssueEditActivity activity );

    HasValue<String> name();
    HasValue<String> description();
    HasValue<En_CaseState> state();
    HasValue<En_ImportanceLevel> importance();
    HasTime timeElapsedLabel();
    HasTime timeElapsedInput();

    HasValue<En_TimeElapsedType> timeElapsedType();

    HasValue<EntityOption> company();
    HasValue<PersonShortView> initiator();
    HasValue<PersonShortView> manager();
    HasValue<ProductShortView> product();
    HasValue<Boolean> isPrivate();
    HasValue<Set<PersonShortView>> notifiers();
    HasValue<Set<CaseLink>> links();
    HasValue<Set<CaseTag>> tags();
    HasValue<JiraMetaData> jiraSlaSelector();

    HasValidable nameValidator();
    HasValidable stateValidator();
    HasValidable importanceValidator();

    HasVisibility timeElapsedContainerVisibility();

    HasValidable companyValidator();

    HasEnabled initiatorState();

    HasVisibility numberVisibility();
    HasVisibility jiraSlaSelectorVisibility();

    void setSubscriptionEmails(String value);

    HasWidgets getCommentsContainer();
    HasAttachments attachmentsContainer();

    HasVisibility numberContainerVisibility();

    void setFileUploadHandler(AttachmentUploader.FileUploadHandler handler);
    void setCaseNumber(Long caseNumber);

    void setCreatedBy(String value);

    HasVisibility copyVisibility();

    void showComments(boolean isShow);
    boolean isAttached();
    HasValue<PlatformOption> platform();

    HasVisibility platformVisibility();

    void setPrivacyIcon(Boolean isPrivate);

    HasVisibility saveVisibility();

    HasEnabled companyEnabled();
    HasEnabled productEnabled();
    HasEnabled managerEnabled();
    HasEnabled stateEnabled();

    void setNumber(Integer num);

    HasVisibility caseSubscriptionContainer();
    HasVisibility privacyVisibility();
    HasVisibility timeElapsedLabelVisibility();
    HasVisibility timeElapsedEditContainerVisibility();

    HasEnabled saveEnabled();

    Element timeElapsedHeader();

    void setStateFilter(Selector.SelectorFilter<En_CaseState> filter);

    void setPlatformFilter(Selector.SelectorFilter<PlatformOption> filter);

    void initiatorUpdateCompany(Company company);

    void initiatorSelectorAllowAddNew(boolean b);

    void applyCompanyValueIfOneOption();

    void setTagsAddButtonEnabled(boolean enabled);

    void setTagsEditButtonEnabled(boolean enabled);

    void setStateWorkflow(En_CaseStateWorkflow workflow);

    void setDescriptionPreviewAllowed( boolean isPreviewAllowed );

    void switchToRONameDescriptionView(boolean b);

    void setDescriptionRO(String value);

    void setNameRO(String name);

    String DESCRIPTION = "description";
}
