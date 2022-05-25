package ru.protei.portal.ui.employee.client.activity.preview;

import com.google.gwt.i18n.client.TimeZone;
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
import ru.protei.portal.ui.common.client.common.ConfigStorage;
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
import ru.protei.portal.ui.employee.client.activity.item.AbstractEmployeeItemView;
import ru.protei.portal.ui.employee.client.activity.item.AbstractPositionItemActivity;
import ru.protei.portal.ui.employee.client.activity.item.AbstractPositionItemView;

import java.util.*;

import static ru.protei.portal.core.model.helper.CollectionUtils.joining;
import static ru.protei.portal.core.model.helper.CollectionUtils.stream;

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

    @Event
    public void onUpdate(EmployeeEvents.Update event) {
        if(event.id == null || !Objects.equals(event.id, employee.getId()))
            return;

        showAbsences(event.id);
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
        fireEvent(new AbsenceEvents.Edit().withEmployee(this.employee));
    }

    private void fillView(Long employeeId) {
        employeeService.getEmployee(employeeId, new FluentCallback<EmployeeShortView>()
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

    private void showBirthday(EmployeeShortView employee) {

        boolean canDisplayBirthday = stream(configStorage.getConfigData().employeeBirthdayHideIds)
                .noneMatch(l -> Objects.equals(l, employee.getId()));
        view.birthdayContainerVisibility().setVisible(canDisplayBirthday && employee.getBirthday() != null);
        String value = "";
        if (canDisplayBirthday){
            TimeZone timeZone = null;
            if (employee.getTimezoneOffset() != null){
                timeZone = TimeZone.createTimeZone(employee.getTimezoneOffset());
            }
            value = DateFormatter.formatDateMonth(employee.getBirthday(), timeZone);
        }

        view.setBirthday(value);
    }

    private void fillView(final EmployeeShortView employee) {

        this.employee = employee;

        view.setPhotoUrl(AvatarUtils.getPhotoUrl(employee.getId()));
        view.setName(employee.getDisplayName());
        view.setNameHref(LinkUtils.makePreviewLink(EmployeeShortView.class, employee.getId()));

        showBirthday(employee);

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

        showLogins(employee.getId());

        showRestVacationDays(employee);

        showAbsences(employee.getId());
    }

    private void showLogins(final Long employeeId) {
        view.showLoginsPanel(false);

        if (!policyService.hasSystemScopeForPrivilege(En_Privilege.EMPLOYEE_VIEW)) {
            return;
        }

        view.showLoginsPanel(true);
        requestLogins(employeeId);
    }

    private void showRestVacationDays(final EmployeeShortView employee) {
        view.showRestVacationDaysPanel(false);

        if (!isSelfEmployee(employee.getId())) {
            return;
        }

        view.showRestVacationDaysPanel(true);
        view.setRestVacationDays("");
        view.getRestVacationDaysLoading().removeClassName("hide");
        requestRestVacationDays(employee.getId());
    }

    private void showAbsences(Long employeeId) {
        view.showAbsencesPanel(false);

        if (!policyService.hasPrivilegeFor(En_Privilege.ABSENCE_VIEW)) {
            return;
        }

        view.showAbsencesPanel(true);
        fireEvent(new AbsenceEvents.Show(view.absencesContainer(), employeeId));
    }

    private boolean isSelfEmployee(Long employeeId) {
        return Objects.equals(employeeId, policyService.getProfile().getId());
    }

    private void requestLogins(Long employeeId) {
        UserLoginShortViewQuery accountQuery = new UserLoginShortViewQuery();
        accountQuery.setPersonIds(new HashSet<>(Collections.singleton(employeeId)));
        accountService.getUserLoginShortViewList(accountQuery, new FluentCallback<List<UserLoginShortView>>()
                .withSuccess(userLoginShortViews ->
                        view.setLogins(
                                joining(userLoginShortViews, ", ", UserLoginShortView::getUlogin))));
    }

    private void requestRestVacationDays(final Long employeeId) {
        employeeService.getEmployeeRestVacationDays(employeeId,
                new FluentCallback<String>()
                        .withError((throwable, defaultErrorHandler, status) -> {
                            view.getRestVacationDaysLoading().addClassName("hide");
                            view.setRestVacationDays(lang.noData());
                            if (!En_ResultStatus.EMPLOYEE_NOT_SYNCHRONIZING_WITH_1C.equals(status)) {
                                defaultErrorHandler.accept(throwable);
                            }
                        })
                        .withSuccess(restVacationDays -> {
                            if (!Objects.equals(employeeId, employee.getId())) {
                                return;
                            }

                            view.getRestVacationDaysLoading().addClassName("hide");
                            view.setRestVacationDays(restVacationDays);
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

    @Inject
    ConfigStorage configStorage;

    private EmployeeShortView employee;

    private AppEvents.InitDetails initDetails;
}
