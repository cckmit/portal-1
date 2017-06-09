package ru.protei.portal.ui.account.client.activity.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.UserLogin;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.common.NameStatus;
import ru.protei.portal.ui.common.client.events.AccountEvents;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.AccountServiceAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.function.Consumer;

/**
 * Активность создания и редактирования учетной записи
 */
public abstract class AccountEditActivity implements AbstractAccountEditActivity, Activity {
    @PostConstruct
    public void onInit() {
        view.setActivity( this );
    }

    @Event
    public void onInitDetails( AppEvents.InitDetails initDetails ) {
        this.initDetails = initDetails;
    }

    @Event
    public void onShow( AccountEvents.Edit event ) {

        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());

        if( event.id == null ) {
            this.fireEvent( new AppEvents.InitPanelName( lang.accountNew() ) );
            initialView( new UserLogin() );
        } else {
            requestAccount( event.id, this::initialView );
        }
    }

    @Override
    public void onSaveClicked() {
        if ( !validate() ) {
            return;
        }

        if( !confirmValidate() ) {
            fireEvent( new NotifyEvents.Show( lang.accountPasswordsNotMatch(), NotifyEvents.NotifyType.ERROR ) );
            return;
        }

        UserLogin userLogin = applyChangesLogin();
        if( HelperFunc.isNotEmpty( userLogin.getUlogin() ) && HelperFunc.isEmpty( userLogin.getUpass() ) && userLogin.getId() == null ) {
            fireEvent( new NotifyEvents.Show( lang.contactPasswordNotDefinied(), NotifyEvents.NotifyType.ERROR ) );
            return;
        }

        accountService.saveAccount( userLogin, new RequestCallback< UserLogin >() {
            @Override
            public void onError( Throwable throwable ) {}

            @Override
            public void onSuccess( UserLogin userLogin ) {
                fireEvent( new Back() );
            }
        } );
    }

    @Override
    public void onChangeLogin() {
        String value = view.login().getValue().trim();

        if ( value.isEmpty() ){
            view.setLoginStatus( NameStatus.NONE );
            return;
        }

        accountService.isLoginUnique(
                value,
                account.getId(),
                new RequestCallback< Boolean >() {
                    @Override
                    public void onError( Throwable throwable ) {}

                    @Override
                    public void onSuccess( Boolean isUnique ) {
                        view.setLoginStatus( isUnique ? NameStatus.SUCCESS : NameStatus.ERROR );
                    }
                }
        );
    }

    @Override
    public void onCancelClicked() {
        fireEvent( new Back() );
    }

    private void requestAccount( Long id, Consumer< UserLogin > successAction ) {
        accountService.getAccount(  id, new RequestCallback< UserLogin >() {
            @Override
            public void onError( Throwable throwable ) {}

            @Override
            public void onSuccess( UserLogin userLogin ) {
                fireEvent( new AppEvents.InitPanelName( lang.editContactHeader( userLogin.getUlogin() ) ) );
                successAction.accept( userLogin );
            }
        } );
    }

    private void resetValidationStatus(){
        view.setLoginStatus( NameStatus.NONE );
    }

    private UserLogin applyChangesLogin() {
        account.setUlogin( view.login().getValue() );
        account.setPersonId( view.person().getValue().getId() );
        account.setInfo( view.person().getValue().getDisplayShortName() );
        if ( !HelperFunc.isEmpty( view.password().getText() ) ) {
            account.setUpass( view.password().getText() );
        }
        account.setRoles( view.roles().getValue() );
        return account;
    }

    private boolean validate() {
        return view.loginValidator().isValid()
                && view.personValidator().isValid();
    }

    private void initialView( UserLogin userLogin ) {
        this.account = userLogin;
        fillView( account );
        resetValidationStatus();
    }

    private void fillView( UserLogin userLogin ) {
        view.login().setValue( userLogin.getUlogin() );
        view.company().setValue( userLogin.getPerson() == null ? null : EntityOption.fromCompany( userLogin.getPerson().getCompany() ) );
        view.changeCompany( userLogin.getPerson() == null ? null : userLogin.getPerson().getCompany() );
        view.person().setValue( PersonShortView.fromPerson( userLogin.getPerson() ) );
        view.password().setText( "" );
        view.confirmPassword().setText( "" );
        view.roles().setValue( userLogin.getRoles() );
        view.enabledFields( userLogin.getId() == null );
        view.enabledPassword( !userLogin.isLDAP_Auth() );
        view.showInfo( userLogin.getId() != null && !userLogin.isLDAP_Auth() );
    }

    private boolean confirmValidate() {
        return HelperFunc.isEmpty( view.login().getValue() ) ||
                HelperFunc.isEmpty( view.password().getText() ) ||
                ( !HelperFunc.isEmpty( view.confirmPassword().getText() ) &&
                        view.password().getText().equals( view.confirmPassword().getText() ) );
    }

    @Inject
    AbstractAccountEditView view;

    @Inject
    Lang lang;

    @Inject
    AccountServiceAsync accountService;

    private UserLogin account;
    private AppEvents.InitDetails initDetails;
}
