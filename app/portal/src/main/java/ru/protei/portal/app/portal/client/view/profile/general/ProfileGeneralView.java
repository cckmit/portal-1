package ru.protei.portal.app.portal.client.view.profile.general;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
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
    public void setLogin(String login) {
        this.login.setInnerText(login);
    }
    @Override
    public HasVisibility changePasswordButtonVisibility() {
        return changePasswordButton;
    }

    @Override
    public HasVisibility newEmployeeBookContainerVisibility() {
        return newEmployeeBookContainer;
    }

    @UiHandler("changePasswordButton")
    public void onChangePasswordButtonClicked(ClickEvent event) {
        if (activity != null) {
            activity.onChangePasswordButtonClicked();
        }
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        changePasswordButton.ensureDebugId(DebugIds.PROFILE.CHANGE_PASSWORD_BUTTON);
    }

    @UiField
    Button changePasswordButton;
    @UiField
    HTMLPanel newEmployeeBookContainer;
    @UiField
    SpanElement login;

    private AbstractProfileGeneralActivity activity;

    private static ProfileGeneralViewUiBinder ourUiBinder = GWT.create(ProfileGeneralViewUiBinder.class);
    interface ProfileGeneralViewUiBinder extends UiBinder<HTMLPanel, ProfileGeneralView> {}
}