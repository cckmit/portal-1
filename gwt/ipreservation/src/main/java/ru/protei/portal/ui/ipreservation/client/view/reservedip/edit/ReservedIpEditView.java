package ru.protei.portal.ui.ipreservation.client.view.reservedip.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.client.view.input.range.RangePicker;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeButtonSelector;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.ipreservation.client.activity.reservedip.edit.AbstractReservedIpEditActivity;
import ru.protei.portal.ui.ipreservation.client.activity.reservedip.edit.AbstractReservedIpEditView;

/**
 * Вид редактирования зарезервированного IP
 */
public class ReservedIpEditView extends Composite implements AbstractReservedIpEditView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        macAddress.setRegexp( CrmConstants.IpReservation.MAC_ADDRESS );
        ipOwner.setItemRenderer( value -> value == null ? lang.selectReservedIpOwner() : value.getName() );
        ensureDebugIds();
    }

    @Override
    public void setActivity(AbstractReservedIpEditActivity activity) {
        this.activity = activity;
    }

    @Override
    public void setAddress(String address) { this.address.setInnerText(address); }

    @Override
    public HasValue<String> macAddress() { return macAddress; }

    @Override
    public HasValue<DateInterval> useRange() { return useRange; }

    @Override
    public HasText comment() { return comment; }

    @Override
    public HasText lastActiveDate() {
        return lastActiveDate;
    }

    @Override
    public HasText lastCheckInfo() {
        return lastCheckInfo;
    }

    @Override
    public HasValue<PersonShortView> owner() { return ipOwner; }

    @Override
    public HasValidable macAddressValidator() { return macAddress; }

    @Override
    public HasVisibility saveVisibility() { return saveButton; }

    @Override
    public HasEnabled saveEnabled() { return saveButton; }

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
        useRange.ensureDebugId(DebugIds.RESERVED_IP.USE_RANGE_INPUT);
        useRange.getRelative().ensureDebugId(DebugIds.RESERVED_IP.USE_RANGE_BUTTON);
        comment.ensureDebugId(DebugIds.RESERVED_IP.COMMENT_INPUT);
        lastActiveDate.ensureDebugId(DebugIds.RESERVED_IP.LAST_ACTIVE_DATE);
        lastCheckInfo.ensureDebugId(DebugIds.RESERVED_IP.LAST_CHECK_INFO);
        ipOwner.ensureDebugId(DebugIds.RESERVED_IP.OWNER_SELECTOR);
        saveButton.ensureDebugId(DebugIds.PROJECT.SAVE_BUTTON);
        cancelButton.ensureDebugId(DebugIds.PROJECT.CANCEL_BUTTON);
    }

    @UiField
    HeadingElement address;
    @UiField
    ValidableTextBox macAddress;
    @UiField
    TextArea comment;

    @UiField
    TextBox lastActiveDate;
    @UiField
    TextArea lastCheckInfo;

    @Inject
    @UiField(provided = true)
    RangePicker useRange;

    @Inject
    @UiField(provided = true)
    EmployeeButtonSelector ipOwner;

    @UiField
    Button saveButton;
    @UiField
    Button cancelButton;
    @Inject
    @UiField
    Lang lang;

    private AbstractReservedIpEditActivity activity;

    private static ReservedIpEditViewUiBinder ourUiBinder = GWT.create(ReservedIpEditViewUiBinder.class);
    interface ReservedIpEditViewUiBinder extends UiBinder<HTMLPanel, ReservedIpEditView> {}
}
