package ru.protei.portal.ui.dutylog.client.activity.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_DutyType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.DutyLog;
import ru.protei.portal.core.model.query.DutyLogQuery;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsActivity;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.DutyLogEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.DutyLogControllerAsync;
import ru.protei.portal.ui.common.client.util.DateUtils;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.Date;
import java.util.function.Consumer;

public abstract class DutyLogEditActivity implements AbstractDutyLogEditActivity, AbstractDialogDetailsActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        dialogView.setActivity(this);
        dialogView.removeButtonVisibility().setVisible(false);
        dialogView.getBodyContainer().add(view.asWidget());
    }

    @Event
    public void onShow(DutyLogEvents.Edit event) {
        if (!hasAccessCreate() && !hasAccessEdit()) {
            return;
        }

        dialogView.setHeader(event.id == null ? lang.dutyLogCreation() : lang.dutyLogEditing());
        dialogView.showPopup();

        if (event.id == null) {
            loadNewDutyLog();
        } else {
            loadDutyLog(event.id, this::showView);
        }
    }

    @Override
    public void onSaveClicked() {
        if (!validateView()) {
            return;
        }
        saveDutyLog();
    }

    @Override
    public void onCancelClicked() {
        dutyLog = null;
        dialogView.hidePopup();
    }

    private boolean hasAccessCreate() {
        return isNew() && policyService.hasPrivilegeFor(En_Privilege.DUTY_LOG_CREATE);
    }

    private boolean hasAccessEdit() {
        return (isNew() && policyService.hasPrivilegeFor(En_Privilege.DUTY_LOG_CREATE)) ||
                (!isNew() && policyService.hasPrivilegeFor(En_Privilege.DUTY_LOG_EDIT));
    }

    private void showView(DutyLog dutyLog) {
        view.contentVisibility().setVisible(true);
        if (!view.asWidget().isAttached()) {
            return;
        }
        fillView(dutyLog);
    }

    private void showLoading() {
        view.loadingVisibility().setVisible(true);
        dialogView.removeButtonVisibility().setVisible(false);
        dialogView.saveButtonVisibility().setVisible(false);
    }

    private void hideLoading() {
        view.loadingVisibility().setVisible(false);
    }

    private void fillView(DutyLog value) {
        this.dutyLog = value;

        PersonShortView personShortView = new PersonShortView( value.getPersonId() );
        personShortView.setDisplayName( value.getPersonDisplayName() );
        view.employee().setValue(personShortView);
        view.dateRange().setValue(new DateInterval(value.getFrom(), value.getTo()));
        view.type().setValue(value.getType());
        dialogView.saveButtonVisibility().setVisible(hasAccessEdit());
    }

    private boolean validateView() {
        if (!view.employeeValidator().isValid()) {
            fireEvent(new NotifyEvents.Show(lang.dutyLogValidationEmployee(), NotifyEvents.NotifyType.ERROR));
            return false;
        }
        if (!isDateRangeValid(view.dateRange().getValue())) {
            fireEvent(new NotifyEvents.Show(lang.dutyLogValidationDateRange(), NotifyEvents.NotifyType.ERROR));
            return false;
        }
        return true;
    }

    private DutyLog fillDTO() {
        dutyLog.setPersonId(view.employee().getValue().getId());
        DateInterval dateInterval = view.dateRange().getValue();
        dutyLog.setFrom(dateInterval.from);
        dutyLog.setTo(dateInterval.to);
        dutyLog.setType(view.type().getValue());

        return dutyLog;
    }

    private void loadNewDutyLog() {
        DutyLogQuery query = makeQuery();        
        dutyLogController.getDutyLogs(query, new FluentCallback<SearchResult<DutyLog>>()
                .withError(throwable -> {
                    loadDutyLogWithType(En_DutyType.BG);
                })
                .withSuccess(sr -> {
                    En_DutyType dutyType = sr.getResults().get(0).getType();
                    loadDutyLogWithType(dutyType);
                })
        );
    }

    private void loadDutyLog(Long dutyLogId, Consumer<DutyLog> onSuccess) {
        showLoading();
        dutyLogController.getDutyLog(dutyLogId, new FluentCallback<DutyLog>()
                .withError(throwable -> {
                    hideLoading();
                    defaultErrorHandler.accept(throwable);
                })
                .withSuccess(dutyLog -> {
                    hideLoading();
                    onSuccess.accept(dutyLog);
                }));
    }

    private void saveDutyLog() {
        enableButtons(false);
        dutyLogController.saveDutyLog(fillDTO(), new FluentCallback<Long>()
                .withError(error -> {
                    enableButtons(true);
                    defaultErrorHandler.accept(error);
                })
                .withSuccess(result -> {
                    enableButtons(true);
                    fireEvent(new NotifyEvents.Show(isNew() ? lang.dutyLogCreated() : lang.dutyLogUpdated(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new DutyLogEvents.Update());
                    onCancelClicked();
                }));
    }

    private void loadDutyLogWithType(En_DutyType dutyType) {
        DutyLog dutyLog = new DutyLog();
        dutyLog.setPersonId(policyService.getProfileId());
        dutyLog.setPersonDisplayName(policyService.getProfile().getFullName());
        dutyLog.setType(dutyType);
        // дефолтный период дежурства – с пятницы по пятницу (PORTAL-1377)
        dutyLog.setFrom(getCurrentWeekFriday());
        dutyLog.setTo(DateUtils.addDays(dutyLog.getFrom(), 7L));
        showView(dutyLog);
    }

    private void enableButtons(boolean isEnable) {
        dialogView.saveButtonEnabled().setEnabled(isEnable);
    }

    private boolean isNew() {
        return dutyLog == null || dutyLog.getId() == null;
    }

    private boolean isDateRangeValid(DateInterval dateInterval) {
        return dateInterval != null &&
                dateInterval.from != null &&
                dateInterval.to != null &&
                dateInterval.from.before(dateInterval.to);
    }

    private Date getCurrentWeekFriday() {
        Date dateFrom = DateUtils.setBeginOfDay(new Date());
        int dayOfWeek = DateUtils.getDayOfWeekNormalized(dateFrom);
        if (dayOfWeek > 5) {
            dateFrom = DateUtils.addDays(dateFrom, 5L - dayOfWeek);
        } else {
            dateFrom = DateUtils.subsctractDays(dateFrom,  dayOfWeek - 5L);
        }
        return dateFrom;
    }

    private DutyLogQuery makeQuery() {
        return new DutyLogQuery(policyService.getProfileId(), En_SortField.duty_log_date_to, En_SortDir.DESC);
    }

    @Inject
    Lang lang;
    @Inject
    AbstractDutyLogEditView view;
    @Inject
    AbstractDialogDetailsView dialogView;
    @Inject
    DutyLogControllerAsync dutyLogController;
    @Inject
    PolicyService policyService;
    @Inject
    DefaultErrorHandler defaultErrorHandler;

    private DutyLog dutyLog;
}
