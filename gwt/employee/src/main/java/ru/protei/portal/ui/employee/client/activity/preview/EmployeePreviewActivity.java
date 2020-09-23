package ru.protei.portal.ui.employee.client.activity.preview;

import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.core.model.struct.WorkerEntryFacade;
import ru.protei.portal.core.model.view.EmployeeShortView;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.WorkerEntryShortView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.common.EmailRender;
import ru.protei.portal.ui.common.client.events.AbsenceEvents;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.EmployeeEvents;
import ru.protei.portal.ui.common.client.events.ErrorPageEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.EmployeeControllerAsync;
import ru.protei.portal.ui.common.client.util.AvatarUtils;
import ru.protei.portal.ui.common.client.util.LinkUtils;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.employee.client.activity.item.AbstractPositionItemActivity;
import ru.protei.portal.ui.employee.client.activity.item.AbstractPositionItemView;

import java.util.Objects;

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
        WorkerEntryFacade entryFacade = new WorkerEntryFacade(employee.getWorkerEntries());
        entryFacade.getSortedEntries().forEach(workerEntry -> employeeService.getDepartmentHead(workerEntry.getDepId(), new FluentCallback<PersonShortView>()
                .withSuccess(head -> {
                    AbstractPositionItemView positionItemView = makePositionView(workerEntry, head);
                    view.positionsContainer().add(positionItemView.asWidget());
                })
        ));

        view.setID(employee.getId().toString());
        view.setIP(employee.getIpAddress());

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
            itemView.setDepartmentHead(head.getName(), LinkUtils.makePreviewLink(EmployeeShortView.class, head.getId()));
            itemView.departmentHeadContainerVisibility().setVisible(true);
        }

        itemView.setPosition(workerEntry.getPositionName());
        itemView.setCompany(workerEntry.getCompanyName());

        return itemView;
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
    PolicyService policyService;

    @Inject
    Lang lang;

    @Inject
    DefaultErrorHandler defaultErrorHandler;

    private EmployeeShortView employee;

    private AppEvents.InitDetails initDetails;
}
