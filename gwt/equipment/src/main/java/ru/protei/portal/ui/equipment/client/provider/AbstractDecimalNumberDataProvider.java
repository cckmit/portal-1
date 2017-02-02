package ru.protei.portal.ui.equipment.client.provider;

import ru.protei.portal.ui.common.shared.model.DecimalNumber;

/**
 * Валидатор децимальных чисел
 */
public interface AbstractDecimalNumberDataProvider {

    /**
     * Проверка доступности децимального номера изделия
     * @return
     */
    boolean checkIfExistDecimalNumber( DecimalNumber number );

    /**
     * Получение следующего свободного децимального номера
     * @param number    допускается частичная заполненность (мб заполнен только код по классификатору и/или рег.номер)
     * @return
     */
    String getNextAvailableRegisterNumber( DecimalNumber number );
}
