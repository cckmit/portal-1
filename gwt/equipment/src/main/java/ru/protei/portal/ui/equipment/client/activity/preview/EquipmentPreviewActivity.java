package ru.protei.portal.ui.equipment.client.activity.preview;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.Equipment;
import ru.protei.portal.ui.common.client.events.EquipmentEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.En_EquipmentStageLang;
import ru.protei.portal.ui.common.client.lang.En_EquipmentTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.EquipmentServiceAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.equipment.client.common.EquipmentUtils;

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

        this.equipmentId = event.equipment.getId();

        if( equipmentId == null ) {
            fillView( equipmentId );
            return;
        }

        fillView( event.equipment );
    }

    private void fillView( Equipment value ) {
        view.setHeader( lang.equipmentDescription() + " #" + value.getId() );
        view.setName( value.getName() );
        view.setNameBySldWrks( value.getNameSldWrks() );
        view.setComment( value.getComment() );
        view.setType( typeLang.getName( value.getType() ) );
        view.setStage( stageLang.getName( value.getStage() ), value.getStage().name().toLowerCase() );
        view.setProject( value.getProject() == null ? "" : value.getProject() );
        view.setManager( value.getManagerShortName() == null ? "" : value.getManagerShortName() );

        if( value.getDecimalNumbers() != null ) {
            view.setDecimalNumbers( value.getDecimalNumbers().stream().map( EquipmentUtils:: formatNumber ).collect( Collectors.joining(", ")) );
        }

        if ( value.getLinkedEquipmentDecimalNumbers() != null && !value.getLinkedEquipmentDecimalNumbers().isEmpty() ) {
            view.setLinkedEquipment( value.getLinkedEquipmentDecimalNumbers().stream().map( EquipmentUtils:: formatNumber ).collect( Collectors.joining(", ")) );
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
    EquipmentServiceAsync equipmentService;

    private Long equipmentId;
}
