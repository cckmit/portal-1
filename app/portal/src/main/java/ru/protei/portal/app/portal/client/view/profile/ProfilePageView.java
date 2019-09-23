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
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.subscription.list.SubscriptionList;
import ru.protei.portal.ui.common.client.widget.subscription.model.Subscription;

import java.util.List;

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
        changePasswordLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.PROFILE.CHANGE_PASSWORD_LABEL);
        currentPasswordLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.PROFILE.CURRENT_PASSWORD_LABEL);
        currentPassword.ensureDebugId(DebugIds.PROFILE.CURRENT_PASSWORD);
        newPasswordLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.PROFILE.NEW_PASSWORD_LABEL);
        newPassword.ensureDebugId(DebugIds.PROFILE.NEW_PASSWORD);
        confirmPasswordLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.PROFILE.CONFIRM_PASSWORD_LABEL);
        confirmPassword.ensureDebugId(DebugIds.PROFILE.CONFIRM_PASSWORD);
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

    AbstractProfilePageActivity activity;

    interface ContactPreviewViewUiBinder extends UiBinder<HTMLPanel, ProfilePageView > { }
    private static ContactPreviewViewUiBinder ourUiBinder = GWT.create(ContactPreviewViewUiBinder.class);
}