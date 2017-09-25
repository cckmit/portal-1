package ru.protei.portal.ui.equipment.client.widget.number.item;

import ru.protei.portal.core.model.ent.DecimalNumber;

import java.util.List;

/**
 * Хэндлер для отправки сообщение от бокса к списку
 */
public interface AbstractBoxHandler {
    boolean numberExists(DecimalNumber value);

    List<Integer> getRegNumbersListWithSpecificCode(Integer classifierCode);

    List<Integer> makeModListWithSameCodeAndRegNumber(Integer classifierCode, Integer registerNumber);
}
