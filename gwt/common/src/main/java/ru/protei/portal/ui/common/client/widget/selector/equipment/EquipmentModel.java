package ru.protei.portal.ui.common.client.widget.selector.equipment;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_EquipmentType;
import ru.protei.portal.core.model.query.EquipmentQuery;
import ru.protei.portal.core.model.view.EquipmentShortView;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.EquipmentEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.EquipmentControllerAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.*;

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

    public void subscribe( SelectorWithModel< EquipmentShortView > selector ) {
        subscribers.add( selector );
        selector.fillOptions( list );
    }

    private void notifySubscribers() {
        for ( SelectorWithModel< EquipmentShortView > selector : subscribers ) {
            selector.fillOptions( list );
            selector.refreshValue();
        }
    }

    private void refreshOptions() {
        equipmentService.equipmentOptionList(query, callback);
    }

    public void setEquipmentTypes(Set<En_EquipmentType> equipmentTypes) {
        query.setTypes(equipmentTypes);
        refreshOptions();
    }

    public void setProjectId(Long projectId) {
        query.setProjectId(projectId);
        refreshOptions();
    }

    @Inject
    EquipmentControllerAsync equipmentService;

    @Inject
    Lang lang;

    private EquipmentQuery query = new EquipmentQuery(new HashSet<>(
            Arrays.asList( En_EquipmentType.ASSEMBLY_UNIT, En_EquipmentType.COMPLEX, En_EquipmentType.PRODUCT)
    ));

    private final RequestCallback<List<EquipmentShortView>> callback = new RequestCallback<List<EquipmentShortView>>() {
        @Override
        public void onError( Throwable throwable ) {
            fireEvent( new NotifyEvents.Show( lang.errGetList(), NotifyEvents.NotifyType.ERROR ) );
        }

        @Override
        public void onSuccess( List< EquipmentShortView > options ) {
            list.clear();
            list.addAll( options );
            notifySubscribers();
        }
    };

    private List< EquipmentShortView > list = new ArrayList<>();
    List<SelectorWithModel< EquipmentShortView >> subscribers = new ArrayList<>();
}
