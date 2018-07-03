package ru.protei.portal.ui.common.client.widget.decimalnumber.provider;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.core.model.struct.DecimalNumberQuery;
import ru.protei.portal.ui.common.client.service.EquipmentControllerAsync;

/**
 * Валидатор децимальных чисел
 */
public class DecimalNumberDataProvider  {

    /**
     * Проверка доступности децимального номера изделия
     * @return
     */
    public void checkIfExistDecimalNumber( DecimalNumber number, AsyncCallback<Boolean> callback ) {
        equipmentService.checkIfExistDecimalNumber( number, callback );
    }

    /**
     * Получение следующего свободного децимального номера
     * @param filter    допускается частичная заполненность (мб заполнен только код по классификатору и/или рег.номер)
     * @return
     */
    public void getNextAvailableRegisterNumber( DecimalNumberQuery filter, AsyncCallback<Integer> callback ) {
        equipmentService.getNextAvailableRegisterNumber( filter, callback );
    }

    public void getNextAvailableModification( DecimalNumberQuery filter, AsyncCallback< Integer > callback ) {
        equipmentService.getNextAvailableRegisterNumberModification( filter, callback );
    }

    @Inject
    EquipmentControllerAsync equipmentService;
}
