package ru.protei.portal.ui.equipment.client.activity.preview;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Equipment;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.common.DecimalNumberFormatter;
import ru.protei.portal.ui.common.client.events.ConfirmDialogEvents;
import ru.protei.portal.ui.common.client.events.EquipmentEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.En_EquipmentStageLang;
import ru.protei.portal.ui.common.client.lang.En_EquipmentTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.EquipmentControllerAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.stream.Collectors;

/**
 * Активность превью контакта
 */
public abstract class EquipmentPreviewActivity implements Activity, AbstractEquipmentPreviewActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity( this );
    }

    @Event
    public void onShow( EquipmentEvents.ShowPreview event ) {
        event.parent.clear();
        event.parent.add( view.asWidget() );

        Long equipmentId = event.equipment.getId();
        if( equipmentId == null ) {
            fillView( equipmentId );
            return;
        }

        fillView( event.equipment );
    }

    @Event
    public void onConfirmRemove( ConfirmDialogEvents.Confirm event ) {
        if ( !event.identity.equals( getClass().getName() ) ) {
            return;
        }

        equipmentService.removeEquipment( equipment.getId(), new RequestCallback<Boolean>() {
            @Override
            public void onError( Throwable throwable ) {}

            @Override
            public void onSuccess( Boolean aBoolean ) {
                fireEvent( new EquipmentEvents.Show() );
                fireEvent( new NotifyEvents.Show( lang.equipmentRemoveSuccessed(), NotifyEvents.NotifyType.SUCCESS ) );
            }
        } );
    }
    @Override
    public void onCopyClicked() {
        fireEvent( new EquipmentEvents.ShowCopyDialog( equipment ) );
    }

    @Override
    public void onRemoveClicked() {
        fireEvent( new ConfirmDialogEvents.Show( getClass().getName(), lang.equipmentRemoveConfirmMessage() ) );
    }

    private void fillView( Equipment value ) {
        this.equipment = value;

        view.setHeader( lang.equipmentDescription() + " #" + value.getId() );
        view.setName( value.getName() );
        view.setCreatedDate(value.getCreated() == null ? "" : DateFormatter.formatDateTime(value.getCreated()));
        view.setNameBySldWrks( value.getNameSldWrks() );
        view.setComment( value.getComment() );
        view.setType( typeLang.getName( value.getType() ) );
        view.setStage( stageLang.getName( value.getStage() ), value.getStage().name().toLowerCase() );
        view.setProject( value.getProjectName() );
        view.setManager( value.getManagerShortName() == null ? "" : value.getManagerShortName() );
        view.setCopyBtnEnabledStyle( policyService.hasPrivilegeFor( En_Privilege.EQUIPMENT_CREATE ) );
        view.setRemoveBtnEnabledStyle( policyService.hasPrivilegeFor( En_Privilege.EQUIPMENT_REMOVE ) );

        if( value.getDecimalNumbers() != null ) {
            view.setDecimalNumbers( value.getDecimalNumbers().stream().map( DecimalNumberFormatter::formatNumber ).collect( Collectors.joining(", ")) );
        }

        if ( value.getLinkedEquipmentDecimalNumbers() != null && !value.getLinkedEquipmentDecimalNumbers().isEmpty() ) {
            view.setLinkedEquipment( value.getLinkedEquipmentDecimalNumbers().stream().map( DecimalNumberFormatter::formatNumber ).collect( Collectors.joining(", ")) );
        } else {
            view.setLinkedEquipment( lang.equipmentPrimaryUseNotDefinied() );
        }
    }

    private void fillView( Long id ) {
        if (id == null) {
            fireEvent( new NotifyEvents.Show( lang.errIncorrectParams(), NotifyEvents.NotifyType.ERROR ) );
            return;
        }

        equipmentService.getEquipment( id, new RequestCallback<Equipment>() {
            @Override
            public void onError ( Throwable throwable ) {
                fireEvent( new NotifyEvents.Show( lang.errNotFound(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess ( Equipment value ) {
                fillView( value );
            }
        } );
    }

    @Inject
    Lang lang;
    @Inject
    AbstractEquipmentPreviewView view;
    @Inject
    En_EquipmentTypeLang typeLang;
    @Inject
    En_EquipmentStageLang stageLang;
    @Inject
    PolicyService policyService;
    @Inject
    EquipmentControllerAsync equipmentService;

    private Equipment equipment;
}
