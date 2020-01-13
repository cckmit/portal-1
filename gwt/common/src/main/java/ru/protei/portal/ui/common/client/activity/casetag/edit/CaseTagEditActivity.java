package ru.protei.portal.ui.common.client.activity.casetag.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsActivity;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.CaseTagEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CaseTagControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.Profile;

import java.util.Objects;

public abstract class CaseTagEditActivity implements Activity, AbstractCaseTagEditActivity, AbstractDialogDetailsActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        dialogView.setActivity(this);
        dialogView.getBodyContainer().add(view.asWidget());
    }

    @Event
    public void onShow(CaseTagEvents.Edit event) {
        if ( event.caseTag == null ) {
            fireEvent(new NotifyEvents.Show(lang.error(), NotifyEvents.NotifyType.ERROR));
            return;
        }

        this.caseTag = event.caseTag;

        boolean isCreationMode = caseTag.getId() == null;
        boolean isSameTag = Objects.equals(caseTag.getPersonId(), policyService.getProfile().getId());
        boolean isAllowedChangeCompany = policyService.hasGrantAccessFor(En_Privilege.ISSUE_EDIT);

        // создавать могут все с привилегией ISSUE_EDIT. Заказчики только для своих компаний, сотрудники НТЦ протей для всех.
        // редактируем/удаляем только свои кейсы
        boolean isAllowedEdit = policyService.hasPrivilegeFor(En_Privilege.ISSUE_EDIT) && (isCreationMode || isSameTag);

        view.name().setValue(caseTag.getName());
        view.color().setValue(caseTag.getColor());
        view.setAuthor(caseTag.getPersonName());
        view.authorVisibility().setVisible(caseTag.getId() != null);

        view.colorEnabled().setEnabled(isAllowedEdit);
        view.nameEnabled().setEnabled(isAllowedEdit);

        EntityOption company;
        if (isCreationMode) {
            company = EntityOption.fromCompany(policyService.getUserCompany());
        } else {
            company = new EntityOption(caseTag.getCompanyName(), caseTag.getCompanyId());
        }
        view.company().setValue(company);

        view.companyEnabled().setEnabled(isAllowedChangeCompany);

        dialogView.removeButtonVisibility().setVisible(!isCreationMode && isSameTag);
        dialogView.saveButtonVisibility().setVisible(isAllowedEdit);
        dialogView.setHeader(caseTag.getId() == null ? lang.tagCreate() : lang.tagEdit());
        dialogView.showPopup();
    }

    @Override
    public void onRemoveClicked() {
        caseTagController.removeTag(caseTag, new FluentCallback<Void>()
                .withSuccess(v -> {
                    dialogView.hidePopup();
                    fireEvent(new CaseTagEvents.Remove(caseTag));
                    fireEvent(new CaseTagEvents.ChangeModel());
                })
        );
    }

    @Override
    public void onSaveClicked() {
        if (!validate()) {
            return;
        }

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
        if (caseTag.getCaseType() == null) {
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

    private CaseTag caseTag;
}