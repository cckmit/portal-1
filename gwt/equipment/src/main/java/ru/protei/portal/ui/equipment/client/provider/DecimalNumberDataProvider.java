package ru.protei.portal.ui.equipment.client.provider;

import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.service.EquipmentServiceAsync;
import ru.protei.portal.ui.common.shared.model.DecimalNumber;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

/**
 * Провайдер данных по децимальным номерам
 */
public class DecimalNumberDataProvider implements AbstractDecimalNumberDataProvider {

    @Override
    public boolean checkIfExistDecimalNumber( DecimalNumber number ) {
        Boolean isExist = false;

        equipmentService.checkIfExistDecimalNumber( number, new RequestCallback<Boolean>(){
            @Override
            public void onError( Throwable throwable ) {}

            @Override
            public void onSuccess( Boolean result ) {
            }
        });

        return isExist;
    }

    @Override
    public String getNextAvailableRegisterNumber( DecimalNumber number ) {
        return null;
    }

    @Inject
    EquipmentServiceAsync equipmentService;
}
