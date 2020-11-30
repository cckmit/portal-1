package ru.protei.portal.app.portal.client.view.profile.general;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.app.portal.client.activity.profile.AbstractProfilePageActivity;
import ru.protei.portal.app.portal.client.activity.profile.general.AbstractProfileGeneralActivity;
import ru.protei.portal.app.portal.client.activity.profile.general.AbstractProfileGeneralView;
import ru.protei.portal.test.client.DebugIds;

public class ProfileGeneralView extends Composite implements AbstractProfileGeneralView {

    public ProfileGeneralView() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
    }

    @Override
    public void setActivity(AbstractProfileGeneralActivity activity) {
        this.activity = activity;
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

    private AbstractProfileGeneralActivity activity;

    private static ProfileGeneralViewUiBinder ourUiBinder = GWT.create(ProfileGeneralViewUiBinder.class);
    interface ProfileGeneralViewUiBinder extends UiBinder<HTMLPanel, ProfileGeneralView> {}
}