package ru.protei.portal.ui.absence.client.activity.report;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsActivity;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.events.AbsenceEvents;
import ru.protei.portal.ui.common.client.events.ErrorPageEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.AbsenceControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.Date;

import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.WIDE_MODAL;

public abstract class AbsenceReportCreateActivity implements AbstractAbsenceReportCreateActivity, AbstractDialogDetailsActivity, Activity {

    @PostConstruct
    public void onInit() {
        dialogView.setActivity(this);
        dialogView.addStyleName(WIDE_MODAL);
        dialogView.setHeader(lang.absenceReport());
        dialogView.removeButtonVisibility().setVisible(false);
        dialogView.setSaveOnEnterClick(false);
        dialogView.setSaveButtonName(lang.buttonSend());
        dialogView.getBodyContainer().add(view.asWidget());
    }

    @Event
    public void onShow(AbsenceEvents.CreateReport event) {
        if (!policyService.hasPrivilegeFor(En_Privilege.ABSENCE_REPORT)) {
            fireEvent(new ErrorPageEvents.ShowForbidden());
            return;
        }

        view.resetFilter();
        dialogView.showPopup();

        resetView();
    }

    @Override
    public void onSaveClicked() {
        if (!validateView()) {
            return;
        }

        String name = HelperFunc.isEmpty(view.name().getValue()) ?
                lang.absenceReportDefaultNameTemplate(DateFormatter.formatDateTime(new Date())) :
                view.name().getValue();

        absenceController.createReport(name, view.getFilterParams().getQuery(),
                new FluentCallback<Void>()
                .withSuccess(result -> dialogView.hidePopup()));
    }

    @Override
    public void onCancelClicked() {
        dialogView.hidePopup();
    }

    private void resetView() {
        view.name().setValue(null);
        view.resetFilter();
    }

    private boolean validateView() {
        if (!view.getFilterParams().isValidDateRange()) {
            fireEvent(new NotifyEvents.Show(lang.absenceReportValidationDateRange(), NotifyEvents.NotifyType.ERROR));
            return false;
        }
        return true;
    }

    @Inject
    AbstractAbsenceReportCreateView view;
    @Inject
    AbsenceControllerAsync absenceController;
    @Inject
    PolicyService policyService;
    @Inject
    AbstractDialogDetailsView dialogView;

    @Inject
    Lang lang;
}
