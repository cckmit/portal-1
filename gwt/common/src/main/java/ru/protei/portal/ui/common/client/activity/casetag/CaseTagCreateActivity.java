package ru.protei.portal.ui.common.client.activity.casetag;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsActivity;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.CaseTagEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CaseTagControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

public abstract class CaseTagCreateActivity implements Activity, AbstractCaseTagCreateActivity, AbstractDialogDetailsActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        dialogView.setActivity(this);
        dialogView.setHeader(lang.tagCreate());
        dialogView.getBodyContainer().add(view.asWidget());
    }

    @Event
    public void onShow(CaseTagEvents.Create event) {
        caseType = event.getCaseType();
        view.name().setValue("");
        view.color().setValue("");
        view.company().setValue(EntityOption.fromCompany(company));
        if (policyService.hasGrantAccessFor( En_Privilege.ISSUE_VIEW )) {
            view.setVisibleCompanyPanel();
        }
        dialogView.showPopup();
    }

    @Event
    public void onChangeCompany(CaseTagEvents.ChangeCompany event) {
        company = event.getCompany();
    }

    @Override
    public void onSaveClicked() {

        if (!validate()) {
            return;
        }

        CaseTag caseTag = new CaseTag();
        caseTag.setCaseType(caseType);
        caseTag.setName(view.name().getValue());
        caseTag.setColor(view.color().getValue());
        caseTag.setCompanyId(view.company().getValue().getId());

        caseTagController.createTag(caseTag, new FluentCallback<Void>()
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
    private Company company;

    @Inject
    Lang lang;
    @Inject
    PolicyService policyService;
    @Inject
    AbstractCaseTagCreateView view;
    @Inject
    AbstractDialogDetailsView dialogView;
    @Inject
    CaseTagControllerAsync caseTagController;
}
