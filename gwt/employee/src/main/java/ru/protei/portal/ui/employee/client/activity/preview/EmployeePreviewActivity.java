package ru.protei.portal.ui.employee.client.activity.preview;

import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
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
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.EmployeeEvents;
import ru.protei.portal.ui.common.client.events.ForbiddenEvents;
import ru.protei.portal.ui.common.client.service.AvatarUtils;
import ru.protei.portal.ui.common.client.service.EmployeeControllerAsync;
import ru.protei.portal.ui.common.client.util.LinkUtils;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.employee.client.activity.item.AbstractPositionItemActivity;
import ru.protei.portal.ui.employee.client.activity.item.AbstractPositionItemView;

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
            fireEvent(new ForbiddenEvents.Show());
            return;
        }

        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());

        this.employeeId = event.employeeId;

        fillView(employeeId);
        view.showFullScreen(true);
    }

    @Event
    public void onShow(EmployeeEvents.ShowPreview event) {
        event.parent.clear();
        event.parent.add( view.asWidget() );

        this.employeeId = event.employee.getId();

        fillView(event.employee);
        view.showFullScreen(false);
    }

    @Override
    public void onFullScreenClicked () {
        fireEvent(new EmployeeEvents.ShowFullScreen(employeeId));
    }

    @Override
    public void onBackButtonClicked() {
        fireEvent(new EmployeeEvents.Show());
    }

    private void fillView(Long employeeId) {
        employeeService.getEmployeeShortView(employeeId, new FluentCallback<EmployeeShortView>().withSuccess(this::fillView));
    }

    private void fillView(EmployeeShortView employee) {

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
            view.setEmail(EmailRender.renderToHtml(infoFacade.publicEmailsStream(), false));
            view.emailContainerVisibility().setVisible(true);
        }
        if (StringUtils.isEmpty(infoFacade.publicPhonesAsString())) {
            view.setPhones("");
            view.phonesContainerVisibility().setVisible(false);
        } else {
            view.setPhones(infoFacade.publicPhonesAsFormattedString(true));
            view.phonesContainerVisibility().setVisible(true);
        }

        view.getPositionsContainer().clear();
        WorkerEntryFacade entryFacade = new WorkerEntryFacade(employee.getWorkerEntries());
        entryFacade.getSortedEntries().forEach(workerEntry -> employeeService.getDepartmentHead(workerEntry.getPersonId(), workerEntry.getDepId(), new FluentCallback<PersonShortView>()
                .withSuccess(head -> {
                    AbstractPositionItemView positionItemView = makePositionView(workerEntry, head);
                    view.getPositionsContainer().add(positionItemView.asWidget());
                })
        ));

        view.setID(employee.getId().toString());
        view.setIP(employee.getIpAddress());
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

        if (head != null) {
            itemView.setDepartmentHead(head.getName(), LinkUtils.makeLink(EmployeeShortView.class, head.getId()));
            itemView.departmentHeadContainerVisibility().setVisible(true);
        }

        itemView.setPosition(workerEntry.getPositionName());

        return itemView;
    }

    @Inject
    AbstractEmployeePreviewView view;

    @Inject
    Provider< AbstractPositionItemView > positionFactory;

    @Inject
    EmployeeControllerAsync employeeService;

    @Inject
    PolicyService policyService;

    private Long employeeId;

    private AppEvents.InitDetails initDetails;
}
