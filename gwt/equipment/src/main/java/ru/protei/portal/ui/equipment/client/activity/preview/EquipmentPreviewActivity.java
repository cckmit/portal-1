package ru.protei.portal.ui.equipment.client.activity.preview;

import com.google.gwt.core.client.GWT;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Equipment;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.common.DecimalNumberFormatter;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.ConfirmDialogEvents;
import ru.protei.portal.ui.common.client.events.EquipmentEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
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
    public void onInit( AppEvents.InitDetails event ) {
        this.initDetails = event;
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
    public void onShowFullScreen( EquipmentEvents.ShowFullScreen event ) {
        initDetails.parent.clear();
        initDetails.parent.add( view.asWidget() );

        fillView( event.equipmentId );
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

    @Override
    public void onFullScreenClicked() {
        if (equipment == null) {
            return;
        }
        fireEvent(new EquipmentEvents.ShowFullScreen(equipment.getId()));
    }

    private void fillView( Equipment value ) {
        this.equipment = value;

        view.setHeader( value.getName() + " (#" + value.getId() + ")");
        view.setCreatedBy(lang.createBy(value.getAuthorShortName(), DateFormatter.formatDateTime(value.getCreated())));

        view.setNameBySldWrks( value.getNameSldWrks() );
        view.setComment( value.getComment() );
        String typeImage = null;
        if ( value.getType() != null ) {
            typeImage = "./images/eq_" + value.getType().name().toLowerCase() + ".png";
        }
        view.setType( typeImage );

        view.setProject( value.getProjectName() );
        view.setManager( value.getManagerShortName() == null ? "" : value.getManagerShortName() );
        view.setCopyBtnEnabledStyle( policyService.hasPrivilegeFor( En_Privilege.EQUIPMENT_CREATE ) );
        view.setRemoveBtnEnabledStyle( policyService.hasPrivilegeFor( En_Privilege.EQUIPMENT_REMOVE ) );

        boolean isLinkedEqPresent = value.getLinkedEquipmentId() != null;
        if ( isLinkedEqPresent ) {
            view.setLinkedEquipmentExternalLink(GWT.getHostPageBaseURL() + "#equipment_preview:id=" + value.getLinkedEquipmentId() );
        }

        if( value.getDecimalNumbers() != null ) {
            view.setDecimalNumbers( value.getDecimalNumbers().stream().map( DecimalNumberFormatter::formatNumber ).collect( Collectors.joining(", ")) );
        }

        if ( value.getLinkedEquipmentDecimalNumbers() != null && !value.getLinkedEquipmentDecimalNumbers().isEmpty() ) {
            view.setLinkedEquipment( value.getLinkedEquipmentDecimalNumbers().stream().map( DecimalNumberFormatter::formatNumber ).collect( Collectors.joining(", ")) );
        } else {
            view.setLinkedEquipment( lang.equipmentPrimaryUseNotDefinied() );
        }

        fireEvent(new EquipmentEvents.ShowDocumentList(view.documents(), equipment.getId()));
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
    PolicyService policyService;
    @Inject
    EquipmentControllerAsync equipmentService;

    private Equipment equipment;
    private AppEvents.InitDetails initDetails;
}
