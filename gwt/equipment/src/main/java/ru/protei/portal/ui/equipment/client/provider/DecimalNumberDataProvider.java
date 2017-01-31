package ru.protei.portal.ui.equipment.client.provider;

import ru.protei.portal.ui.common.shared.model.DecimalNumber;

/**
 * Провайдер данных по децимальным номерам
 */
public class DecimalNumberDataProvider implements AbstractDecimalNumberDataProvider {

    @Override
    public boolean isValidDecimalNumber( DecimalNumber number ) {
        return false;
    }

    @Override
    public String getNextAvailableRegisterNumber( DecimalNumber number ) {
        return null;
    }
}
