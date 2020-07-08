package ru.protei.portal.ui.absence.client.activity.report;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_AbsenceReason;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.query.AbsenceQuery;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsActivity;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.AbsenceControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.HashSet;
import java.util.stream.Collectors;

public abstract class AbsenceReportCreateActivity implements AbstractAbsenceReportCreateActivity, AbstractDialogDetailsActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        dialogView.setActivity(this);
        dialogView.setHeader(lang.absenceReport());
        dialogView.removeButtonVisibility().setVisible(false);
        dialogView.setSaveOnEnterClick(false);
        dialogView.setSaveButtonName(lang.buttonSend());
        dialogView.getBodyContainer().add(view.asWidget());
    }

    @Event
    public void onShow(AbsenceEvents.CreateReport event) {
        if (!policyService.hasPrivilegeFor(En_Privilege.ABSENCE_REPORT)) {
            fireEvent(new ForbiddenEvents.Show());
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

        absenceController.createReport(view.name().getValue(), makeQuery(), new FluentCallback<Void>()
                .withSuccess(result -> dialogView.hidePopup()));
    }

    @Override
    public void onCancelClicked() {
        dialogView.hidePopup();
    }

    private void resetView() {
        view.name().setValue(null);
        view.dateRange().setValue(null);
        view.employees().setValue(null);
        view.reasons().setValue(null);
        view.sortField().setValue(En_SortField.absence_person);
        view.sortDir().setValue(true);
    }

    private AbsenceQuery makeQuery() {
        return new AbsenceQuery(
                view.dateRange().getValue().from,
                view.dateRange().getValue().to,
                CollectionUtils.isEmpty(view.employees().getValue()) ? new HashSet<>() :
                        view.employees().getValue().stream().map(PersonShortView::getId).collect(Collectors.toSet()),
                CollectionUtils.isEmpty(view.reasons().getValue()) ? new HashSet<>() :
                        view.reasons().getValue().stream().map(En_AbsenceReason::getId).collect(Collectors.toSet()),
                view.sortField().getValue(),
                view.sortDir().getValue() ? En_SortDir.ASC : En_SortDir.DESC);
    }

    private boolean validateView() {
        if (view.dateRange().getValue() == null
                || view.dateRange().getValue().from == null
                || view.dateRange().getValue().to == null) {
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
