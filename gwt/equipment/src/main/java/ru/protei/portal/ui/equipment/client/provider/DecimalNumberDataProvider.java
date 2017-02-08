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
        Integer a;

        equipmentService.checkIfExistDecimalNumber( number, new RequestCallback<Boolean>(){
            @Override
            public void onError( Throwable throwable ) {}

            @Override
            public void onSuccess( Boolean result ) {
                isExist = result;
            }
        });

        return isExist;
    }

    @Override
    public String getNextAvailableRegisterNumber( DecimalNumber number ) {
        equipmentService.getNextAvailableRegisterNumber( number, new RequestCallback<DecimalNumber>(){
            @Override
            public void onError( Throwable throwable ) {}

            @Override
            public void onSuccess( DecimalNumber result ) {
            }
        });

        return null;
    }

    @Inject
    EquipmentServiceAsync equipmentService;
}
