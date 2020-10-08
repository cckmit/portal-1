package ru.protei.portal.ui.issue.client.activity.create.subtask;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_TextMarkup;
import ru.protei.portal.core.model.ent.CaseInfo;
import ru.protei.portal.core.model.util.UiResult;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsActivity;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.LocalStorageService;
import ru.protei.portal.ui.common.client.events.IssueEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.IssueControllerAsync;
import ru.protei.portal.ui.common.client.service.TextRenderControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.function.Consumer;

import static ru.protei.portal.ui.common.client.common.UiConstants.ISSUE_CREATE_PREVIEW_DISPLAYED;

public abstract class SubtaskCreateActivity implements AbstractSubtaskCreateActivity, AbstractDialogDetailsActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        dialogView.setActivity(this);
        dialogView.removeButtonVisibility().setVisible(false);
        dialogView.setSaveOnEnterClick(false);
        dialogView.getBodyContainer().add(view.asWidget());
    }

    @Event
    public void onShow(IssueEvents.AddSubtask event) {
        if (!policyService.hasPrivilegeFor(En_Privilege.ISSUE_CREATE)) {
            return;
        }

        dialogView.setHeader(lang.issueCreate());
        dialogView.showPopup();

        if (!view.asWidget().isAttached()) {
            return;
        }

        issueService.getIssueShortInfo(event.caseNumber, new FluentCallback<CaseInfo>()
                .withSuccess(this::fillView));
    }

    @Override
    public void onSaveClicked() {
        if (!validateView()) {
            return;
        }

/*
        issueService.createIssue(createRequest, new FluentCallback<UiResult<Long>>()
                .withSuccess(createIssueResult -> {
                    fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new IssueEvents.Show(false));
                })
        );
*/
    }

    @Override
    public void onRemoveClicked() {}

    @Override
    public void onAdditionalClicked() {}

    @Override
    public void onCancelClicked() {
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

    private void fillView(CaseInfo caseInfo) {
        view.name().setValue(null);
        view.description().setValue(null);
    }

    private boolean validateView() {
/*
        if (isCompanyWithAutoOpenIssues(currentCompany) && issueMetaView.product().getValue() == null) {
            fireEvent(new NotifyEvents.Show(lang.errProductNotSelected(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        if (issueMetaView.getCompany() == null) {
            fireEvent(new NotifyEvents.Show(lang.errSaveIssueNeedSelectCompany(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        if (issueMetaView.getManager() == null && isStateWithRestrictions(issueMetaView.state().getValue().getId())) {
            fireEvent(new NotifyEvents.Show(lang.errSaveIssueNeedSelectManager(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        if (issueMetaView.getManager() != null && issueMetaView.product().getValue() == null) {
            fireEvent(new NotifyEvents.Show(lang.errProductNotSelected(), NotifyEvents.NotifyType.ERROR));
            return false;
        }
*/

        boolean isFieldsValid = view.nameValidator().isValid()/* &&
                issueMetaView.companyValidator().isValid()*/;

        if (!isFieldsValid) {
            fireEvent(new NotifyEvents.Show(lang.errSaveIssueFieldsInvalid(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        return true;
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
}