package ru.protei.portal.ui.absence.client.activity.create;



public abstract class AbsenceCreateActivity {
//        extends AbsenceCommonActivity {

//    @Inject
//    public void onInit() {
//        super.onInit();
//        view.getDateContainer().add(createView.asWidget());
//        createView.setActivity(this);
//    }
//
//    @Event
//    public void onShow(AbsenceEvents.Create event) {
//        if (!hasAccessCreate()) {
//            return;
//        }
//        dialogView.setHeader(lang.absenceCreation());
//        this.employee = event.employee;
//        onShow();
//    }
//
//    @Override
//    public void onDateRangeChanged() {
//        List<DateInterval> value = createView.dateRange().getValue();
//        if (isDateRangeValid(value)) {
//            createView.setDateRangeValid(isDateRangesIntersectValid(value));
//        } else {
//            createView.setDateRangeValid(true);
//        }
//    }
//
//    @Override
//    public void onReasonChangeToNightWork() {
//        List<DateInterval> intervals = createView.dateRange().getValue();
//        int lastIntervalIndex = intervals.size() - 1;
//        DateInterval lastInterval = intervals.get(lastIntervalIndex);
//
//        Date to = lastInterval.to;
//        to.setHours(13);
//        to.setMinutes(0);
//        to.setSeconds(0);
//
//        createView.dateRange().setValue(intervals);
//    }
//
//    protected void performFillView() {
//        fillView(this.employee != null ? new PersonAbsence(
//                this.employee.getId(),
//                this.employee.getDisplayName()) : new PersonAbsence());
//        this.employee = null;
//        createView.dateRange().setValue(new ArrayList<>());
//        createView.setDateRangeValid(true);
//        dialogView.saveButtonVisibility().setVisible(true);
//    }
//
//    protected boolean additionalValidate() {
//        if (!isDateRangeValid(createView.dateRange().getValue())) {
//            fireEvent(new NotifyEvents.Show(lang.absenceValidationDateRanges(), NotifyEvents.NotifyType.ERROR));
//            return false;
//        }
//        if (!isDateRangesIntersectValid(createView.dateRange().getValue())) {
//            fireEvent(new NotifyEvents.Show(lang.absenceValidationDateRangesIntersection(), NotifyEvents.NotifyType.ERROR));
//            return false;
//        }
//        return true;
//    }
//
//    @Override
//    protected void performSave(Consumer<Throwable> onError, Runnable onSuccess) {
//        List<DateInterval> value = createView.dateRange().getValue();
//        List<PersonAbsence> collect = value.stream().map(date -> {
//            PersonAbsence personAbsence = fillDTO();
//            personAbsence.setFromTime(date.from);
//            personAbsence.setTillTime(date.to);
//            return personAbsence;
//        }).collect(Collectors.toList());
//
//        absenceController.saveAbsences(collect, new FluentCallback<List<Long>>()
//                .withError(throwable -> {
//                    defaultErrorHandler.accept(throwable);
//                    onError.accept(throwable);
//                })
//                .withSuccess(absence -> {
//                    fireEvent(new NotifyEvents.Show(lang.absenceCreated(collect.size()), NotifyEvents.NotifyType.SUCCESS));
//                    fireEvent(new EmployeeEvents.Update(collect.get(0).getPersonId()));
//                    onSuccess.run();
//                }));
//    }
//
//    private boolean hasAccessCreate() {
//        return policyService.hasPrivilegeFor(En_Privilege.ABSENCE_CREATE);
//    }
//
//    private PersonAbsence fillDTO() {
//        return fillCommonDTO();
//    }
//
//    private boolean isDateRangesIntersectValid(List<DateInterval> dateIntervals) {
//        List<DateInterval> temp = new ArrayList<>(dateIntervals);
//        temp.sort(Comparator.nullsFirst(Comparator.comparing(date -> date.from)));
//        boolean result = true;
//        for (int i = 0; i < temp.size()-1; i++) {
//            if (temp.get(i).to.after(temp.get(i + 1).from)) {
//                result = false;
//                break;
//            }
//        }
//        return result;
//    }
//
//    private boolean isDateRangeValid(List<DateInterval> dateIntervals) {
//        for (DateInterval dateInterval : dateIntervals) {
//            if (!(dateInterval != null &&
//                    dateInterval.from != null &&
//                    dateInterval.to != null &&
//                    dateInterval.from.before(dateInterval.to))) {
//                return false;
//            }
//        }
//        return true;
//    }
//
//    @Inject
//    AbstractAbsenceCreateView createView;
//
//    private EmployeeShortView employee;
}
