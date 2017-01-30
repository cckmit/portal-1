package ru.protei.portal.ui.equipment.client.activity.preview;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.Equipment;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.EquipmentEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.EquipmentServiceAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

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

        this.equipmentId = event.equipment.getId();

        fillView( event.equipment );
    }

    private void fillView( Equipment value ) {

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
    EquipmentServiceAsync equipmentService;

    private Long equipmentId;
    private AppEvents.InitDetails initDetails;
}
