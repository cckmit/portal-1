package ru.protei.portal.ui.employeeregistration.client.activity.preview;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.EmployeeRegistration;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.*;
import ru.protei.portal.ui.common.client.service.EmployeeRegistrationControllerAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import static ru.protei.portal.core.model.helper.StringUtils.join;

public abstract class EmployeeRegistrationPreviewActivity implements AbstractEmployeeRegistrationPreviewActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity( this );
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails event) {
        this.fullScreenContainer = event.parent;
    }

    @Event
    public void onShow( EmployeeRegistrationEvents.ShowPreview event ) {
        event.parent.clear();
        event.parent.add( view.asWidget() );

        loadDetails(event.id);

        employeeRegistrationId = event.id;
        view.showFullScreen(false);
    }

    @Event
    public void onFullScreenShow(EmployeeRegistrationEvents.ShowFullScreen event) {
        if (event.id == null) {
            fireEvent(new Back());
            return;
        }

        employeeRegistrationId = event.id;

        if (!policyService.hasPrivilegeFor(En_Privilege.EMPLOYEE_REGISTRATION_VIEW)) {
            fireEvent(new ForbiddenEvents.Show());
            return;
        }

        fullScreenContainer.clear();
        fullScreenContainer.add( view.asWidget() );
        loadDetails(event.id);
        view.showFullScreen(true);
    }

    @Override
    public void onFullScreenPreviewClicked () {
        fireEvent( new EmployeeRegistrationEvents.ShowFullScreen( employeeRegistrationId ) );
    }

    @Override
    public void onBackButtonClicked() {
        fireEvent(new EmployeeRegistrationEvents.Show());
    }

    private void loadDetails(Long id) {
        employeeRegistrationController.getEmployeeRegistration(id, new RequestCallback<EmployeeRegistration>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errGetItem(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(EmployeeRegistration result) {
                if (result == null) {
                    onError(null);
                    return;
                }
                fillView(result);
            }
        });
    }

    private void fillView( EmployeeRegistration value ) {
        view.setFullName(value.getEmployeeFullName() + " (" + (value.getId() == null ? "" : " #" + value.getId()) + ")");
        view.setComment(value.getComment());
        view.setWorkplace(value.getWorkplace());
        view.setEmploymentDate(DateFormatter.formatDateOnly(value.getEmploymentDate()));
        view.setCreatedBy(lang.createBy(value.getCreatorShortName(), DateFormatter.formatDateTime(value.getCreated())));
        view.setEquipmentList( join(value.getEquipmentList(), equipmentLang::getName, ", "));
        view.setResourceList( join(value.getResourceList(), resourceLang::getName, ", "));
        view.setPhoneOfficeTypeList( join(value.getPhoneOfficeTypeList(), phoneOfficeTypeLang::getName, ", "));
        view.setCurators( join( value.getCurators(), Person::getDisplayShortName, "," ) );
        view.setPosition(value.getPosition());
        view.setProbationPeriodMonth(value.getProbationPeriodMonth() == null ?
                lang.employeeRegistrationWithoutProbationPeriod() :
                String.valueOf(value.getProbationPeriodMonth())
        );
        view.setOperatingSystem(value.getOperatingSystem());
        view.setResourceComment(value.getResourceComment());
        view.setAdditionalSoft(value.getAdditionalSoft());
        view.setHeadOfDepartment(value.getHeadOfDepartment() == null ? "" : value.getHeadOfDepartment().getName());

        if (value.getEmploymentType() == null) {
            view.setEmploymentType("");
        } else {
            String employmentType = employmentTypeLang.getName(value.getEmploymentType());
            if(value.isWithRegistration()) {
                employmentType += lang.employeeRegistrationEmployeeWithRegistrationTrue();
            }
            view.setEmploymentType(employmentType);
        }

        view.setState(value.getStateName());

        fireEvent(new CaseLinkEvents.Show(view.getLinksContainer())
                .withCaseType(En_CaseType.EMPLOYEE_REGISTRATION)
                .withCaseId(value.getId())
                .readOnly());

        fireEvent(new CaseCommentEvents.Show(view.getCommentsContainer())
                .withCaseType(En_CaseType.EMPLOYEE_REGISTRATION)
                .withCaseId(value.getId())
                .withModifyEnabled(policyService.hasPrivilegeFor(En_Privilege.EMPLOYEE_REGISTRATION_VIEW)));
    }

    private HasWidgets fullScreenContainer;

    @Inject
    private AbstractEmployeeRegistrationPreviewView view;
    @Inject
    private EmployeeRegistrationControllerAsync employeeRegistrationController;
    @Inject
    private PolicyService policyService;

    @Inject
    private En_EmployeeEquipmentLang equipmentLang;
    @Inject
    private En_InternalResourceLang resourceLang;
    @Inject
    private En_PhoneOfficeTypeLang phoneOfficeTypeLang;
    @Inject
    private En_EmploymentTypeLang employmentTypeLang;
    @Inject
    private Lang lang;

    private Long employeeRegistrationId;
}
