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
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.IssueEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.issue.client.service.IssueServiceAsync;

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
        if(!view.nameValidator().isValid()){
            view.nameValidator().setValid(false);
            return;
        }

        fillIssueObject(tempIssue);

        issueService.saveIssue(tempIssue, new RequestCallback<Boolean>() {
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

    private void resetValidation(){
        view.nameValidator().setValid(true);
    }

    private void initialView(CaseObject issue){
        tempIssue = issue;
        fillView(tempIssue);
        resetValidation();
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
        view.name().setText(issue.getName());
        view.isLocal().setValue(issue.isPrivateCase());
        view.description().setText(issue.getInfo());

        view.state().setValue(
                En_CaseState.getById(issue.getStateId()));
        view.importance().setValue(
                En_ImportanceLevel.getById(issue.getImpLevel()));

        EntityOption entityOption = new EntityOption(issue.getInitiatorCompany().getCname(), issue.getInitiatorCompany().getId());
        view.company().setValue(entityOption);
        view.initiator().setValue(issue.getInitiator());

        view.product().setValue(issue.getProduct());
        view.manager().setValue(issue.getManager());
    }

    private void fillIssueObject(CaseObject issue){
        issue.setName(view.name().getText());
        issue.setPrivateCase(view.isLocal().getValue());
        issue.setInfo(view.description().getText());

        issue.setStateId(view.state().getValue().getId());
        issue.setImpLevel(view.importance().getValue().getId());

        Company company = new Company(view.company().getValue().getId());
        issue.setInitiatorCompany(company);
        issue.setInitiator(view.initiator().getValue());

        issue.setProduct(view.product().getValue());
        issue.setManager(view.manager().getValue());
    }

    @Inject
    AbstractIssueEditView view;
    @Inject
    IssueServiceAsync issueService;
    @Inject
    Lang lang;

    private AppEvents.InitDetails initDetails;
    private CaseObject tempIssue;


}
