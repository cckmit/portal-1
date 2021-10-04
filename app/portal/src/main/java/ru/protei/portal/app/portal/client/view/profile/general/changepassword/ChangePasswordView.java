package ru.protei.portal.app.portal.client.view.profile.general.changepassword;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.app.portal.client.activity.profile.general.changepassword.AbstractChangePasswordView;
import ru.protei.portal.test.client.DebugIds;

public class ChangePasswordView extends Composite implements AbstractChangePasswordView {
    public ChangePasswordView() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
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

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        currentPasswordLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.PROFILE.LABEL.CURRENT_PASSWORD);
        currentPassword.ensureDebugId(DebugIds.PROFILE.CURRENT_PASSWORD_INPUT);
        newPasswordLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.PROFILE.LABEL.NEW_PASSWORD);
        newPassword.ensureDebugId(DebugIds.PROFILE.NEW_PASSWORD_INPUT);
        confirmPasswordLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.PROFILE.LABEL.CONFIRM_PASSWORD);
        confirmPassword.ensureDebugId(DebugIds.PROFILE.CONFIRM_PASSWORD_INPUT);
    }

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

    interface ChangePasswordViewUiBinder extends UiBinder<HTMLPanel, ChangePasswordView> {}
    private static ChangePasswordViewUiBinder ourUiBinder = GWT.create(ChangePasswordViewUiBinder.class);
}