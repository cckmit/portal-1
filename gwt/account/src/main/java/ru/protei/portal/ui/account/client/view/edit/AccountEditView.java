package ru.protei.portal.ui.account.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.account.client.activity.edit.AbstractAccountEditActivity;
import ru.protei.portal.ui.account.client.activity.edit.AbstractAccountEditView;
import ru.protei.portal.ui.common.client.common.NameStatus;
import ru.protei.portal.ui.common.client.widget.selector.person.PersonButtonSelector;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;

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
    public HasText login() {
        return login;
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
    public HasValidable loginValidator(){
        return login;
    }

    @Override
    public HasValidable personValidator() { return person; }

    @Override
    public void setLoginStatus( NameStatus status ) {
        verifiableIcon.setClassName( status.getStyle() );
    }

    @Override
    public void showInfo( boolean isShow ) {
        infoPanel.setVisible( isShow );
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

    @UiField
    ValidableTextBox login;

    @UiField
    Element verifiableIcon;

    @Inject
    @UiField( provided = true )
    PersonButtonSelector person;

    @UiField
    HTMLPanel infoPanel;

    @UiField
    PasswordTextBox password;

    @UiField
    PasswordTextBox confirmPassword;

    @UiField
    Button saveButton;

    @UiField
    Button cancelButton;

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