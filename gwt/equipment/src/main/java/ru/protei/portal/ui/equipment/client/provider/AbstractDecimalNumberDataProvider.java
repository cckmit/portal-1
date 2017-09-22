package ru.protei.portal.ui.equipment.client.provider;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.core.model.struct.DecimalNumberFilter;

import java.util.List;

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
     * @param number    допускается частичная заполненность (мб заполнен только код по классификатору и/или рег.номер)
     * @return
     */
    void getNextAvailableRegisterNumber( DecimalNumber number, AsyncCallback<DecimalNumber> callback  );

    void getNextAvailableRegisterNumberModification( DecimalNumber number, AsyncCallback<DecimalNumber> callback  );

    void getNextAvailableRegNumberNotContainsInList(DecimalNumberFilter filter, AsyncCallback<DecimalNumber> callback);

    void getNextAvailableRegisterNumberModificationNotContainsInList(DecimalNumberFilter filter, AsyncCallback<DecimalNumber> callback);
}
