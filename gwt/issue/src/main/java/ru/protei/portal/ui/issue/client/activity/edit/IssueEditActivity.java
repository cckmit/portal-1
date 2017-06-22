package ru.protei.portal.ui.issue.client.activity.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.AttachmentCollection;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.IssueEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.AttachmentServiceAsync;
import ru.protei.portal.ui.common.client.service.IssueServiceAsync;
import ru.protei.portal.ui.common.client.widget.uploader.FileUploader;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Активность создания и редактирования обращения
 */
public abstract class IssueEditActivity implements AbstractIssueEditActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity( this );
        view.setFileUploadHandler(new FileUploader.FileUploadHandler() {
            @Override
            public void onSuccess(Attachment attachment) {
                attachmentCollection.addAttachment(attachment);
            }
            @Override
            public void onError() {
                fireEvent(new NotifyEvents.Show(lang.uploadFileError(), NotifyEvents.NotifyType.ERROR));
            }
        });
    }

    @Event
    public void onInitDetails( AppEvents.InitDetails initDetails ) {
        this.initDetails = initDetails;
    }

    @Event
    public void onShow( IssueEvents.Edit event ) {
        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());
        attachmentCollection.clear();

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
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(throwable.getMessage(), NotifyEvents.NotifyType.SUCCESS));
            }

            @Override
            public void onSuccess(Boolean aBoolean) {
                fireEvent(new IssueEvents.ChangeModel());
                fireEvent(new Back());
                fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
            }
        });
    }

    @Override
    public void onCancelClicked() {
        fireEvent(new Back());
    }

    @Override
    public void removeAttachment(Attachment attachment) {
        if(issue.getId() == null || issue.getAttachmentsIds() == null || !issue.getAttachmentsIds().contains(attachment.getId())){
            attachmentService.removeAttachmentEverywhere(attachment.getId(), new RequestCallback<Boolean>() {
                @Override
                public void onError(Throwable throwable) {
                    fireEvent(new NotifyEvents.Show(lang.removeFileError(), NotifyEvents.NotifyType.ERROR));
                }
                @Override
                public void onSuccess(Boolean result) {
                    if(!result){
                        onError(null);
                    }
                    attachmentCollection.removeAttachment(attachment);
                    if(issue.getId() != null)
                        fireEvent( new IssueEvents.ShowComments( view.getCommentsContainer(), issue.getId(), attachmentCollection ) );
                }
            });
        }else
            attachmentCollection.removeAttachment(attachment);
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
        attachmentService.getAttachmentsByCaseId(id, new RequestCallback<List<Attachment>>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent( new NotifyEvents.Show( lang.attachmentsNotLoaded(), NotifyEvents.NotifyType.ERROR ) );
            }
            @Override
            public void onSuccess(List<Attachment> result) {
                view.attachmentsContainer().clear();
                result.forEach(attachmentCollection::addAttachment);
            }
        });
    }

    private void fillView(CaseObject issue) {
        if ( issue.getId() != null ) {
            view.showComments(true);
            fireEvent( new IssueEvents.ShowComments( view.getCommentsContainer(), issue.getId(), attachmentCollection) );
        }else {
            view.showComments(false);
            view.getCommentsContainer().clear();
            view.attachmentsContainer().clear();
        }

        view.name().setValue(issue.getName());
        view.isLocal().setValue(issue.isPrivateCase());
        view.description().setText(issue.getInfo());

        view.state().setValue(issue.getId() == null ? En_CaseState.CREATED : En_CaseState.getById(issue.getStateId()));
        view.importance().setValue(issue.getId() == null ? En_ImportanceLevel.BASIC : En_ImportanceLevel.getById(issue.getImpLevel()));

        Company initiatorCompany = issue.getInitiatorCompany();
        view.company().setValue(EntityOption.fromCompany(initiatorCompany));
        view.changeCompany(initiatorCompany);
        view.initiator().setValue( PersonShortView.fromPerson(issue.getInitiator()));
        if ( issue.getInitiatorCompany() != null ) {
            view.setSubscriptionEmails( issue.getInitiatorCompany().getSubscriptions() == null
                    ? ""
                    : issue.getInitiatorCompany().getSubscriptions()
                    .stream()
                    .map( CompanySubscription::getEmail )
                    .collect( Collectors.joining(", ") ) );
        }

        view.product().setValue( ProductShortView.fromProduct(issue.getProduct()));
        view.manager().setValue(PersonShortView.fromPerson(issue.getManager()));
        view.saveVisibility().setVisible( policyService.hasPrivilegeFor( En_Privilege.ISSUE_EDIT ) );
    }

    private void fillIssueObject(CaseObject issue){
        issue.setName(view.name().getValue());
        issue.setPrivateCase(view.isLocal().getValue());
        issue.setInfo(view.description().getText());

        issue.setStateId(view.state().getValue().getId());
        issue.setImpLevel(view.importance().getValue().getId());

        issue.setInitiatorCompany(Company.fromEntityOption(view.company().getValue()));
        issue.setInitiator(Person.fromPersonShortView(view.initiator().getValue()));

        issue.setProduct(DevUnit.fromProductShortView(view.product().getValue()));
        issue.setManager(Person.fromPersonShortView( view.manager().getValue()));

        issue.setAttachmentsIds(new ArrayList<>(attachmentCollection.keySet()));
    }

    private boolean validateFieldsAndGetResult(){
        return view.nameValidator().isValid() &&
                view.stateValidator().isValid() &&
                view.importanceValidator().isValid() &&
                view.companyValidator().isValid() &&
                view.initiatorValidator().isValid() &&
                view.productValidator().isValid() &&
                view.managerValidator().isValid();
    }

    @Inject
    AbstractIssueEditView view;
    @Inject
    IssueServiceAsync issueService;
    @Inject
    AttachmentServiceAsync attachmentService;
    @Inject
    Lang lang;
    @Inject
    PolicyService policyService;

    private AttachmentCollection attachmentCollection = new AttachmentCollection() {
        @Override
        public void addAttachment(Attachment attachment) {
            put(attachment.getId(), attachment);
            view.attachmentsContainer().add(attachment);
        }

        @Override
        public void removeAttachment(Attachment attachment) {
            remove(attachment.getId());
            view.attachmentsContainer().remove(attachment);
        }
    };

    private AppEvents.InitDetails initDetails;
    private CaseObject issue;
}
