package ru.protei.portal.ui.equipment.client.widget.selector;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_EquipmentType;
import ru.protei.portal.core.model.ent.Equipment;
import ru.protei.portal.core.model.query.EquipmentQuery;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.EquipmentEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.EquipmentServiceAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Модель контактов домашней компании
 */
public abstract class EquipmentModel implements Activity {

    @Event
    public void onInit( AuthEvents.Success event ) {
        refreshOptions();
    }

    @Event
    public void onEmployeeListChanged( EquipmentEvents.ChangeModel event ) {
        refreshOptions();
    }

    public void subscribe( ModelSelector< Equipment > selector ) {
        subscribers.add( selector );
        selector.fillOptions( list );
    }

    private void notifySubscribers() {
        for ( ModelSelector< Equipment > selector : subscribers ) {
            selector.fillOptions( list );
            selector.refreshValue();
        }
    }

    private void refreshOptions() {
        // todo: needed check filter values
        equipmentService.getEquipments( new EquipmentQuery( null, null, null, null, null,
                        new HashSet<>( Arrays.asList( En_EquipmentType.ASSEMBLY_UNIT, En_EquipmentType.COMPLEX, En_EquipmentType.PRODUCT ) )),
                new RequestCallback< List< Equipment > >() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent( new NotifyEvents.Show( lang.errGetList(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess( List< Equipment > options ) {
                list.clear();
                list.addAll( options );
                notifySubscribers();
            }
        } );
    }

    @Inject
    EquipmentServiceAsync equipmentService;

    @Inject
    Lang lang;

    private List< Equipment > list = new ArrayList<>();

    List< ModelSelector< Equipment > > subscribers = new ArrayList<>();
}
