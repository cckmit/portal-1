package ru.protei.portal.ui.equipment.client.provider;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.protei.portal.core.model.struct.DecimalNumberFilter;
import ru.protei.portal.ui.common.client.service.EquipmentServiceAsync;
import ru.protei.portal.core.model.ent.DecimalNumber;

import java.util.List;

/**
 * Провайдер данных по децимальным номерам
 */
public class DecimalNumberDataProvider implements AbstractDecimalNumberDataProvider {

    @Override
    public void checkIfExistDecimalNumber( DecimalNumber number, AsyncCallback<Boolean> callback ) {
        equipmentService.checkIfExistDecimalNumber( number, callback );
    }

    @Override
    public void getNextAvailableRegisterNumber( DecimalNumberFilter filter, AsyncCallback<DecimalNumber> callback ) {
        equipmentService.getNextAvailableRegisterNumber( filter, callback );
    }

    @Override
    public void getNextAvailableRegisterNumberModification( DecimalNumberFilter filter, AsyncCallback< DecimalNumber > callback ) {
        equipmentService.getNextAvailableRegisterNumberModification( filter, callback );
    }

    @Inject
    EquipmentServiceAsync equipmentService;
}
