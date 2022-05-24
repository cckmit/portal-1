package ru.protei.portal.ui.account.client.activity.edit;

import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.UserLogin;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.NameStatus;
import ru.protei.portal.ui.common.client.events.AccountEvents;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.ErrorPageEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.En_PrivilegeEntityLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.AccountControllerAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.Collections;
import java.util.Set;
import java.util.function.Consumer;

import static ru.protei.portal.core.model.helper.CollectionUtils.setOf;

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
        if (!hasPrivileges(event.id)) {
            fireEvent(new ErrorPageEvents.ShowForbidden());
            return;
        }

        initDetails.parent.clear();
        Window.scrollTo(0, 0);
        initDetails.parent.add(view.asWidget());

        if( event.id == null ) {
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

        if( !passwordValidate() ) {
            fireEvent( new NotifyEvents.Show( lang.accountPasswordNotDefinied(), NotifyEvents.NotifyType.ERROR ) );
            return;
        }

        if( !confirmValidate() ) {
            fireEvent( new NotifyEvents.Show( lang.accountPasswordsNotMatch(), NotifyEvents.NotifyType.ERROR ) );
            return;
        }

        if ( !roleValidate() ) {
            fireEvent( new NotifyEvents.Show( lang.accountRoleNotDefinied(), NotifyEvents.NotifyType.ERROR ) );
            return;
        }

        Boolean sendWelcomeEmail = view.sendWelcomeEmail().getValue();

        accountService.saveAccount( applyChangesLogin(), sendWelcomeEmail, new RequestCallback< UserLogin >() {
            @Override
            public void onError( Throwable throwable ) {}

            @Override
            public void onSuccess( UserLogin userLogin ) {
                fireEvent(new AccountEvents.Show(!isNew(account)));
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
        fireEvent(new AccountEvents.Show(!isNew(account)));
    }

    private boolean isNew(UserLogin userLogin) {
        return userLogin.getId() == null;
    }

    private void requestAccount( Long id, Consumer< UserLogin > successAction ) {
        accountService.getAccount(  id, new RequestCallback< UserLogin >() {
            @Override
            public void onError( Throwable throwable ) {}

            @Override
            public void onSuccess( UserLogin userLogin ) {
                successAction.accept( userLogin );
            }
        } );
    }

    @Override
    public void onSearchChanged() {
        final String searchPattern = view.searchPattern().getValue().trim();
        view.setRolesFilter(StringUtils.isEmpty(searchPattern) ? null : makerRolesFilter(searchPattern));
    }

    private Selector.SelectorFilter<UserRole> makerRolesFilter(String searchPattern) {
        String searchString = searchPattern.toUpperCase();
        return userRole -> userRole != null &&
                (userRole.getCode().toUpperCase().contains(searchString)
              || userRole.getInfo().toUpperCase().contains(searchString)
              || userRolePrivilegesContains(userRole.getPrivileges(), searchString)
        );
    }

    private boolean userRolePrivilegesContains(Set<En_Privilege> privileges, String searchString) {
        return privileges.stream().anyMatch(
               privilege -> entityLang.getName(privilege.getEntity()).toUpperCase().contains(searchString)
        );
    }

    private void resetValidationStatus(){
        view.setLoginStatus( NameStatus.NONE );
    }

    private UserLogin applyChangesLogin() {
        account.setUlogin( view.login().getValue() );
        account.setPersonId( view.person().getValue().getId() );
        account.setInfo( view.person().getValue().getName() );
        if ( !HelperFunc.isEmpty( view.password().getText() ) ) {
            account.setUpass( view.password().getText() );
        }
        account.setRoles( view.roles().getValue() );
        return account;
    }

    private void initialView( UserLogin userLogin ) {
        this.account = userLogin;
        fillView( account );
        resetValidationStatus();
    }

    private void fillView( UserLogin userLogin ) {
        view.login().setValue( userLogin.getUlogin() );
        if (userLogin.getId() == null){
            view.company().setValue(null);
            view.person().setValue(null);
        }
        else {
            view.company().setValue( new EntityOption(userLogin.getCompanyName(), userLogin.getCompanyId()) );
            view.person().setValue( new PersonShortView(userLogin.getDisplayName(), userLogin.getPersonId()), userLogin.isFired() );
        }
        view.setCompaniesForInitiator(userLogin.getCompanyId() == null ? Collections.emptySet() : setOf(userLogin.getCompanyId()));
        view.password().setText( "" );
        view.confirmPassword().setText( "" );
        view.roles().setValue( userLogin.getRoles() );
        view.enabledFields( userLogin.getId() == null );
        view.enabledPassword( !userLogin.isLDAP_Auth() );
        view.showInfo( userLogin.getId() != null && !userLogin.isLDAP_Auth() );
        view.sendWelcomeEmailVisibility().setVisible(userLogin.getId() == null);
        view.sendWelcomeEmail().setValue(false);
        view.searchPattern().setValue(null);
    }

    private boolean validate() {
        return view.loginValidator().isValid()
                && view.personValidator().isValid();
    }

    private boolean passwordValidate() {
        return account.getId() != null ||
                HelperFunc.isNotEmpty( view.login().getValue() ) &&
                HelperFunc.isNotEmpty( view.password().getText() );
    }

    private boolean confirmValidate() {
        return HelperFunc.isEmpty( view.login().getValue() ) ||
                HelperFunc.isEmpty( view.password().getText() ) ||
                ( !HelperFunc.isEmpty( view.confirmPassword().getText() ) &&
                        view.password().getText().equals( view.confirmPassword().getText() ) );
    }

    private boolean roleValidate() {
        return view.roles().getValue() != null &&
                !view.roles().getValue().isEmpty();
    }

    private boolean hasPrivileges(Long accountId) {
        if (accountId == null && policyService.hasPrivilegeFor(En_Privilege.ACCOUNT_CREATE)) {
            return true;
        }

        if (accountId != null && policyService.hasPrivilegeFor(En_Privilege.ACCOUNT_EDIT)) {
            return true;
        }

        return false;
    }

    @Inject
    AbstractAccountEditView view;

    @Inject
    Lang lang;

    @Inject
    En_PrivilegeEntityLang entityLang;

    @Inject
    AccountControllerAsync accountService;

    @Inject
    PolicyService policyService;

    private UserLogin account;
    private AppEvents.InitDetails initDetails;
}
