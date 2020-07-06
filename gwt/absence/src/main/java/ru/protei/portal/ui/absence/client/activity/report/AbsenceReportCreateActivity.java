package ru.protei.portal.ui.absence.client.activity.report;

import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_AbsenceReason;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.query.AbsenceQuery;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.service.AbsenceControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.HashSet;
import java.util.stream.Collectors;

public abstract class AbsenceReportCreateActivity implements AbstractAbsenceReportCreateActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
    }

    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        resetView();
    }

    @Event(Type.FILL_CONTENT)
    public void onShow(AbsenceEvents.CreateReport event) {
        if (!policyService.hasPrivilegeFor(En_Privilege.ABSENCE_REPORT)) {
            fireEvent(new ForbiddenEvents.Show());
            return;
        }

        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());
    }

    @Override
    public void onReportClicked() {
        absenceController.createReport(view.name().getValue(), makeQuery(), new FluentCallback());
    }

    @Override
    public void onResetClicked() {
        resetView();
    }

    @Override
    public void onCancelClicked() {
        fireEvent(new Back());
    }

    private void resetView() {
        view.name().setValue(null);
        view.dateRange().setValue(null);
        view.markDateRangeError();
        view.employees().setValue(null);
        view.reasons().setValue(null);
        view.sortField().setValue(En_SortField.absence_person);
        view.sortDir().setValue(true);
    }

    private AbsenceQuery makeQuery() {
        return new AbsenceQuery(
                view.dateRange().getValue().from,
                view.dateRange().getValue().to,
                CollectionUtils.isEmpty(view.employees().getValue()) ? new HashSet<>() : view.employees().getValue().stream().map(PersonShortView::getId).collect(Collectors.toSet()),
                CollectionUtils.isEmpty(view.reasons().getValue()) ? new HashSet<>() : view.reasons().getValue().stream().map(En_AbsenceReason::getId).collect(Collectors.toSet()),
                view.sortField().getValue(),
                view.sortDir().getValue() ? En_SortDir.ASC : En_SortDir.DESC);
    }

    @Inject
    AbstractAbsenceReportCreateView view;
    @Inject
    AbsenceControllerAsync absenceController;
    @Inject
    PolicyService policyService;

    private AppEvents.InitDetails initDetails;
}
