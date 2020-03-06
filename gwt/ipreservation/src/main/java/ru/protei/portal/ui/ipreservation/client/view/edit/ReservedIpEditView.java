package ru.protei.portal.ui.ipreservation.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.Subnet;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeButtonSelector;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.ipreservation.client.activity.edit.AbstractReservedIpEditActivity;
import ru.protei.portal.ui.ipreservation.client.activity.edit.AbstractReservedIpEditView;

/**
 * Вид редактирования зарезервированного IP
 */
public class ReservedIpEditView extends Composite implements AbstractReservedIpEditView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
    }

    @Override
    public void setActivity(AbstractReservedIpEditActivity activity) {
        this.activity = activity;
    }

    @Override
    public void setAddress(String address) { this.address.setText(String.valueOf(address)); }

    @Override
    public HasValue<String> macAddress() { return macAddress; }

    @Override
    public HasText comment() { return comment; }

    @Override
    public HasValue<PersonShortView> owner() { return owner; }

    @Override
    public HasValue<Subnet> subnet() { return null; }

    @Override
    public HasValidable macAddressValidator() {
        return macAddress;
    }

/*    @Override
    public HasVisibility addressVisibility() { return address; }*/

    @Override
    public HasVisibility saveVisibility() {
        return saveButton;
    }

    @Override
    public HasEnabled saveEnabled() {
        return saveButton;
    }

    @UiHandler("saveButton")
    public void onSaveClicked(ClickEvent event) {
        if (activity != null) {
            activity.onSaveClicked();
        }
    }

    @UiHandler("cancelButton")
    public void onCancelClicked(ClickEvent event) {
        if (activity != null) {
            activity.onCancelClicked();
        }
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        macAddress.ensureDebugId(DebugIds.RESERVED_IP.MAC_ADDRESS_INPUT);
        comment.ensureDebugId(DebugIds.RESERVED_IP.COMMENT_INPUT);
        //owner.setEnsureDebugId(DebugIds.RESERVED_IP.OWNER_SELECTOR);
        saveButton.ensureDebugId(DebugIds.PROJECT.SAVE_BUTTON);
        cancelButton.ensureDebugId(DebugIds.PROJECT.CANCEL_BUTTON);
    }

    @UiField
    Label address;
    @UiField
    ValidableTextBox macAddress;
    @UiField
    TextArea comment;
    @Inject
    @UiField(provided = true)
    EmployeeButtonSelector owner;
/*    @Inject
    @UiField(provided = true)
    SubnetButtonSelector subnet;*/

    @UiField
    Button saveButton;
    @UiField
    Button cancelButton;
    @Inject
    @UiField
    Lang lang;

    private AbstractReservedIpEditActivity activity;

    interface ReservedIpEditViewUiBinder extends UiBinder<HTMLPanel, ReservedIpEditView> {}
    private static ReservedIpEditViewUiBinder ourUiBinder = GWT.create(ReservedIpEditViewUiBinder.class);
}