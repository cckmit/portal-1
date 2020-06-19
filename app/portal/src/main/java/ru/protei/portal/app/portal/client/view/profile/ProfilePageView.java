package ru.protei.portal.app.portal.client.view.profile;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.app.portal.client.activity.profile.AbstractProfilePageActivity;
import ru.protei.portal.app.portal.client.activity.profile.AbstractProfilePageView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.account.client.widget.casefilter.group.PersonCaseFilterWidget;
import ru.protei.portal.ui.common.client.lang.Lang;

/**
 * Вид превью контакта
 */
public class ProfilePageView extends Composite implements AbstractProfilePageView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
    }

    @Override
    public void setActivity(AbstractProfilePageActivity activity) {
        this.activity = activity;
    }

    @Override
    public void setName( String name ) {
        this.name.setText( name );
    }

    @Override
    public void setCompany( String value ) {
        this.company.setInnerText( value );
    }

    @Override
    public HasValue<String> currentPassword() {
        return currentPassword;
    }

    @Override
    public HasValue<String> newPassword() {
        return newPassword;
    }

    @Override
    public HasValue<String> confirmPassword() {
        return confirmPassword;
    }

    @Override
    public HasVisibility passwordContainerVisibility() {
        return passwordContainer;
    }

    @Override
    public void setIcon( String iconSrc ) {
        this.icon.setSrc( iconSrc );
    }

    @Override
    public void setPersonId(Long personId) {
        personCaseFilterWidget.setPersonId(personId);
    }

    @Override
    public HasVisibility personCaseFilterContainerVisibility() {
        return personCaseFilterContainer;
    }

    @Override
    public HasVisibility changePasswordButtonVisibility() {
        return changePasswordButton;
    }

    @UiHandler("changePasswordButton")
    public void onChangePasswordButtonClicked(ClickEvent event) {
        if (activity != null) {
            activity.onChangePasswordButtonClicked();
        }
    }

    @UiHandler("savePasswordButton")
    public void onSavePasswordButtonClicked(ClickEvent event) {
        if (activity != null) {
            activity.onSavePasswordButtonClicked();
        }
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        name.ensureDebugId(DebugIds.PROFILE.NAME);
        company.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.PROFILE.COMPANY);
        changePasswordButton.ensureDebugId(DebugIds.PROFILE.CHANGE_PASSWORD_BUTTON);
        changePasswordLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.PROFILE.LABEL.CHANGE_PASSWORD);
        currentPasswordLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.PROFILE.LABEL.CURRENT_PASSWORD);
        currentPassword.ensureDebugId(DebugIds.PROFILE.CURRENT_PASSWORD_INPUT);
        newPasswordLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.PROFILE.LABEL.NEW_PASSWORD);
        newPassword.ensureDebugId(DebugIds.PROFILE.NEW_PASSWORD_INPUT);
        confirmPasswordLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.PROFILE.LABEL.CONFIRM_PASSWORD);
        confirmPassword.ensureDebugId(DebugIds.PROFILE.CONFIRM_PASSWORD_INPUT);
        savePasswordButton.ensureDebugId(DebugIds.PROFILE.SAVE_PASSWORD_BUTTON);
    }

    @UiField
    Button changePasswordButton;
    @UiField
    Button savePasswordButton;
    @Inject
    @UiField
    Lang lang;
    @UiField
    InlineLabel name;
    @UiField
    Element company;
    @UiField
    ImageElement icon;
    @UiField
    HTMLPanel passwordContainer;
    @UiField
    PasswordTextBox currentPassword;
    @UiField
    PasswordTextBox newPassword;
    @UiField
    PasswordTextBox confirmPassword;
    @UiField
    LabelElement currentPasswordLabel;
    @UiField
    LabelElement newPasswordLabel;
    @UiField
    LabelElement confirmPasswordLabel;
    @UiField
    HeadingElement changePasswordLabel;
    @UiField
    HTMLPanel personCaseFilterContainer;

    @Inject
    @UiField( provided = true )
    PersonCaseFilterWidget personCaseFilterWidget;

    AbstractProfilePageActivity activity;

    interface ContactPreviewViewUiBinder extends UiBinder<HTMLPanel, ProfilePageView > { }
    private static ContactPreviewViewUiBinder ourUiBinder = GWT.create(ContactPreviewViewUiBinder.class);
}