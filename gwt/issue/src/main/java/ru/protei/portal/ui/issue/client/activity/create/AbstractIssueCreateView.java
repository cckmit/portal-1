package ru.protei.portal.ui.issue.client.activity.create;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseStateWorkflow;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.dict.En_TimeElapsedType;
import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.core.model.ent.Company;
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

public interface AbstractIssueCreateView extends IsWidget {
    void setActivity(AbstractIssueCreateActivity issueCreateActivity);

    HasValue<String> name();
    HasValue<String> description();
    HasValue<En_CaseState> state();
    HasValue<En_ImportanceLevel> importance();
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

    HasValidable nameValidator();
    HasValidable stateValidator();
    HasValidable importanceValidator();

    HasVisibility timeElapsedContainerVisibility();

    HasValidable companyValidator();

    HasEnabled initiatorState();

    HasEnabled platformState();

    void setSubscriptionEmails(String value);

    HasAttachments attachmentsContainer();

    void setFileUploadHandler(AttachmentUploader.FileUploadHandler handler);

    boolean isAttached();

    HasValue<PlatformOption> platform();

    HasVisibility platformVisibility();

    HasVisibility saveVisibility();

    HasEnabled companyEnabled();
    HasEnabled productEnabled();
    HasEnabled managerEnabled();
    HasEnabled stateEnabled();

    HasVisibility caseSubscriptionContainer();
    HasVisibility privacyVisibility();
    HasVisibility timeElapsedEditContainerVisibility();

    HasEnabled saveEnabled();

    void setStateFilter(Selector.SelectorFilter<En_CaseState> filter);

    void setPlatformFilter(Selector.SelectorFilter<PlatformOption> filter);

    void initiatorUpdateCompany(Company company);

    void initiatorSelectorAllowAddNew(boolean b);

    void applyCompanyValueIfOneOption();

    void setTagsAddButtonEnabled(boolean enabled);

    void setTagsEditButtonEnabled(boolean enabled);

    void setStateWorkflow(En_CaseStateWorkflow workflow);

    void setDescriptionPreviewAllowed( boolean isPreviewAllowed );

    String DESCRIPTION = "description";
}
