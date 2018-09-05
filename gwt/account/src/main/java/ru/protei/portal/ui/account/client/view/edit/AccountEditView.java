package ru.protei.portal.ui.account.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.account.client.activity.edit.AbstractAccountEditActivity;
import ru.protei.portal.ui.account.client.activity.edit.AbstractAccountEditView;
import ru.protei.portal.ui.account.client.widget.role.RoleOptionList;
import ru.protei.portal.ui.common.client.common.NameStatus;
import ru.protei.portal.ui.common.client.widget.optionlist.item.OptionItem;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanySelector;
import ru.protei.portal.ui.common.client.widget.selector.person.InitiatorModel;
import ru.protei.portal.ui.common.client.widget.selector.person.PersonButtonSelector;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;

import java.util.Set;

/**
 * Представление создания и редактирования учетной записи
 */
public class AccountEditView extends Composite implements AbstractAccountEditView {

    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    @Override
    public void setActivity( AbstractAccountEditActivity activity ) {
        this.activity = activity;
    }

    @Override
    public HasValue< String > login() {
        return login;
    }

    @Override
    public HasValue< EntityOption > company() {
        return company;
    }

    @Override
    public HasValue< PersonShortView > person() {
        return person;
    }

    @Override
    public HasText password() {
        return password;
    }

    @Override
    public HasText confirmPassword() {
        return confirmPassword;
    }

    @Override
    public HasValue< Set< UserRole > > roles() {
        return roles;
    }

    @Override
    public HasValue<Boolean> sendWelcomeEmail() {
        return sendWelcomeEmail;
    }

    @Override
    public HasValidable loginValidator(){
        return login;
    }

    @Override
    public HasValidable personValidator() { return person; }

    @Override
    public HasVisibility sendWelcomeEmailVisibility() {
        return sendWelcomeEmail;
    }

    @Override
    public void setLoginStatus( NameStatus status ) {
        verifiableIcon.setClassName( status.getStyle() );
    }

    @Override
    public void showInfo( boolean isShow ) {
        infoPanel.setVisible( isShow );
    }

    @Override
    public void enabledFields( boolean isEnabled ) {
        company.setEnabled( isEnabled );
        person.setEnabled( isEnabled );
        login.setEnabled( isEnabled );
    }

    @Override
    public void enabledPassword( boolean isEnabled ) {
        password.setEnabled( isEnabled );
        confirmPassword.setEnabled( isEnabled );
    }

    @Override
    public void changeCompanies(Set<Long> companyIds) {
        person.updateCompanies(companyIds);
    }

    @UiHandler( "company" )
    public void onChangeCompany( ValueChangeEvent< EntityOption > event ) {
        Company company = Company.fromEntityOption( event.getValue() );

        person.setEnabled( company != null );
        changeCompanies(InitiatorModel.makeCompanyIds(company));
        person.setValue( null );
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

    @UiHandler("login")
    public void onChangeLogin( KeyUpEvent keyUpEvent ) {
        verifiableIcon.setClassName( NameStatus.UNDEFINED.getStyle());
        timer.cancel();
        timer.schedule( 300 );
    }

    @Inject
    @UiField( provided = true )
    CompanySelector company;

    @Inject
    @UiField( provided = true )
    PersonButtonSelector person;

    @UiField
    ValidableTextBox login;

    @UiField
    Element verifiableIcon;

    @UiField
    PasswordTextBox password;

    @UiField
    PasswordTextBox confirmPassword;

    @UiField
    HTMLPanel infoPanel;

    @Inject
    @UiField( provided = true )
    RoleOptionList roles;

    @UiField
    Button saveButton;

    @UiField
    Button cancelButton;

    @UiField
    OptionItem sendWelcomeEmail;

    Timer timer = new Timer() {
        @Override
        public void run() {
            if ( activity != null ) {
                activity.onChangeLogin();
            }
        }
    };

    AbstractAccountEditActivity activity;

    private static AccountEditViewUiBinder ourUiBinder = GWT.create( AccountEditViewUiBinder.class );
    interface AccountEditViewUiBinder extends UiBinder< HTMLPanel, AccountEditView > {}
}