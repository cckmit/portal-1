package ru.protei.portal.ui.issue.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanySelector;
import ru.protei.portal.ui.common.client.widget.selector.dict.ImportanceButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.ContactButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.product.ProductButtonSelector;
import ru.protei.portal.ui.common.client.widget.uploader.FileUploader;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.issue.client.activity.edit.AbstractIssueEditActivity;
import ru.protei.portal.ui.issue.client.activity.edit.AbstractIssueEditView;
import ru.protei.portal.ui.issue.client.widget.state.buttonselector.IssueStatesButtonSelector;

/**
 * Вид создания и редактирования обращения
 */
public class IssueEditView extends Composite implements AbstractIssueEditView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        state.setDefaultValue(lang.selectIssueState());
        importance.setDefaultValue(lang.selectIssueImportance());
        initiator.setDefaultValue(lang.selectIssueInitiator());
        company.setDefaultValue(lang.selectIssueCompany());
        product.setDefaultValue(lang.selectIssueProduct());
        manager.setDefaultValue(lang.selectIssueManager());
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
    public void changeCompany(Company company){
        initiator.updateCompany(company);
    }

    @Override
    public HasWidgets getCommentsContainer() {
        return commentsContainer;
    }

    @UiHandler("company")
    public void onChangeCompany(ValueChangeEvent<EntityOption> event){
        Company company = Company.fromEntityOption(event.getValue());

        initiator.setEnabled(company != null);
        changeCompany(company);
        initiator.setValue(null);
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

    @UiField
    ValidableTextBox name;

    @UiField
    TextArea description;

    @UiField
    ToggleButton local;

    @Inject
    @UiField(provided = true)
    IssueStatesButtonSelector state;

    @Inject
    @UiField(provided = true)
    ImportanceButtonSelector importance;

    @Inject
    @UiField(provided = true)
    CompanySelector company;

    @Inject
    @UiField(provided = true)
    ContactButtonSelector initiator;

    @Inject
    @UiField(provided = true)
    ProductButtonSelector product;

    @Inject
    @UiField(provided = true)
    EmployeeButtonSelector manager;

    @UiField
    Button saveButton;
    @UiField
    Button cancelButton;
    @Inject
    @UiField
    Lang lang;
    @UiField
    HTMLPanel commentsContainer;

    @Inject
    @UiField
    FileUploader uploader;


    private AbstractIssueEditActivity activity;

    interface IssueEditViewUiBinder extends UiBinder<HTMLPanel, IssueEditView> {}
    private static IssueEditViewUiBinder ourUiBinder = GWT.create(IssueEditViewUiBinder.class);
}