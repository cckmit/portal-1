package ru.protei.portal.ui.employee.client.activity.preview;

import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.UserLoginShortView;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.UserLoginShortViewQuery;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.core.model.struct.WorkerEntryFacade;
import ru.protei.portal.core.model.view.EmployeeShortView;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.WorkerEntryShortView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.common.EmailRender;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.AccountControllerAsync;
import ru.protei.portal.ui.common.client.service.EmployeeControllerAsync;
import ru.protei.portal.ui.common.client.util.AvatarUtils;
import ru.protei.portal.ui.common.client.util.LinkUtils;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.employee.client.activity.item.AbstractPositionItemActivity;
import ru.protei.portal.ui.employee.client.activity.item.AbstractPositionItemView;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.joining;

/**
 * Активность превью сотрудника
 */
public abstract class EmployeePreviewActivity implements AbstractEmployeePreviewActivity, AbstractPositionItemActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity( this );
    }

    @Event
    public void onInit(AppEvents.InitDetails event) {
        this.initDetails = event;
    }

    @Event
    public void onShow(EmployeeEvents.ShowFullScreen event) {
        if (!policyService.hasPrivilegeFor(En_Privilege.EMPLOYEE_VIEW)) {
            fireEvent(new ErrorPageEvents.ShowForbidden());
            return;
        }

        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());

        fillView(event.employeeId);
        view.showFullScreen(true);
    }

    @Event
    public void onShow(EmployeeEvents.ShowPreview event) {
        event.parent.clear();
        event.parent.add( view.asWidget() );

        fillView(event.employee);
        view.showFullScreen(false);
    }

    @Override
    public void onFullScreenClicked() {
        fireEvent(new EmployeeEvents.ShowFullScreen(employee.getId()));
    }

    @Override
    public void onBackButtonClicked() {
        fireEvent(new EmployeeEvents.Show(true));
    }

    @Override
    public void onCreateAbsenceButtonClicked() {
        fireEvent(new AbsenceEvents.Create().withEmployee(this.employee));
    }

    private void fillView(Long employeeId) {
        employeeService.getEmployeeWithChangedHiddenCompanyNames(employeeId, new FluentCallback<EmployeeShortView>()
                .withError(throwable -> {
                    if (En_ResultStatus.NOT_FOUND.equals(getStatus(throwable))) {
                        fireEvent(new ErrorPageEvents.ShowNotFound(initDetails.parent, lang.errEmployeeNotFound()));
                        return;
                    }

                    defaultErrorHandler.accept(throwable);
                })
                .withSuccess(this::fillView)
        );
    }

    private void fillView(EmployeeShortView employee) {

        this.employee = employee;

        view.setPhotoUrl(AvatarUtils.getPhotoUrl(employee.getId()));
        view.setName(employee.getDisplayName());

        if (employee.getBirthday() == null) {
            view.setBirthday("");
            view.birthdayContainerVisibility().setVisible(false);
        } else {
            view.setBirthday(DateFormatter.formatDateMonth(employee.getBirthday()));
            view.birthdayContainerVisibility().setVisible(true);
        }

        PlainContactInfoFacade infoFacade = new PlainContactInfoFacade(employee.getContactInfo());

        if (CollectionUtils.isEmpty(infoFacade.publicEmails())) {
            view.setEmail("");
            view.emailContainerVisibility().setVisible(false);
        } else {
            view.setEmail(EmailRender.renderToHtml(infoFacade.publicEmailsStream()));
            view.emailContainerVisibility().setVisible(true);
        }
        if (StringUtils.isEmpty(infoFacade.publicPhonesAsString())) {
            view.setPhones("");
            view.phonesContainerVisibility().setVisible(false);
        } else {
            view.setPhones(infoFacade.publicPhonesAsFormattedString(true));
            view.phonesContainerVisibility().setVisible(true);
        }

        view.positionsContainer().clear();

        Map<WorkerEntryShortView, AbstractPositionItemView> itemViewMap = new HashMap<>();
        WorkerEntryFacade entryFacade = new WorkerEntryFacade(employee.getWorkerEntries());
        entryFacade.getSortedEntries().forEach(workerEntry -> employeeService.getDepartmentHead(workerEntry.getDepId(), new FluentCallback<PersonShortView>()
                .withSuccess(head -> {
                    itemViewMap.put(workerEntry, makePositionView(workerEntry, head));

                    if (isAllDepartmentsHeadsReceived(entryFacade.getSortedEntries(), itemViewMap)){
                        entryFacade.getSortedEntries().forEach(item -> view.positionsContainer().add(itemViewMap.get(item).asWidget()));
                    }
                })
        ));
        view.setID(employee.getId().toString());
        view.setIP(employee.getIpAddress());

        requestLogins(employee.getId());


        List<String> workerExtIds = employee.getWorkerEntries().stream()
                                            .map(entry -> entry.getWorkerExtId())
                                            .collect(Collectors.toList());
        if (workerExtIds.isEmpty()) {
            view.setRestVacationDays(lang.noData());
        } else {
            view.setRestVacationDays("");
            view.getRestVacationDaysLoading().removeClassName("hide");
            requestRestVacationDays(workerExtIds);
        }

        showAbsences(employee.getId());
    }

    @Event
    public void onUpdate(EmployeeEvents.Update event) {
        if(event.id == null || !Objects.equals(event.id, employee.getId()))
            return;

        showAbsences(event.id);
    }

    private void showAbsences(Long employeeId) {
        view.showAbsencesPanel(false);

        if (!policyService.hasPrivilegeFor(En_Privilege.ABSENCE_VIEW))
            return;

        view.showAbsencesPanel(true);
        fireEvent(new AbsenceEvents.Show(view.absencesContainer(), employeeId));
    }

    private void requestLogins(Long employeeId) {
        UserLoginShortViewQuery accountQuery = new UserLoginShortViewQuery();
        accountQuery.setPersonIds(new HashSet<>(Collections.singleton(employeeId)));
        accountService.getUserLoginShortViewList(accountQuery, new FluentCallback<List<UserLoginShortView>>()
                .withSuccess(userLoginShortViews ->
                        view.setLogins(
                                joining(userLoginShortViews, ", ", UserLoginShortView::getUlogin))));
    }

    private void requestRestVacationDays(List<String> workerExtId) {
        employeeService.getEmployeeRestVacationDays(workerExtId,
                new FluentCallback<String>()
                        .withError(throwable -> {
                            view.getRestVacationDaysLoading().addClassName("hide");
                            view.setRestVacationDays(lang.noData());
                            fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                        })
                        .withSuccess(new Consumer<String>() {
                            @Override
                            public void accept(String restVacationDaysResult) {
                                view.getRestVacationDaysLoading().addClassName("hide");
                                view.setRestVacationDays(restVacationDaysResult == null ? lang.noData()
                                                                                        : restVacationDaysResult);
                            }
                        }));
    }

    private AbstractPositionItemView makePositionView(WorkerEntryShortView workerEntry, PersonShortView head) {

        AbstractPositionItemView itemView = positionFactory.get();
        itemView.setActivity(this);

        if (workerEntry.getDepartmentParentName() == null) {
            itemView.setDepartmentParent(workerEntry.getDepartmentName());
        } else {
            itemView.setDepartmentParent(workerEntry.getDepartmentParentName());
            itemView.setDepartment(workerEntry.getDepartmentName());
            itemView.departmentContainerVisibility().setVisible(true);
        }

        if (head != null && !head.getId().equals(employee.getId())) {
            itemView.setDepartmentHead(head.getDisplayName(), LinkUtils.makePreviewLink(EmployeeShortView.class, head.getId()));
            itemView.departmentHeadContainerVisibility().setVisible(true);
        }

        itemView.setPosition(workerEntry.getPositionName());
        itemView.setCompany(workerEntry.getCompanyName());

        return itemView;
    }

    private boolean isAllDepartmentsHeadsReceived (List<WorkerEntryShortView> workerEntries, Map<WorkerEntryShortView, AbstractPositionItemView> itemViewMap){
        return workerEntries.size() == itemViewMap.size();
    }


    private En_ResultStatus getStatus(Throwable throwable) {
        if (!(throwable instanceof RequestFailedException)) {
            return null;
        }

        return ((RequestFailedException) throwable).status;
    }

    @Inject
    AbstractEmployeePreviewView view;

    @Inject
    Provider< AbstractPositionItemView > positionFactory;

    @Inject
    EmployeeControllerAsync employeeService;

    @Inject
    AccountControllerAsync accountService;

    @Inject
    PolicyService policyService;

    @Inject
    Lang lang;

    @Inject
    DefaultErrorHandler defaultErrorHandler;

    private EmployeeShortView employee;

    private AppEvents.InitDetails initDetails;
}
