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
    public void onShow(CaseTagEvents.Update event) {
        this.caseTag = event.getCaseTag();
        caseType = event.getCaseType();
        view.name().setValue(event.getTagName());
        view.color().setValue(event.getTagColor());
        view.company().setValue(caseTag != null ? new EntityOption(caseTag.getCompanyName(), caseTag.getCompanyId()) : new EntityOption());
        view.setVisibleCompanyPanel(event.isCompanyPanelVisible());
        dialogView.removeButtonVisibility().setVisible(!event.getTagName().isEmpty());
        dialogView.setHeader(event.getTagName().isEmpty() ? lang.tagCreate() : lang.tagEdit());
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

    @Inject
    Lang lang;
    @Inject
    AbstractCaseTagEditView view;
    @Inject
    AbstractDialogDetailsView dialogView;
    @Inject
    CaseTagControllerAsync caseTagController;
}
