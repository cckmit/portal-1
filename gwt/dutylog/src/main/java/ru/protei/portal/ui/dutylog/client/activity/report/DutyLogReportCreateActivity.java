package ru.protei.portal.ui.dutylog.client.activity.report;

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
import ru.protei.portal.ui.common.client.events.DutyLogEvents;
import ru.protei.portal.ui.common.client.events.ErrorPageEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.DutyLogControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.Date;

import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.WIDE_MODAL;

public abstract class DutyLogReportCreateActivity implements AbstractDutyLogReportCreateActivity, AbstractDialogDetailsActivity, Activity {

    @PostConstruct
    public void onInit() {
        dialogView.setActivity(this);
        dialogView.addStyleName(WIDE_MODAL);
        dialogView.setHeader(lang.dutyLogReport());
        dialogView.removeButtonVisibility().setVisible(false);
        dialogView.setSaveOnEnterClick(false);
        dialogView.setSaveButtonName(lang.buttonSend());
        dialogView.getBodyContainer().add(view.asWidget());
    }

    @Event
    public void onShow(DutyLogEvents.CreateReport event) {
        if (!policyService.hasPrivilegeFor(En_Privilege.DUTY_LOG_REPORT)) {
            fireEvent(new ErrorPageEvents.ShowForbidden());
            return;
        }

        dialogView.showPopup();

        resetView();
    }

    @Override
    public void onSaveClicked() {
        if (!validateView()) {
            return;
        }

        String name = HelperFunc.isEmpty(view.name().getValue()) ?
                lang.dutyLogReportDefaultNameTemplate(DateFormatter.formatDateTime(new Date())) :
                view.name().getValue();

        fireEvent(new NotifyEvents.Show(lang.dutyLogReportRequestNotification(), NotifyEvents.NotifyType.INFO));
        dutyLogController.createReport(name, view.getFilterWidget().getFilterParamView().getQuery(),
                new FluentCallback<Void>()
                        .withSuccess(result -> dialogView.hidePopup()));
    }

    @Override
    public void onCancelClicked() {
        dialogView.hidePopup();
    }

    private void resetView() {
        view.name().setValue(null);
        view.getFilterWidget().resetFilter();
    }

    private boolean validateView() {
        if (!view.getFilterWidget().getFilterParamView().isValidDateRange()) {
            fireEvent(new NotifyEvents.Show(lang.dutyLogValidationDateRange(), NotifyEvents.NotifyType.ERROR));
            return false;
        }
        return true;
    }

    @Inject
    AbstractDutyLogReportCreateView view;
    @Inject
    DutyLogControllerAsync dutyLogController;
    @Inject
    PolicyService policyService;
    @Inject
    AbstractDialogDetailsView dialogView;

    @Inject
    Lang lang;
}
