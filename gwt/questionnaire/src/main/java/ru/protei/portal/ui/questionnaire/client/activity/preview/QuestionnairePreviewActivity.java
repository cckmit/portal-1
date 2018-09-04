package ru.protei.portal.ui.questionnaire.client.activity.preview;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.Questionnaire;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.events.QuestionnaireEvents;
import ru.protei.portal.ui.common.client.lang.En_EmployeeEquipmentLang;
import ru.protei.portal.ui.common.client.lang.En_EmploymentTypeLang;
import ru.protei.portal.ui.common.client.lang.En_InternalResourceLang;
import ru.protei.portal.ui.common.client.lang.Lang;

public abstract class QuestionnairePreviewActivity implements AbstractQuestionnairePreviewActivity, Activity {
    @PostConstruct
    public void onInit() {
        view.setActivity( this );
    }

    @Event
    public void onShow( QuestionnaireEvents.ShowPreview event ) {
        event.parent.clear();
        event.parent.add( view.asWidget() );

        fillView( event.questionnaire );
    }

    private void fillView( Questionnaire value ) {
        view.setFullName(value.getEmployeeFullName());
        view.setComment(value.getComment());
        view.setWorkplaceInfo(value.getWorkplaceInfo());
        view.setEmploymentDate(DateFormatter.formatDateOnly(value.getEmploymentDate()));
        view.setCreated(DateFormatter.formatDateTime(value.getCreated()));
        view.setEquipmentList(CollectionUtils.join(value.getEquipmentList(), equipmentLang::getName, ", "));
        view.setResourceList(CollectionUtils.join(value.getResourceList(), resourceLang::getName, ", "));
        view.setPost(value.getPost());
        if (value.getHeadOfDepartment() != null)
            view.setHeadOfDepartment(PersonShortView.fromPerson(value.getHeadOfDepartment()).getDisplayShortName());
        else
            view.setHeadOfDepartment("");

        if (value.getEmploymentType() == null) {
            view.setEmploymentType("");
        } else {
            String employmentType = employmentTypeLang.getName(value.getEmploymentType());
            view.setEmploymentType(employmentType);
        }

        if (value.isWithRegistration())
            view.setWithRegistration(lang.questionnaireEmployeeWithRegistrationTrue());
        else
            view.setWithRegistration(lang.questionnaireEmployeeWithRegistrationFalse());

        view.setState(value.getState());
    }

    @Inject
    private AbstractQuestionnairePreviewView view;

    @Inject
    private En_EmployeeEquipmentLang equipmentLang;
    @Inject
    private En_InternalResourceLang resourceLang;
    @Inject
    private En_EmploymentTypeLang employmentTypeLang;
    @Inject
    private Lang lang;

}
