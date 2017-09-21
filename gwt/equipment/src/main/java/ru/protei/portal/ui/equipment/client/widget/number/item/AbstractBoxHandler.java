package ru.protei.portal.ui.equipment.client.widget.number.item;

import ru.protei.portal.core.model.ent.DecimalNumber;

import java.util.List;

/**
 * Created by serebryakov on 20/09/17.
 */
public interface AbstractBoxHandler {
    boolean numberExists(DecimalNumber value);

    List<Integer> getRegNumbersListWithSpecificCode(Integer classifierCode);
}
