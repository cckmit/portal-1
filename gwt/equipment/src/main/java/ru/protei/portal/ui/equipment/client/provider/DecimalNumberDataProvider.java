package ru.protei.portal.ui.equipment.client.provider;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.service.EquipmentServiceAsync;
import ru.protei.portal.ui.common.shared.model.DecimalNumber;

/**
 * Провайдер данных по децимальным номерам
 */
public class DecimalNumberDataProvider implements AbstractDecimalNumberDataProvider {

    @Override
    public void checkIfExistDecimalNumber( DecimalNumber number, AsyncCallback<Boolean> callback ) {
        equipmentService.checkIfExistDecimalNumber( number, callback );
    }

    @Override
    public void getNextAvailableRegisterNumber( DecimalNumber number, AsyncCallback<DecimalNumber> callback ) {
        equipmentService.getNextAvailableRegisterNumber( number, callback );
    }

    @Override
    public void getNextAvailableRegisterNumberModification( DecimalNumber number, AsyncCallback< DecimalNumber > callback ) {
        equipmentService.getNextAvailableRegisterNumberModification( number, callback );
    }

    @Inject
    EquipmentServiceAsync equipmentService;
}
