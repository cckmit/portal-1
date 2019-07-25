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
import ru.protei.portal.ui.common.client.service.PersonControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

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
        Long currentPersonId = policyService.getProfile().getId();
        caseType = event.getCaseType();
        fillView(event);

        dialogView.removeButtonVisibility().setVisible(event.getCaseTag() != null && Objects.equals(currentPersonId, event.getCaseTag().getPersonId()));
        dialogView.saveButtonVisibility().setVisible(event.getCaseTag() == null || Objects.equals(currentPersonId, event.getCaseTag().getPersonId()));
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

    private void fillView(CaseTagEvents.Update event) {
        view.name().setValue(event.getTagName());
        view.color().setValue(event.getTagColor());
        view.company().setValue(caseTag != null ? new EntityOption(caseTag.getCompanyName(), caseTag.getCompanyId()) : EntityOption.fromCompany(event.getCompany()));
        view.setVisibleCompanyPanel(event.isCompanyPanelVisible());
        if (caseTag != null) {
            personService.getPersonNames(
                    Collections.singletonList(caseTag.getPersonId()),
                    new FluentCallback<Map<Long, String>>()
                            .withSuccess(map -> {
                                view.setVisibleAuthorPanel(true);
                                view.setAuthor(map.get(caseTag.getPersonId()));
                            }));
        } else {
            view.setVisibleAuthorPanel(false);
            view.setAuthor("");
        }
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
    @Inject
    PersonControllerAsync personService;
    @Inject
    PolicyService policyService;
}
