package ru.protei.portal.ui.issue.client.activity.create.subtask;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_TextMarkup;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.util.UiResult;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsActivity;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.LocalStorageService;
import ru.protei.portal.ui.common.client.events.IssueEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.IssueControllerAsync;
import ru.protei.portal.ui.common.client.service.TextRenderControllerAsync;
import ru.protei.portal.ui.common.client.widget.selector.company.SubcontractorCompanyModel;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.function.Consumer;

import static ru.protei.portal.core.model.helper.CollectionUtils.setOf;
import static ru.protei.portal.core.model.util.CaseStateUtil.isTerminalState;
import static ru.protei.portal.ui.common.client.common.UiConstants.ISSUE_CREATE_PREVIEW_DISPLAYED;

public abstract class SubtaskCreateActivity implements AbstractSubtaskCreateActivity, AbstractDialogDetailsActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        dialogView.setActivity(this);
        dialogView.removeButtonVisibility().setVisible(false);
        dialogView.setSaveOnEnterClick(false);
        dialogView.getBodyContainer().add(view.asWidget());
        view.setManagerCompanyModel(subcontractorCompanyModel);
    }

    @Event
    public void onShow(IssueEvents.CreateSubtask event) {
        if (!policyService.hasPrivilegeFor(En_Privilege.ISSUE_CREATE)) {
            return;
        }

        dialogView.setHeader(lang.subtaskCreate());
        dialogView.showPopup();

        if (!view.asWidget().isAttached()) {
            return;
        }

        requestParentIssue(event.caseNumber);
    }

    @Override
    public void onSaveClicked() {
        if (!validate()) {
            return;
        }

        CaseObjectCreateRequest createRequest = fillCaseCreateRequest(new CaseObjectCreateRequest());

        issueService.createSubtask(createRequest, parentCaseObject.getId(), new FluentCallback<UiResult<Long>>()
                .withSuccess(createIssueResult -> {
                    onCancelClicked();
                    fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new IssueEvents.Show(false));
                })
        );
    }

    @Override
    public void onRemoveClicked() {}

    @Override
    public void onAdditionalClicked() {}

    @Override
    public void onCancelClicked() {
        parentCaseObject = null;
        dialogView.hidePopup();
    }

    @Override
    public void renderMarkupText(String text, Consumer<String> consumer) {
        textRenderController.render(text, En_TextMarkup.MARKDOWN, new FluentCallback<String>()
                .withError(throwable -> consumer.accept(null))
                .withSuccess(consumer));
    }

    @Override
    public void onDisplayPreviewChanged( String key, boolean isDisplay ) {
        localStorageService.set( ISSUE_CREATE_PREVIEW_DISPLAYED + "_" + key, String.valueOf( isDisplay ) );
    }

    @Override
    public void onManagerCompanyChanged() {
        view.manager().setValue(null);
        view.updateManagersCompanyFilter(view.managerCompany().getValue().getId());
    }

    private void requestParentIssue(Long caseNumber) {
        issueService.getIssue(caseNumber, new FluentCallback<CaseObject>()
                .withSuccess(this::fillView));
    }

    private void fillView(CaseObject caseObject) {
        this.parentCaseObject = caseObject;

        view.name().setValue(null);
        view.description().setValue(null);
        subcontractorCompanyModel.setCompanyId(caseObject.getInitiatorCompanyId());
        view.managerCompany().setValue(EntityOption.fromCompany(policyService.getUserCompany()));
        view.updateManagersCompanyFilter(policyService.getUserCompany().getId());
        view.manager().setValue(null);
        view.setFocusName();
    }

    private boolean validate() {

        if (parentCaseObject == null) {
            fireEvent(new NotifyEvents.Show(lang.errNotFoundParent(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        if (isCompanyWithAutoOpenIssues(parentCaseObject.getInitiatorCompany())) {
            fireEvent(new NotifyEvents.Show(lang.errNotAllowedCompanyWithAutoOpenIssues(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        if (isStateCreatingSubtaskNotAllowed(parentCaseObject)) {
            fireEvent(new NotifyEvents.Show(lang.errNotAllowedParentState(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        boolean isFieldsValid = view.nameValidator().isValid() &&
                view.managerCompanyValidator().isValid();

        if (!isFieldsValid) {
            fireEvent(new NotifyEvents.Show(lang.errSaveSubtaskFieldsInvalid(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        return true;
    }

    private CaseObjectCreateRequest fillCaseCreateRequest(CaseObjectCreateRequest createRequest) {

        CaseObject caseObject = createRequest.getCaseObject();
        caseObject.setName(view.name().getValue());
        caseObject.setInfo(view.description().getValue());
        caseObject.setStateId(view.manager().getValue() == null ? CrmConstants.State.CREATED : CrmConstants.State.OPENED);
        caseObject.setManagerId(view.manager().getValue() == null ? null : view.manager().getValue().getId());
        caseObject.setManagerCompanyId(view.managerCompany().getValue().getId());
        caseObject.setManagerCompanyName(view.managerCompany().getValue().getDisplayText());

        return createRequest;
    }

    private boolean isCompanyWithAutoOpenIssues(Company company) {
        return Boolean.TRUE.equals(company.getAutoOpenIssue());
    }

    private boolean isStateCreatingSubtaskNotAllowed(CaseObject caseObject) {
        return isTerminalState(caseObject.getStateId()) ||
                CrmConstants.State.CREATED == caseObject.getStateId();
    }

    @Inject
    Lang lang;
    @Inject
    AbstractSubtaskCreateView view;
    @Inject
    AbstractDialogDetailsView dialogView;
    @Inject
    TextRenderControllerAsync textRenderController;
    @Inject
    LocalStorageService localStorageService;
    @Inject
    protected PolicyService policyService;
    @Inject
    IssueControllerAsync issueService;
    @Inject
    SubcontractorCompanyModel subcontractorCompanyModel;

    private CaseObject parentCaseObject;
}