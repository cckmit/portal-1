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
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanySelector;
import ru.protei.portal.ui.common.client.widget.selector.dict.ImportanceButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.PersonButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.product.ProductButtonSelector;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.issue.client.activity.edit.AbstractIssueEditActivity;
import ru.protei.portal.ui.issue.client.activity.edit.AbstractIssueEditView;
import ru.protei.portal.ui.issue.client.widget.buttonselector.IssueStatesButtonSelector;

/**
 * Вид создания и редактирования обращения
 */
public class IssueEditView extends Composite implements AbstractIssueEditView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractIssueEditActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasText name() {
        return null;
    }

    @Override
    public HasValidable nameValidator() {
        return null;
    }

    @Override
    public HasText description() {
        return null;
    }

    @Override
    public HasValue<En_CaseState> state() {
        return null;
    }

    @Override
    public HasValue<En_ImportanceLevel> importance() {
        return null;
    }

    @Override
    public HasValue<Company> company() {
        return null;
    }

    @Override
    public HasValue<Person> initiator() {
        return null;
    }

    @Override
    public HasValue<Person> manager() {
        return null;
    }

    @Override
    public HasValue<DevUnit> product() {
        return null;
    }

    @Override
    public HasValue<Boolean> isLocal() {
        return null;
    }

    @UiHandler("company")
    public void onChangeCompany(ValueChangeEvent<EntityOption> event){
        if(event.getValue() == null){
            initiator.setDisabled(true);
            initiator.updateCompany(null);
        }else {
            initiator.setDisabled(false);
            Company company = new Company(event.getValue().getId());
            initiator.updateCompany(company);
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

    @UiField
    ValidableTextBox name;

    @UiField
    CheckBox local;

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
    PersonButtonSelector initiator;

    @Inject
    @UiField(provided = true)
    ProductButtonSelector product;

    @Inject
    @UiField(provided = true)
    EmployeeButtonSelector employee;

    @UiField
    Button saveButton;
    @UiField
    Button cancelButton;
    @Inject
    @UiField
    Lang lang;


    private AbstractIssueEditActivity activity;

    interface IssueEditViewUiBinder extends UiBinder<HTMLPanel, IssueEditView> {}
    private static IssueEditViewUiBinder ourUiBinder = GWT.create(IssueEditViewUiBinder.class);
}