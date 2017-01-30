package ru.protei.portal.ui.equipment.client.widget.number;

/**
 * Валидатор децимальных чисел
 */
public interface DecimalNumberDataProvider {

    /**
     * Проверка доступности децимального номера изделия
     * @return
     */
    boolean isValidDecimalNumber( String classifierCode );

    String getNextAvailableRegisterNumber( String classifierCode, String regNumber );
}
