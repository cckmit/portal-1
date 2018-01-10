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
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.AttachmentEvents;
import ru.protei.portal.ui.common.client.events.IssueEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.AttachmentServiceAsync;
import ru.protei.portal.ui.common.client.service.CompanyServiceAsync;
import ru.protei.portal.ui.common.client.service.IssueServiceAsync;
import ru.protei.portal.ui.common.client.widget.uploader.AttachmentUploader;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Активность создания и редактирования обращения
 */
public abstract class IssueEditActivity implements AbstractIssueEditActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity( this );
        view.setFileUploadHandler(new AttachmentUploader.FileUploadHandler() {
            @Override
            public void onSuccess(Attachment attachment) {
                addAttachmentsToCase(Collections.singleton(attachment));
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

        if(event.id == null) {
            fireEvent(new AppEvents.InitPanelName(lang.newIssue()));
            initialView(new CaseObject());
        }else {
            fireEvent(new AppEvents.InitPanelName(lang.issueEdit()));
            requestIssue(event.id, this::initialView);
        }
    }

    @Event
    public void onAddingAttachments( AttachmentEvents.Add event ) {
        if(view.isAttached() && issue.getId().equals(event.caseId)) {
            addAttachmentsToCase(event.attachments);
        }
    }

    @Event
    public void onRemovingAttachments( AttachmentEvents.Remove event ) {
        if(view.isAttached() && issue.getId().equals(event.caseId)) {
            event.attachments.forEach(view.attachmentsContainer()::remove);
            issue.getAttachments().removeAll(event.attachments);
            issue.setAttachmentExists(!issue.getAttachments().isEmpty());
        }
    }

    @Override
    public void onSaveClicked() {
        if(!isFieldsValid()){
            return;
        }
        if(isNoChanges(issue)){
            fireEvent(new NotifyEvents.Show(lang.noChanges(), NotifyEvents.NotifyType.INFO));
            return;
        }

        fillIssueObject(issue);

        issueService.saveIssue(issue, new RequestCallback<CaseObject>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(throwable.getMessage(), NotifyEvents.NotifyType.SUCCESS));
            }

            @Override
            public void onSuccess(CaseObject caseObject) {
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
        attachmentService.removeAttachmentEverywhere(attachment.getId(), new RequestCallback<Boolean>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.removeFileError(), NotifyEvents.NotifyType.ERROR));
            }
            @Override
            public void onSuccess(Boolean result) {
                if(!result){
                    onError(null);
                    return;
                }

                view.attachmentsContainer().remove(attachment);
                issue.getAttachments().remove(attachment);
                issue.setAttachmentExists(!issue.getAttachments().isEmpty());
                if(issue.getId() != null)
                    fireEvent( new IssueEvents.ShowComments( view.getCommentsContainer(), issue.getId() ) );

            }
        });
    }

    @Override
    public void onCompanyChanged() {
        if ( view.company().getValue() == null ) {
            view.setSubscriptionEmails( "" );
        } else {
            companyService.getCompanySubscription( view.company().getValue().getId(), new RequestCallback< List<CompanySubscription> >() {
                @Override
                public void onError( Throwable throwable ) {}

                @Override
                public void onSuccess( List<CompanySubscription> subscriptions ) {
                    view.setSubscriptionEmails(
                            subscriptions == null
                                    ? ""
                                    : subscriptions.stream()
                                    .map( CompanySubscription::getEmail )
                                    .collect( Collectors.joining( ", " ) ) );
                }
            });
        }
    }

    private void initialView(CaseObject issue){
        this.issue = issue;
        fillView(this.issue);
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

    private void fillView(CaseObject issue) {
        view.companyEnabled().setEnabled( policyService.hasPrivilegeFor( En_Privilege.ISSUE_COMPANY_EDIT ) && issue.getId() == null );
        view.productEnabled().setEnabled( policyService.hasPrivilegeFor( En_Privilege.ISSUE_PRODUCT_EDIT ) );
        view.managerEnabled().setEnabled( policyService.hasPrivilegeFor( En_Privilege.ISSUE_MANAGER_EDIT) );
        view.privacyVisibility().setVisible( policyService.hasPrivilegeFor( En_Privilege.ISSUE_PRIVACY_VIEW ) );

        view.attachmentsContainer().clear();
        view.setCaseId(issue.getId());

        if ( issue.getId() != null ) {
            view.showComments(true);
            view.attachmentsContainer().add(issue.getAttachments());
            fireEvent( new IssueEvents.ShowComments( view.getCommentsContainer(), issue.getId()) );
        }else {
            view.showComments(false);
            view.getCommentsContainer().clear();
        }

        view.name().setValue(issue.getName());
        view.isLocal().setValue(issue.isPrivateCase());
        view.description().setText(issue.getInfo());

        view.state().setValue(issue.getId() == null ? En_CaseState.CREATED : En_CaseState.getById(issue.getStateId()));
        view.importance().setValue(issue.getId() == null ? En_ImportanceLevel.BASIC : En_ImportanceLevel.getById(issue.getImpLevel()));

        Company initiatorCompany = issue.getInitiatorCompany();
        if ( initiatorCompany == null ) {
            initiatorCompany = policyService.getUserCompany();
        }

        view.company().setValue(EntityOption.fromCompany(initiatorCompany), true);
        view.initiator().setValue( PersonShortView.fromPerson( issue.getInitiator() ) );
        view.product().setValue( ProductShortView.fromProduct( issue.getProduct() ) );
        view.manager().setValue( PersonShortView.fromPerson( issue.getManager() ) );
        view.saveVisibility().setVisible( policyService.hasPrivilegeFor( En_Privilege.ISSUE_EDIT ) );
    }

    private void fillIssueObject(CaseObject issue){
        issue.setName(view.name().getValue());
        issue.setPrivateCase( view.isLocal().getValue() );
        issue.setInfo(view.description().getText());

        isChangedStatus = false;
        if (issue.getStateId() != view.state().getValue().getId()) {
            isChangedStatus = true;
        }
        issue.setStateId(view.state().getValue().getId());
        issue.setImpLevel(view.importance().getValue().getId());

        issue.setInitiatorCompany(Company.fromEntityOption(view.company().getValue()));
        issue.setInitiator(Person.fromPersonShortView(view.initiator().getValue()));
        issue.setProduct( DevUnit.fromProductShortView( view.product().getValue() ) );
        issue.setManager( Person.fromPersonShortView( view.manager().getValue() ) );
    }

    private boolean isFieldsValid(){
        return view.nameValidator().isValid() &&
                view.stateValidator().isValid() &&
                view.importanceValidator().isValid() &&
                view.companyValidator().isValid();
//        && view.initiatorValidator().isValid();
    }

    private boolean isNoChanges(CaseObject issue){
        return
            Objects.equals(issue.getName(), view.name().getValue())
            && Objects.equals(issue.getInfo(), view.description().getText())
            && Objects.equals(issue.isPrivateCase(), view.isLocal().getValue())
            && Objects.equals(issue.getState(), view.state().getValue())
            && Objects.equals(issue.getImpLevel(), view.importance().getValue().getId())
            && Objects.equals(issue.getInitiatorCompanyId(), view.company().getValue() == null? null: view.company().getValue().getId())
            && Objects.equals(issue.getInitiatorId(), view.initiator().getValue() == null? null: view.initiator().getValue().getId())
            && Objects.equals(issue.getProductId(), view.product().getValue() == null? null: view.product().getValue().getId())
            && Objects.equals(issue.getManagerId(), view.manager().getValue() == null? null: view.manager().getValue().getId());
    }

    private void addAttachmentsToCase(Collection<Attachment> attachments){
        view.attachmentsContainer().add(attachments);
        if(issue.getAttachments() == null)
            issue.setAttachments(new ArrayList<>());

        issue.getAttachments().addAll(attachments);
        issue.setAttachmentExists(true);
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
    @Inject
    CompanyServiceAsync companyService;

    private AppEvents.InitDetails initDetails;
    private CaseObject issue;
    private boolean isChangedStatus = false;
    private int initialHash;
}
