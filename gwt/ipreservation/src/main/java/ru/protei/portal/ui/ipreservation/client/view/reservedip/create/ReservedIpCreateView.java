package ru.protei.portal.ui.ipreservation.client.view.reservedip.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DateIntervalType;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.SubnetOption;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.common.NameStatus;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeButtonSelector;
import ru.protei.portal.ui.common.client.widget.typedrangepicker.DateIntervalWithType;
import ru.protei.portal.ui.common.client.widget.typedrangepicker.TypedToggleRangePicker;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.ipreservation.client.activity.reservedip.create.AbstractReservedIpCreateActivity;
import ru.protei.portal.ui.ipreservation.client.activity.reservedip.create.AbstractReservedIpCreateView;
import ru.protei.portal.ui.ipreservation.client.view.widget.mode.En_ReservedMode;
import ru.protei.portal.ui.ipreservation.client.view.widget.mode.ReservedModeBtnGroup;
import ru.protei.portal.ui.ipreservation.client.view.widget.selector.SubnetMultiSelector;

import java.util.Arrays;
import java.util.Set;

import static ru.protei.portal.core.model.helper.CollectionUtils.stream;

/**
 * Вид редактирования зарезервированного IP
 */
public class ReservedIpCreateView extends Composite implements AbstractReservedIpCreateView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ipAddress.setRegexp( CrmConstants.IpReservation.IP_ADDRESS );
        macAddress.setRegexp( CrmConstants.IpReservation.MAC_ADDRESS );
        number.setRegexp( CrmConstants.IpReservation.NUMBER );
        fillUseRangeButtons();
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
    public HasValidable ipAddressValidator() { return ipAddress; }

    @Override
    public void setIpAddressStatus(NameStatus status) {
        verifiableIcon.setClassName(status.getStyle());
    }

    @Override
    public HasValue<String> macAddress() { return macAddress; }

    @Override
    public HasValidable macAddressValidator() { return macAddress; }

    @Override
    public HasValue<String> number() { return number; }

    @Override
    public HasValidable numberValidator() { return number; }

    @Override
    public HasText comment() { return comment; }

    @Override
    public HasValue<PersonShortView> owner() { return ipOwner; }

    @Override
    public HasValidable ownerValidator() { return ipOwner; }

    @Override
    public HasValue<Set<SubnetOption>> subnets() { return subnets; }

    @Override
    public HasValue<DateIntervalWithType> useRange() { return useRange; }

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
    public HasVisibility reserveModeVisibility() { return reservedMode; }

    @Override
    public HasEnabled ownerEnabled() { return ipOwner; }

    @Override
    public HasEnabled saveEnabled() { return saveButton; }

    @Override
    public void setFreeIpCountLabel(int count ) {
        this.freeIpCountLabel.setInnerText(String.valueOf(count));
    }

    @Override
    public void setEnableUnlimited(boolean value) { useRange.setEnableBtn(En_DateIntervalType.UNLIMITED, value); }

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

    @UiHandler("ipAddress")
    public void onChangeIpAddress( KeyUpEvent event ) {
        verifiableIcon.setClassName(NameStatus.UNDEFINED.getStyle());
        ipTimer.cancel();
        ipTimer.schedule( 200 );
    }

    @UiHandler("number")
    public void onChangeNumber( KeyUpEvent event ) {
        numberTimer.cancel();
        numberTimer.schedule( 200 );
    }

    @UiHandler("subnets")
    public void onSubnetSelected(ValueChangeEvent<Set<SubnetOption>> event)  {
        if ( activity != null ) {
            activity.checkCreateAvailable();
        }
    }

    @UiHandler("ipOwner")
    public void onOwnerSelected(ValueChangeEvent<PersonShortView> event)  {
        if ( activity != null ) {
            activity.onOwnerChanged();
        }
    }

    private void fillUseRangeButtons() {
        En_DateIntervalType.reservedIpTypes().forEach(type -> useRange.addBtn(type,"btn btn-default col-md-4"));
        useRange.getValue().setIntervalType(En_DateIntervalType.MONTH);
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        reservedMode.ensureDebugId(DebugIds.RESERVED_IP.MODE_TOGGLE);
        ipAddress.ensureDebugId(DebugIds.RESERVED_IP.IP_ADDRESS_INPUT);
        macAddress.ensureDebugId(DebugIds.RESERVED_IP.MAC_ADDRESS_INPUT);
        number.ensureDebugId(DebugIds.RESERVED_IP.NUMBER_INPUT);
        freeIpCountLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.RESERVED_IP.FREE_IP_COUNT_LABEL);
        comment.ensureDebugId(DebugIds.RESERVED_IP.COMMENT_INPUT);
        ipOwner.ensureDebugId(DebugIds.RESERVED_IP.OWNER_SELECTOR);
        /*
           @todo dates
         */
        saveButton.ensureDebugId(DebugIds.PROJECT.SAVE_BUTTON);
        cancelButton.ensureDebugId(DebugIds.PROJECT.CANCEL_BUTTON);
    }

    Timer ipTimer = new Timer() {
        @Override
        public void run() {
            if ( activity != null ) {
                activity.onChangeIpAddress();
            }
        }
    };

    Timer numberTimer = new Timer() {
        @Override
        public void run() {
            if ( activity != null ) {
                activity.checkCreateAvailable();
            }
        }
    };

    @Inject
    @UiField(provided = true)
    ReservedModeBtnGroup reservedMode;
    @UiField
    ValidableTextBox ipAddress;
    @UiField
    Element verifiableIcon;
    @UiField
    ValidableTextBox number;
    @UiField
    Element freeIpCountLabel;
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

    @Inject
    @UiField(provided = true)
    TypedToggleRangePicker useRange;

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