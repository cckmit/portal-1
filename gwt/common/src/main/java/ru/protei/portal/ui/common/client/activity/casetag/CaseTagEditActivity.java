package ru.protei.portal.ui.common.client.activity.casetag;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsActivity;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.CaseTagEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CaseTagControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

public abstract class CaseTagEditActivity implements Activity, AbstractCaseTagEditActivity, AbstractDialogDetailsActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        dialogView.setActivity(this);
        dialogView.getBodyContainer().add(view.asWidget());
    }

    @Event
    public void onShow(CaseTagEvents.Readonly event) {
        this.caseTag = event.getCaseTag();
        this.isReadOnly = true;
        fillView(caseTag);

        dialogView.removeButtonVisibility().setVisible(false);
        dialogView.saveButtonVisibility().setVisible(false);
        dialogView.setHeader(lang.tagInfo());
        dialogView.showPopup();
    }

    @Event
    public void onShow(CaseTagEvents.Update event) {
        this.caseTag = event.getCaseTag();
        this.isReadOnly = false;
        this.isCompanyPanelVisible = event.isCompanyPanelVisible();
        caseType = event.getCaseTag().getCaseType();
        fillView(caseTag);

        dialogView.saveButtonVisibility().setVisible(true);
        dialogView.removeButtonVisibility().setVisible(caseTag.getId() != null);
        dialogView.setHeader(caseTag.getId() == null ? lang.tagCreate() : lang.tagEdit());
        dialogView.showPopup();
    }

    @Override
    public void onRemoveClicked() {
        caseTagController.removeTag(caseTag, new FluentCallback<Void>()
                .withSuccess(v -> {
                    dialogView.hidePopup();
                    fireEvent(new CaseTagEvents.ChangeModel());
                })
        );
    }

    @Override
    public void onSaveClicked() {

        if (!validate()) {
            return;
        }

        CaseTag caseTag = this.caseTag == null ? new CaseTag() : this.caseTag;
        caseTag.setCaseType(caseType);
        caseTag.setName(view.name().getValue());
        caseTag.setColor(view.color().getValue());
        caseTag.setCompanyId(view.company().getValue().getId());

        caseTag.setPersonId(policyService.getProfile().getId());

        caseTagController.saveTag(caseTag, new FluentCallback<Void>()
                .withSuccess(v -> {
                    dialogView.hidePopup();
                    fireEvent(new CaseTagEvents.ChangeModel());
                })
        );
    }

    @Override
    public void onCancelClicked() {
        dialogView.hidePopup();
    }


    private void fillView(CaseTag caseTag) {
        view.name().setValue(caseTag.getName());
        view.color().setValue(caseTag.getColor());
        view.company().setValue(caseTag.getCompanyName() != null && caseTag.getCompanyId() != null ? new EntityOption(caseTag.getCompanyName(), caseTag.getCompanyId()) : EntityOption.fromCompany(null));
        view.setVisibleAuthorPanel(caseTag.getPersonName() != null);
        view.setAuthor(caseTag.getPersonName());
        view.setVisibleCompanyPanel(isReadOnly || isCompanyPanelVisible);
        view.colorEnabled().setEnabled(!isReadOnly);
        view.nameEnabled().setEnabled(!isReadOnly);
        view.companyEnabled().setEnabled(!isReadOnly);
    }

    private boolean validate() {
        if (caseType == null) {
            return false;
        }
        if (StringUtils.isBlank(view.name().getValue())) {
            return false;
        }
        if (StringUtils.isBlank(view.color().getValue())) {
            return false;
        }
        return true;
    }

    private En_CaseType caseType;
    private CaseTag caseTag;
    private boolean isReadOnly;
    private boolean isCompanyPanelVisible;

    @Inject
    Lang lang;
    @Inject
    AbstractCaseTagEditView view;
    @Inject
    AbstractDialogDetailsView dialogView;
    @Inject
    CaseTagControllerAsync caseTagController;
    @Inject
    PolicyService policyService;
}
