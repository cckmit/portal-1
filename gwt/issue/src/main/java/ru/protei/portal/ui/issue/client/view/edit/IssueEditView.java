package ru.protei.portal.ui.issue.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.AddEvent;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.attachment.list.AttachmentList;
import ru.protei.portal.ui.common.client.widget.attachment.list.HasAttachments;
import ru.protei.portal.ui.common.client.widget.attachment.list.events.RemoveEvent;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;
import ru.protei.portal.ui.common.client.widget.issuelinks.IssueLinks;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanySelector;
import ru.protei.portal.ui.common.client.widget.selector.dict.ImportanceButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.PersonButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.product.ProductButtonSelector;
import ru.protei.portal.ui.common.client.widget.timefield.HasTime;
import ru.protei.portal.ui.common.client.widget.timefield.TimeLabel;
import ru.protei.portal.ui.common.client.widget.uploader.AttachmentUploader;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.issue.client.activity.edit.AbstractIssueEditActivity;
import ru.protei.portal.ui.issue.client.activity.edit.AbstractIssueEditView;
import ru.protei.portal.ui.issue.client.widget.state.buttonselector.IssueStatesButtonSelector;

import java.util.Set;


/**
 * Вид создания и редактирования обращения
 */
public class IssueEditView extends Composite implements AbstractIssueEditView, ResizeHandler {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        state.setDefaultValue(lang.selectIssueState());
        importance.setDefaultValue(lang.selectIssueImportance());
        company.setDefaultValue(lang.selectIssueCompany());
        product.setDefaultValue(lang.selectIssueProduct());
        manager.setDefaultValue(lang.selectIssueManager());
        initiator.setDefaultValue(lang.selectIssueInitiator());
        initiator.setAddButtonText(lang.personCreateNew());
        initiator.setAddButtonVisible(true);
        Window.addResizeHandler(this);
    }

    @Override
    protected void onAttach() {
        super.onAttach();
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        if (resizeFinishedTimer.isRunning()) {
            resizeFinishedTimer.cancel();
        }
    }

    @Override
    public void onResize(ResizeEvent event) {
        if (resizeFinishedTimer.isRunning()) {
            resizeFinishedTimer.cancel();
        }
        resizeFinishedTimer.schedule(200);
    }

    @Override
    public void setActivity(AbstractIssueEditActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasValue<String> name() {
        return name;
    }

    @Override
    public HasText description() {
        return description;
    }

    @Override
    public HasValue<En_CaseState> state() {
        return state;
    }

    @Override
    public HasValue<En_ImportanceLevel> importance() {
        return importance;
    }

    @Override
    public HasTime timeElapsed() {
        return timeElapsed;
    }

    @Override
    public HasValue<EntityOption> company() {
        return company;
    }

    @Override
    public HasValue<PersonShortView> initiator() {
        return initiator;
    }

    @Override
    public HasValue<PersonShortView> manager() {
        return manager;
    }

    @Override
    public HasValue<ProductShortView> product() {
        return product;
    }

    @Override
    public HasValue<Boolean> isLocal() {
        return local;
    }

    @Override
    public HasValue<Set<PersonShortView>> notifiers() {
        return notifiers;
    }

    @Override
    public HasValue<Set<CaseLink>> links() {
        return links;
    }

    @Override
    public HasValidable nameValidator() {
        return name;
    }

    @Override
    public HasValidable stateValidator() {
        return state;
    }

    @Override
    public HasValidable importanceValidator() {
        return importance;
    }

    @Override
    public HasVisibility timeElapsedContainer() {
        return timeElapsedContainer;
    }

    @Override
    public HasValidable companyValidator() { return company; }

    @Override
    public HasValidable initiatorValidator() { return initiator; }

    @Override
    public HasValidable productValidator() {
        return product;
    }

    @Override
    public HasValidable managerValidator() {
        return manager;
    }

    @Override
    public HasEnabled initiatorState() {
        return initiator;
    }

    @Override
    public HasVisibility numberVisibility(){
        return number;
    }

    @Override
    public HasValue<Integer> number(){
        return number;
    }

    @Override
    public void setSubscriptionEmails(String value) {
        subscriptions.setInnerText(value);
    }

    @Override
    public HasWidgets getCommentsContainer() {
        return commentsContainer;
    }

    @Override
    public HasAttachments attachmentsContainer(){
        return attachmentContainer;
    }

    @Override
    public void setFileUploadHandler(AttachmentUploader.FileUploadHandler handler){
        fileUploader.setUploadHandler(handler);
    }

    @Override
    public void setCaseNumber(Long caseNumber) {
        fileUploader.autoBindingToCase(caseNumber);
    }

    @Override
    public HasVisibility saveVisibility() {
        return saveButton;
    }

    @Override
    public HasEnabled companyEnabled() {
        return company;
    }

    @Override
    public HasEnabled productEnabled() {
        return product;
    }

    @Override
    public HasEnabled managerEnabled() {
        return manager;
    }

    @Override
    public HasVisibility caseSubscriptionContainer() {
        return caseSubscriptionContainers;
    }

    @Override
    public HasVisibility privacyVisibility() {
        return new HasVisibility() {
            @Override
            public boolean isVisible() {
                return local.isVisible();
            }

            @Override
            public void setVisible( boolean b ) {
                local.setVisible( b );
            }
        };
    }

    @Override
    public void refreshFooterBtnPosition() {
        Scheduler.get().scheduleDeferred(() -> {
            int wHeight = Window.getClientHeight();
            int pHeight = root.getOffsetHeight();
            setFooterFixed(pHeight - DIFF_BEFORE_FOOTER_FIXED > wHeight);
        });
    }

    @Override
    public void setStateFilter(Selector.SelectorFilter<En_CaseState> filter) {
        state.setFilter(filter);
    }

    private void setFooterFixed(boolean isFixed) {
        if (isFixed) {
            root.addStyleName(UiConstants.Styles.FOOTER);
        } else {
            root.removeStyleName(UiConstants.Styles.FOOTER);
        }
    }

    @UiHandler( "company" )
    public void onChangeCompany( ValueChangeEvent< EntityOption > event ){
        Company company = Company.fromEntityOption( event.getValue() );

        initiator.setEnabled( company != null );
        initiator.updateCompany(company);
        initiator.setValue( null );

        if ( activity != null ) {
            activity.onCompanyChanged();
        }
    }

    @UiHandler( "saveButton" )
    public void onSaveClicked( ClickEvent event ) {
        if ( activity != null ) {
            activity.onSaveClicked();
        }
    }
    @UiHandler( "cancelButton" )
    public void onCancelClicked( ClickEvent event ) {
        if ( activity != null ) {
            activity.onCancelClicked();
        }

    }

    @UiHandler("attachmentContainer")
    public void attachmentContainerRemove(RemoveEvent event) {
        activity.removeAttachment(event.getAttachment());
    }

    @UiHandler("initiator")
    public void onAddContactEvent(AddEvent event) {
        activity.onCreateContactClicked();
    }

    @UiHandler("local")
    public void onLocalClick(ClickEvent event) {
        if (activity != null) {
            activity.onLocalClicked();
        }
    }

    @Override
    public void showComments(boolean isShow) {
        if(isShow)
            comments.removeClassName(UiConstants.Styles.HIDE);
        else
            comments.addClassName(UiConstants.Styles.HIDE);
    }

    @UiField
    HTMLPanel root;

    @UiField
    ValidableTextBox name;

    @UiField
    TextArea description;

    @UiField
    ToggleButton local;

    @UiField
    IntegerBox number;

    @Inject
    @UiField(provided = true)
    IssueStatesButtonSelector state;

    @Inject
    @UiField(provided = true)
    ImportanceButtonSelector importance;

    @Inject
    @UiField(provided = true)
    TimeLabel timeElapsed;

    @Inject
    @UiField(provided = true)
    CompanySelector company;

    @Inject
    @UiField(provided = true)
    PersonButtonSelector initiator;

    @Inject
    @UiField(provided = true)
    ProductButtonSelector product;

    @Inject
    @UiField(provided = true)
    EmployeeButtonSelector manager;

    @Inject
    @UiField(provided = true)
    EmployeeMultiSelector notifiers;

    @UiField
    Button saveButton;
    @UiField
    Button cancelButton;
    @Inject
    @UiField
    Lang lang;
    @UiField
    HTMLPanel commentsContainer;
    @UiField
    DivElement comments;
    @Inject
    @UiField
    AttachmentUploader fileUploader;
    @Inject
    @UiField(provided = true)
    AttachmentList attachmentContainer;
    @UiField
    DivElement subscriptions;
    @UiField
    HTMLPanel nameInputGroupContainer;
    @UiField
    HTMLPanel nameContainer;
    @UiField
    HTMLPanel caseSubscriptionContainers;
    @Inject
    @UiField(provided = true)
    IssueLinks links;
    @UiField
    HTMLPanel timeElapsedContainer;

    private static final int DIFF_BEFORE_FOOTER_FIXED = 200;

    private Timer resizeFinishedTimer = new Timer() {
        @Override
        public void run() {
            refreshFooterBtnPosition();
        }
    };

    private AbstractIssueEditActivity activity;

    interface IssueEditViewUiBinder extends UiBinder<HTMLPanel, IssueEditView> {}
    private static IssueEditViewUiBinder ourUiBinder = GWT.create(IssueEditViewUiBinder.class);
}