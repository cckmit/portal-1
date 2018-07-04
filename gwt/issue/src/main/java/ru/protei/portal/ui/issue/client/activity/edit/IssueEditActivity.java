package ru.protei.portal.ui.issue.client.activity.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.context.client.annotation.ContextAware;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.AttachmentServiceAsync;
import ru.protei.portal.ui.common.client.service.CompanyControllerAsync;
import ru.protei.portal.ui.common.client.service.IssueControllerAsync;
import ru.protei.portal.ui.common.client.widget.uploader.AttachmentUploader;
import ru.protei.portal.ui.common.shared.model.Profile;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.common.shared.model.ShortRequestCallback;

import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.dict.En_CaseState.CREATED;

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

        if (event.id == null) {
            fireEvent(new AppEvents.InitPanelName(lang.newIssue()));
            if (issue != null) {
                initialView(issue);
            } else {
                CaseObject caseObject = new CaseObject();
                caseObject.setPrivateCase(true);
                initialView(caseObject);
            }
        } else {
            fireEvent(new AppEvents.InitPanelName(lang.issueEdit()));
            requestIssue(event.id, this::initialView);
        }

    }

    @Event
    public void onAddingAttachments( AttachmentEvents.Add event ) {
        if(view.isAttached() && issue.getId().equals(event.issueId)) {
            addAttachmentsToCase(event.attachments);
        }
    }

    @Event
    public void onRemovingAttachments( AttachmentEvents.Remove event ) {
        if(view.isAttached() && issue.getId().equals(event.issueId)) {
            event.attachments.forEach(view.attachmentsContainer()::remove);
            issue.getAttachments().removeAll(event.attachments);
            issue.setAttachmentExists(!issue.getAttachments().isEmpty());
        }
    }

    @Event
    public void onFillPerson(PersonEvents.PersonCreated event) {
        if (CrmConstants.Issue.CREATE_CONTACT_IDENTITY.equals(event.origin) && issue != null && event.person != null) {
            issue.setInitiator(event.person);
            issue.setInitiatorId(event.person.getId());
            if (issue.getInitiator() != null) {
                view.initiator().setValue(PersonShortView.fromPerson(issue.getInitiator()));
            }
        }
    }

    @Event
    public void onChangeCommentsView(IssueEvents.ChangeCommentsView event) {
        view.refreshFooterBtnPosition();
    }

    @Override
    public void onSaveClicked() {
        if(!validateView()){
            return;
        }

        fillIssueObject(issue);

        issueService.saveIssue(issue, new RequestCallback<CaseObject>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(throwable.getMessage(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(CaseObject caseObject) {
                if (issue.getId() != null) {
                    fireEvent(new IssueEvents.SaveComment(caseObject.getId()));
                } else {
                    fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new IssueEvents.ChangeModel());
                    fireEvent(new Back());
                }
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
        Company companyOption = Company.fromEntityOption(view.company().getValue());

        view.initiatorState().setEnabled(companyOption != null);
        view.initiatorUpdateCompany(companyOption);
        view.initiator().setValue(null);

        if ( companyOption == null ) {
            setSubscriptionEmails(getSubscriptionsBasedOnPrivacy(null, lang.issueCompanySubscriptionNeedSelectCompany()));
        } else {
            companyService.getCompanySubscription(companyOption.getId(), new ShortRequestCallback<List<CompanySubscription>>()
                    .setOnSuccess(subscriptions -> setSubscriptionEmails(getSubscriptionsBasedOnPrivacy(subscriptions, lang.issueCompanySubscriptionNotDefined()))));

            companyService.getCompanyCaseStates(companyOption.getId(), new ShortRequestCallback<List<CaseState>>()
                    .setOnSuccess(caseStates -> {
                        view.setStateFilter(caseStateFilter.makeFilter(caseStates));
                        fireEvent(new CaseStateEvents.UpdateSelectorOptions());
                    }));
        }

        fireEvent(new CaseStateEvents.UpdateSelectorOptions());
    }

    @Override
    public void onCreateContactClicked() {
        if (view.company().getValue() != null) {
            fillIssueObject(issue);
            fireEvent(new ContactEvents.Edit(null, Company.fromEntityOption(view.company().getValue()), CrmConstants.Issue.CREATE_CONTACT_IDENTITY));
        }
    }

    @Override
    public void onLocalClicked() {
        setSubscriptionEmails(getSubscriptionsBasedOnPrivacy(subscriptionsList, subscriptionsListEmptyMessage));
    }

    private void initialView(CaseObject issue){
        this.issue = issue;
        fillView(this.issue);
    }

    private void requestIssue(Long number, Consumer<CaseObject> successAction){
        issueService.getIssue(number, new RequestCallback<CaseObject>() {
            @Override
            public void onError(Throwable throwable) {}

            @Override
            public void onSuccess(CaseObject issue) {
                successAction.accept(issue);
            }
        });
    }

    private void fillView(CaseObject issue) {
        view.companyEnabled().setEnabled( isCompanyChangeAllowed(issue) );
        view.productEnabled().setEnabled( policyService.hasPrivilegeFor( En_Privilege.ISSUE_PRODUCT_EDIT ) );
        view.managerEnabled().setEnabled( policyService.hasPrivilegeFor( En_Privilege.ISSUE_MANAGER_EDIT) );
        view.privacyVisibility().setVisible( policyService.hasPrivilegeFor( En_Privilege.ISSUE_PRIVACY_VIEW ) );

        view.attachmentsContainer().clear();
        view.setCaseNumber(issue.getCaseNumber());

        if ( issue.getId() != null ) {
            view.showComments(true);
            view.attachmentsContainer().add(issue.getAttachments());
            fireEvent( new IssueEvents.ShowComments( view.getCommentsContainer(), issue.getId()) );
        } else {
            view.showComments(false);
            view.getCommentsContainer().clear();
        }

        if(policyService.hasPrivilegeFor(En_Privilege.ISSUE_FILTER_MANAGER_VIEW)) { //TODO change rule
            view.notifiers().setValue(issue.getNotifiers() == null ? new HashSet<>() :
                    issue.getNotifiers().stream().map(PersonShortView::fromPerson).collect(Collectors.toSet()));
            view.caseSubscriptionContainer().setVisible(true);
        }else{
            view.caseSubscriptionContainer().setVisible(false);
        }

        view.links().setValue(issue.getLinks() == null ? null : new HashSet<>(issue.getLinks()));

        view.name().setValue(issue.getName());

        view.numberVisibility().setVisible( issue.getId() != null );
        view.number().setValue( issue.getId() == null ? null : issue.getCaseNumber().intValue() );

        view.isLocal().setValue(issue.isPrivateCase());
        view.description().setText(issue.getInfo());

        view.state().setValue(issue.getId() == null ? CREATED : En_CaseState.getById(issue.getStateId()));
        view.importance().setValue(issue.getId() == null ? En_ImportanceLevel.BASIC : En_ImportanceLevel.getById(issue.getImpLevel()));

        Company initiatorCompany = issue.getInitiatorCompany();
        if ( initiatorCompany == null ) {
            initiatorCompany = policyService.getUserCompany();
        }

        view.company().setValue(EntityOption.fromCompany(initiatorCompany));
        onCompanyChanged();
        view.initiator().setValue( decideInitiator(issue) );
        view.product().setValue( ProductShortView.fromProduct( issue.getProduct() ) );
        view.manager().setValue( PersonShortView.fromPerson( issue.getManager() ) );
        view.saveVisibility().setVisible( policyService.hasPrivilegeFor( En_Privilege.ISSUE_EDIT ) );

        view.refreshFooterBtnPosition();
    }

    private void fillIssueObject(CaseObject issue){
        issue.setName(view.name().getValue());
        issue.setPrivateCase( view.isLocal().getValue() );
        issue.setInfo(view.description().getText());

        issue.setStateId(view.state().getValue().getId());
        issue.setImpLevel(view.importance().getValue().getId());

        issue.setInitiatorCompany(Company.fromEntityOption(view.company().getValue()));
        issue.setInitiator(Person.fromPersonShortView(view.initiator().getValue()));
        issue.setProduct( DevUnit.fromProductShortView( view.product().getValue() ) );
        issue.setManager( Person.fromPersonShortView( view.manager().getValue() ) );
        issue.setNotifiers(view.notifiers().getValue().stream().map(Person::fromPersonShortView).collect(Collectors.toSet()));
        issue.setLinks(view.links().getValue() == null ? new ArrayList<>() : new ArrayList<>(view.links().getValue()));
    }

    private boolean validateView() {
        boolean isFieldsValid = view.nameValidator().isValid() &&
                view.stateValidator().isValid() &&
                view.importanceValidator().isValid() &&
                view.companyValidator().isValid();

        if(!isFieldsValid) return false;

        if(view.manager().getValue() != null && view.state().getValue() == CREATED){
            fireEvent(new NotifyEvents.Show(lang.errCreatedStateSelected(), NotifyEvents.NotifyType.ERROR));
            return false;
        }
        if(CREATED != view.state().getValue() && view.product().getValue() == null){
            fireEvent(new NotifyEvents.Show(lang.errProductNotSelected(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        return true;
    }

    private void addAttachmentsToCase(Collection<Attachment> attachments){
        view.attachmentsContainer().add(attachments);
        if(issue.getAttachments() == null || issue.getAttachments().isEmpty())
            issue.setAttachments(new ArrayList<>());

        issue.getAttachments().addAll(attachments);
        issue.setAttachmentExists(true);
    }

    private PersonShortView decideInitiator(CaseObject issue) {
        if (isNew(issue)) {
            Profile profile = policyService.getProfile();
            return new PersonShortView(profile.getShortName(), profile.getId(), profile.isFired());
        }
        return PersonShortView.fromPerson(issue.getInitiator());
    }

    private boolean isNew(CaseObject issue) {
        return issue.getId() == null;
    }

    private String getSubscriptionsBasedOnPrivacy(List<CompanySubscription> subscriptionsList, String emptyMessage) {
        this.subscriptionsList = subscriptionsList;
        this.subscriptionsListEmptyMessage = emptyMessage;
        return subscriptionsList == null || subscriptionsList.isEmpty()
                ? subscriptionsListEmptyMessage
                : subscriptionsList.stream()
                .map(CompanySubscription::getEmail)
                .filter(mail -> !view.isLocal().getValue() || CompanySubscription.isProteiRecipient(mail))
                .collect(Collectors.joining(", "));
    }

    private boolean isCompanyChangeAllowed(CaseObject issue) {
        if (policyService.hasPrivilegeFor(En_Privilege.ISSUE_COMPANY_EDIT) &&
                (issue.getId() == null || subscriptionsList == null || subscriptionsList.isEmpty() || view.isLocal().getValue())
        ) {
            return true;
        }
        return subscriptionsList.stream()
                .map(CompanySubscription::getEmail)
                .allMatch(CompanySubscription::isProteiRecipient);
    }

    private void setSubscriptionEmails(String value) {
        view.setSubscriptionEmails(value);
        view.companyEnabled().setEnabled(isCompanyChangeAllowed(issue));
    }

    @Inject
    AbstractIssueEditView view;
    @Inject
    IssueControllerAsync issueService;
    @Inject
    AttachmentServiceAsync attachmentService;
    @Inject
    Lang lang;
    @Inject
    PolicyService policyService;
    @Inject
    CompanyControllerAsync companyService;
    @Inject
    CaseStateFilterProvider caseStateFilter;

    private List<CompanySubscription> subscriptionsList;
    private String subscriptionsListEmptyMessage;
    private AppEvents.InitDetails initDetails;
    @ContextAware
    CaseObject issue;

    private static final Logger log = Logger.getLogger(IssueEditActivity.class.getName());
}
