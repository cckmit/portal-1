package ru.protei.portal.ui.equipment.client.provider;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.core.model.struct.DecimalNumberQuery;

/**
 * Валидатор децимальных чисел
 */
public interface AbstractDecimalNumberDataProvider {

    /**
     * Проверка доступности децимального номера изделия
     * @return
     */
    void checkIfExistDecimalNumber( DecimalNumber number, AsyncCallback<Boolean> callback );

    /**
     * Получение следующего свободного децимального номера
     * @param filter    допускается частичная заполненность (мб заполнен только код по классификатору и/или рег.номер)
     * @return
     */
    void getNextAvailableRegisterNumber( DecimalNumberQuery filter, AsyncCallback<Integer> callback  );

    void getNextAvailableModification( DecimalNumberQuery filter, AsyncCallback<Integer> callback  );
}
