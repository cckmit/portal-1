package ru.protei.portal.ui.issue.client.activity.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.view.ContactShortView;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.IssueEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.IssueServiceAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.function.Consumer;

/**
 * Активность создания и редактирования обращения
 */
public abstract class IssueEditActivity implements AbstractIssueEditActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity( this );
    }

    @Event
    public void onInitDetails( AppEvents.InitDetails initDetails ) {
        this.initDetails = initDetails;
    }

    @Event
    public void onShow( IssueEvents.Edit event ) {
        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());


        if(event.id == null) {
            fireEvent(new AppEvents.InitPanelName(lang.newIssue()));
            initialView(new CaseObject());
        }else {
            fireEvent(new AppEvents.InitPanelName(lang.issueEdit()));
            requestIssue(event.id, this::initialView);
        }
    }

    @Override
    public void onSaveClicked() {
        if(!validateFieldsAndGetResult()){
            return;
        }

        fillIssueObject(issue);

        issueService.saveIssue(issue, new RequestCallback<Boolean>() {
            @Override
            public void onError(Throwable throwable) {}

            @Override
            public void onSuccess(Boolean aBoolean) {
                fireEvent(new IssueEvents.Show());
                fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                fireEvent(new IssueEvents.ChangeModel());
            }
        });
    }

    @Override
    public void onCancelClicked() {
        fireEvent(new Back());
    }

    private void resetState(){
        view.initiatorState().setEnabled(view.companyValidator().isValid());
    }

    private void initialView(CaseObject issue){
        this.issue = issue;
        fillView(this.issue);
        resetState();
    }

    private void requestIssue(Long id, Consumer<CaseObject> successAction){
        issueService.getIssue(id, new RequestCallback<CaseObject>() {
            @Override
            public void onError(Throwable throwable) {}

            @Override
            public void onSuccess(CaseObject issue) {
                successAction.accept(issue);
            }
        });
    }

    private void fillView(CaseObject issue){
        view.name().setValue(issue.getName());
        view.isLocal().setValue(issue.isPrivateCase());
        view.description().setText(issue.getInfo());

        view.state().setValue(En_CaseState.getById(issue.getStateId()));
        view.importance().setValue(En_ImportanceLevel.getById(issue.getImpLevel()));

        Company initiatorCompany = issue.getInitiatorCompany();
        view.company().setValue(EntityOption.fromCompany(initiatorCompany));
        view.changeCompany(initiatorCompany);
        view.initiator().setValue( ContactShortView.fromPerson( issue.getInitiator() ));

        view.product().setValue(EntityOption.fromProduct(issue.getProduct()));
        view.manager().setValue(EntityOption.fromPerson(issue.getManager()));
    }

    private void fillIssueObject(CaseObject issue){
        issue.setName(view.name().getValue());
        issue.setPrivateCase(view.isLocal().getValue());
        issue.setInfo(view.description().getText());

        issue.setStateId(view.state().getValue().getId());
        issue.setImpLevel(view.importance().getValue().getId());

        issue.setInitiatorCompany(Company.fromEntityOption(view.company().getValue()));
        issue.setInitiator(Person.fromContactShortView(view.initiator().getValue()));

        issue.setProduct(DevUnit.fromEntityOption(view.product().getValue()));
        issue.setManager(Person.fromEntityOption(view.manager().getValue()));
    }


    private boolean validateFieldsAndGetResult(){
        return view.nameValidator().isValid() &&
                view.stateValidator().isValid() &&
                view.importanceValidator().isValid() &&
                view.companyValidator().isValid() &&
                view.productValidator().isValid() &&
                view.managerValidator().isValid();
    }

    @Inject
    AbstractIssueEditView view;
    @Inject
    IssueServiceAsync issueService;
    @Inject
    Lang lang;

    private AppEvents.InitDetails initDetails;
    private CaseObject issue;

}
