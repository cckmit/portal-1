package ru.protei.portal.ui.equipment.client.provider;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.protei.portal.core.model.struct.DecimalNumberQuery;
import ru.protei.portal.ui.common.client.service.EquipmentServiceAsync;
import ru.protei.portal.core.model.ent.DecimalNumber;

/**
 * Провайдер данных по децимальным номерам
 */
public class DecimalNumberDataProvider implements AbstractDecimalNumberDataProvider {

    @Override
    public void checkIfExistDecimalNumber( DecimalNumber number, AsyncCallback<Boolean> callback ) {
        equipmentService.checkIfExistDecimalNumber( number, callback );
    }

    @Override
    public void getNextAvailableRegisterNumber( DecimalNumberQuery filter, AsyncCallback<Integer> callback ) {
        equipmentService.getNextAvailableRegisterNumber( filter, callback );
    }

    @Override
    public void getNextAvailableModification( DecimalNumberQuery filter, AsyncCallback< Integer > callback ) {
        equipmentService.getNextAvailableRegisterNumberModification( filter, callback );
    }

    @Inject
    EquipmentServiceAsync equipmentService;
}
