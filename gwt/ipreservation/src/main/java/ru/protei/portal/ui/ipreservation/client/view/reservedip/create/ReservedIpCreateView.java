package ru.protei.portal.ui.ipreservation.client.view.reservedip.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.SubnetOption;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeButtonSelector;
import ru.protei.portal.ui.common.client.widget.typedrangepicker.DateIntervalWithType;
import ru.protei.portal.ui.common.client.widget.typedrangepicker.TypedRangePicker;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.ipreservation.client.activity.reservedip.create.AbstractReservedIpCreateActivity;
import ru.protei.portal.ui.ipreservation.client.activity.reservedip.create.AbstractReservedIpCreateView;
import ru.protei.portal.ui.ipreservation.client.view.widget.mode.En_ReservedMode;
import ru.protei.portal.ui.ipreservation.client.view.widget.mode.ReservedModeBtnGroup;
import ru.protei.portal.ui.ipreservation.client.view.widget.selector.SubnetMultiSelector;

import java.util.Set;

/**
 * Вид редактирования зарезервированного IP
 */
public class ReservedIpCreateView extends Composite implements AbstractReservedIpCreateView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
    }

    @Override
    public void setActivity(AbstractReservedIpCreateActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasValue<En_ReservedMode> reservedMode() { return reservedMode; }

    @Override
    public HasValue<String> ipAddress() { return ipAddress; }

    @Override
    public HasValue<Long> number() { return number; }

    @Override
    public HasValue<String> macAddress() { return macAddress; }

    @Override
    public HasText comment() { return comment; }

    @Override
    public HasValue<PersonShortView> owner() { return ipOwner; }

    @Override
    public HasValue<Set<SubnetOption>> subnets() { return subnets; }

    @Override
    public HasValue<DateIntervalWithType> useRange() { return null; }

    @Override
    public HasValidable ipAddressValidator() { return ipAddress; }

    @Override
    public HasValidable macAddressValidator() { return macAddress; }

    @Override
    public HasWidgets getExaсtIpContainer() { return exactIpContainer; }

    @Override
    public HasWidgets getAnyFreeIpsContainer() { return anyFreeIpsContainer; }

    @Override
    public HasVisibility saveVisibility() { return saveButton; }

    @Override
    public HasVisibility exaсtIpVisibility() { return exactIpContainer; }

    @Override
    public HasVisibility anyFreeIpsVisibility() { return anyFreeIpsContainer; }

    @Override
    public HasVisibility subnetsVisibility() {
        return null;
    }

    @Override
    public HasEnabled ownerEnabled() { return ipOwner; }

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

    @UiHandler( "reservedMode" )
    public void onReservedModeChanged( ValueChangeEvent<En_ReservedMode> event ) {
        if (activity != null) {
            activity.onReservedModeChanged();
        }
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
//        reservedMode.ensureDebugId(DebugIds.RESERVED_IP.MODE_SWITCHER);
        ipAddress.ensureDebugId(DebugIds.RESERVED_IP.IP_ADDRESS_INPUT);
        macAddress.ensureDebugId(DebugIds.RESERVED_IP.MAC_ADDRESS_INPUT);
        number.ensureDebugId(DebugIds.RESERVED_IP.NUMBER_INPUT);
        comment.ensureDebugId(DebugIds.RESERVED_IP.COMMENT_INPUT);
        ipOwner.setEnsureDebugId(DebugIds.RESERVED_IP.OWNER_SELECTOR);
        /*
           @todo dates
         */
        saveButton.ensureDebugId(DebugIds.PROJECT.SAVE_BUTTON);
        cancelButton.ensureDebugId(DebugIds.PROJECT.CANCEL_BUTTON);
    }

    @Inject
    @UiField(provided = true)
    ReservedModeBtnGroup reservedMode;
    @UiField
    ValidableTextBox ipAddress;
    @UiField
    LongBox number;
    @UiField
    ValidableTextBox macAddress;
    @UiField
    TextArea comment;

    @Inject
    @UiField(provided = true)
    EmployeeButtonSelector ipOwner;

    @Inject
    @UiField(provided = true)
    SubnetMultiSelector subnets;

/*    @Inject
    @UiField(provided = true)
    TypedRangePicker useRange;*/

    @UiField
    HTMLPanel exactIpContainer;
    @UiField
    HTMLPanel anyFreeIpsContainer;

    @UiField
    Button saveButton;
    @UiField
    Button cancelButton;
    @Inject
    @UiField
    Lang lang;

    private AbstractReservedIpCreateActivity activity;

    private static ReservedIpCreateViewUiBinder ourUiBinder = GWT.create(ReservedIpCreateViewUiBinder.class);
    interface ReservedIpCreateViewUiBinder extends UiBinder<HTMLPanel, ReservedIpCreateView> {}
}