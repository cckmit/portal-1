package ru.protei.portal.ui.equipment.client.provider;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.DecimalNumber;

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

    void getNextAvailableRegNumberNotContainsInList(List<Integer> regNumbers, String classifierCode, String orgCode, AsyncCallback<DecimalNumber> callback);

    void getNextAvailableRegisterNumberModificationNotContainsInList(List<Integer> mods, String classifierCode, String orgCode, String regNum, AsyncCallback<DecimalNumber> callback);
}
