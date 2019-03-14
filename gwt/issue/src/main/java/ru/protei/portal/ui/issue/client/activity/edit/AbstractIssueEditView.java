package ru.protei.portal.ui.issue.client.activity.edit;

import com.google.gwt.user.client.ui.*;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.dict.En_TimeElapsedType;
import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
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
    HasText description();
    HasValue<En_CaseState> state();
    HasValue<En_ImportanceLevel> importance();
    HasTime timeElapsedLabel();
    HasTime timeElapsedInput();

    HasValue<En_TimeElapsedType> timeElapsedType();

    HasValue<EntityOption> company();
    HasValue<PersonShortView> initiator();
    HasValue<PersonShortView> manager();
    HasValue<ProductShortView> product();
    HasValue<Boolean> isLocal();
    HasValue<Set<PersonShortView>> notifiers();
    HasValue<Set<CaseLink>> links();

    HasValidable nameValidator();
    HasValidable stateValidator();
    HasValidable importanceValidator();

    HasVisibility timeElapsedContainerVisibility();

    HasValidable companyValidator();
    HasValidable initiatorValidator();
    HasValidable productValidator();
    HasValidable managerValidator();

    HasEnabled initiatorState();

    HasVisibility numberVisibility();

    HasValue<Integer> number();

    void setSubscriptionEmails(String value);

    HasWidgets getCommentsContainer();
    HasAttachments attachmentsContainer();
    void setFileUploadHandler(AttachmentUploader.FileUploadHandler handler);
    void setCaseNumber(Long caseNumber);

    void showComments(boolean isShow);
    boolean isAttached();


    HasVisibility saveVisibility();

    HasEnabled companyEnabled();
    HasEnabled productEnabled();
    HasEnabled managerEnabled();
    HasEnabled stateEnabled();

    HasVisibility caseSubscriptionContainer();
    HasVisibility privacyVisibility();
    HasVisibility timeElapsedLabelVisibility();
    HasVisibility timeElapsedInputVisibility();
    void setTimeElapseTypeVisibility( boolean isVisible );


    HasEnabled saveEnabled();

    void setStateFilter(Selector.SelectorFilter<En_CaseState> filter);

    void initiatorUpdateCompany(Company company);

    void initiatorSelectorAllowAddNew(boolean b);

    void applyCompanyValueIfOneOption();

}
