package ru.protei.portal.ui.common.client.activity.casetag.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsActivity;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.NameStatus;
import ru.protei.portal.ui.common.client.events.CaseTagEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CaseTagControllerAsync;
import ru.protei.portal.ui.common.client.widget.colorpicker.ColorPicker;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.Objects;

import static ru.protei.portal.core.model.helper.StringUtils.isBlank;
import static ru.protei.portal.ui.common.client.common.NameStatus.*;

public abstract class CaseTagEditActivity implements Activity, AbstractCaseTagEditActivity, AbstractDialogDetailsActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        dialogView.setActivity(this);
        dialogView.getBodyContainer().add(view.asWidget());
    }

    @Event
    public void onShow(CaseTagEvents.ShowEdit event) {
        if ( event.caseTag == null ) {
            fireEvent(new NotifyEvents.Show(lang.error(), NotifyEvents.NotifyType.ERROR));
            return;
        }

        this.caseTag = event.caseTag;
        this.caseType = event.caseType;

        boolean isCreationMode = caseTag.getId() == null;
        boolean isTagOwner = Objects.equals(caseTag.getPersonId(), policyService.getProfile().getId());
        boolean isSystemScope = policyService.hasSystemScopeForPrivilege(privilegeByCaseType(caseType));
        boolean isAllowedChangeCompany = isCompanySelectorEnabled(caseType) && isSystemScope && (isCreationMode || isTagOwner) ;

        // создавать могут все с привилегией по типу кейса. Заказчики только для своих компаний, сотрудники НТЦ протей для всех.
        // редактируем/удаляем только свои кейсы
        boolean isAllowedEdit = policyService.hasPrivilegeFor(privilegeByCaseType(caseType)) && (isCreationMode || isTagOwner);

        view.name().setValue(caseTag.getName());
        view.color().setValue(caseTag.getColor());
        view.setAuthor(caseTag.getPersonName());
        view.authorVisibility().setVisible(caseTag.getId() != null);

        view.colorEnabled().setEnabled(isAllowedEdit);
        view.nameEnabled().setEnabled(isAllowedEdit);

        resetNameValidationStatus();

        EntityOption company;
        if (isCreationMode) {
            company = EntityOption.fromCompany(policyService.getUserCompany());
        } else {
            company = new EntityOption(caseTag.getCompanyName(), caseTag.getCompanyId());
        }
        view.company().setValue(company);

        view.companyEnabled().setEnabled(isAllowedChangeCompany);

        dialogView.removeButtonVisibility().setVisible(!isCreationMode && isTagOwner);
        dialogView.saveButtonVisibility().setVisible(isAllowedEdit);
        dialogView.setHeader(caseTag.getId() == null ? lang.tagCreate() : lang.tagEdit());
        dialogView.showPopup();
    }

    @Override
    public void onRemoveClicked() {
        caseTagController.removeTag(caseTag.getId(), new FluentCallback<Long>()
                .withSuccess(v -> {
                    dialogView.hidePopup();
                    fireEvent(new CaseTagEvents.Removed(caseTag));
                })
        );
    }

    @Override
    public void onSaveClicked() {
        String errMsg = validateTagParams();
        if (errMsg != null) {
            showErrorMessage(errMsg);
            return;
        }

        caseTag.setName(view.name().getValue());
        caseTag.setColor(view.color().getValue());
        caseTag.setCompanyId(view.company().getValue().getId());

        if (isNew( caseTag )) {
            caseTagController.create( caseTag, new FluentCallback<Long>()
                    .withSuccess( id -> {
                        caseTag.setId( id );
                        dialogView.hidePopup();
                        fireEvent( new CaseTagEvents.Created( caseTag ) );
                    } )
            );
            return;
        }

        caseTagController.update(caseTag, new FluentCallback<Long>()
                .withSuccess(id -> {
                    dialogView.hidePopup();
                    fireEvent(new CaseTagEvents.Changed(caseTag));
                })
        );
    }

    @Override
    public void onCancelClicked() {
        dialogView.hidePopup();
    }

    @Override
    public void onChangeCaseTagName() {
        String name = view.name().getValue();
        if (isBlank(name)) {
            setNameValidationStatus(ERROR, lang.errTagNameEmpty(), true);
            return;
        }

        CaseTag caseTagDto = new CaseTag();
        caseTagDto.setName(name);
        caseTagDto.setCompanyId(view.company().getValue().getId());
        caseTagDto.setCaseType(caseTag.getCaseType());

        caseTagController.isTagNameExists(caseTagDto, new RequestCallback<Boolean>() {
            @Override
            public void onError(Throwable throwable) {}

            @Override
            public void onSuccess(Boolean isExists) {
                if (isExists) {
                    setNameValidationStatus(ERROR, lang.errTagNameAlreadyExists(), true);
                } else {
                    setNameValidationStatus(SUCCESS, "", false);
                }
            }
        });
    }

    private String validateTagParams() {
        if (isBlank(view.name().getValue())) {
            setNameValidationStatus(ERROR, lang.errTagNameEmpty(), true);
            return lang.errTagNameEmpty();
        }

        String tagNameErrorMsg = view.caseTagNameErrorLabel().getText();
        if (!tagNameErrorMsg.isEmpty()) {
            return tagNameErrorMsg;
        }

        String color = view.color().getValue();
        if (isBlank(color)) {
            return lang.errTagColorEmpty();
        }

        if (!ColorPicker.isValidHexColor(color)) {
            return lang.errTagColorIncorrectFormat();
        }

        if (caseTag.getCaseType() == null) {
            return null;
        }

        return null;
    }

    private void resetNameValidationStatus() {
        view.setCaseTagNameStatus(NONE);
        view.caseTagNameErrorLabel().setText("");
        view.caseTagNameErrorLabelVisibility().setVisible(false);
    }

    private void setNameValidationStatus(NameStatus status, String message, boolean isVisible) {
        view.setCaseTagNameStatus(status);
        view.caseTagNameErrorLabel().setText(message);
        view.caseTagNameErrorLabelVisibility().setVisible(isVisible);
    }

    private void showErrorMessage(String message) {
        fireEvent(new NotifyEvents.Show(message, NotifyEvents.NotifyType.ERROR));
    }

    private boolean isNew( CaseTag caseTag ) {
        return caseTag!=null && caseTag.getId() == null;
    }

    private boolean isCompanySelectorEnabled(En_CaseType caseType) {
        switch (caseType) {
            case CONTRACT: return false;
        }
        return true;
    }

    private En_Privilege privilegeByCaseType(En_CaseType caseType) {
        switch (caseType) {
            case CRM_SUPPORT: return En_Privilege.ISSUE_EDIT;
            case CONTRACT: return En_Privilege.CONTRACT_EDIT;
        }
        return En_Privilege.ISSUE_EDIT;
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
    private En_CaseType caseType;
}
