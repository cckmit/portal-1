package ru.protei.portal.ui.questionnaire.client.activity.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_EmploymentType;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.Questionnaire;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.QuestionnaireEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.QuestionnaireControllerAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.Date;
import java.util.HashSet;


public abstract class QuestionnaireEditActivity implements Activity, AbstractQuestionnaireEditActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
    }

    @Event
    public void onShow(QuestionnaireEvents.Create event) {
        clearView();

        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());
    }

    @Override
    public void onSaveClicked() {
        Questionnaire newQuestionnaire = fillDto();
        if (!isValid(newQuestionnaire)) {
            return;
        }
        saveQuestionnaire(newQuestionnaire);
    }

    @Override
    public void onCancelClicked() {
        fireEvent(new Back());
    }

    private boolean isValid(Questionnaire newQuestionnaire) {
//        if (!newQuestionnaire.isValid()) {
//            fireErrorMessage(getValidationErrorMessage(newQuestionnaire));
//            return false;
//        }
        return true;
    }

    private void fireErrorMessage(String msg) {
        fireEvent(new NotifyEvents.Show(msg, NotifyEvents.NotifyType.ERROR));
    }

    private void saveQuestionnaire(Questionnaire questionnaire) {
        questionnaireService.createQuestionnaire(questionnaire, new RequestCallback<Long>() {
            @Override
            public void onError(Throwable throwable) {
                fireErrorMessage(lang.errNotCreated());
            }

            @Override
            public void onSuccess(Long result) {
                fireEvent(new Back());
            }
        });
    }


    private Questionnaire fillDto() {
        Questionnaire q = new Questionnaire();
        q.setEmployeeFullName(view.fullName().getValue());
        q.setComment(view.comment().getValue());
        q.setWorkplaceInfo(view.workplaceInfo().getValue());
        q.setPost(view.post().getValue());
        q.setEmploymentDate(view.employmentDate().getValue());
        q.setHeadOfDepartment(Person.fromPersonShortView(view.headOfDepartment().getValue()));
        q.setEquipmentList(view.equipmentList().getValue());
        q.setResourceList(view.resourcesList().getValue());
        q.setWithRegistration(view.withRegistration().getValue());
        q.setEmploymentType(view.employmentType().getValue());
        return q;
    }

    private void clearView() {
        view.fullName().setValue("");
        view.comment().setValue("");
        view.workplaceInfo().setValue("");
        view.post().setValue("");
        view.employmentDate().setValue(new Date());
        view.headOfDepartment().setValue(null);
        view.equipmentList().setValue(new HashSet<>());
        view.resourcesList().setValue(new HashSet<>());
        view.withRegistration().setValue(true);
        view.employmentType().setValue(En_EmploymentType.FULL_TIME);

        view.postValidation().setValid(true);
        view.fullNameValidation().setValid(true);
    }

    @Inject
    private Lang lang;
    @Inject
    private AbstractQuestionnaireEditView view;
    @Inject
    private QuestionnaireControllerAsync questionnaireService;

    private AppEvents.InitDetails initDetails;
}
