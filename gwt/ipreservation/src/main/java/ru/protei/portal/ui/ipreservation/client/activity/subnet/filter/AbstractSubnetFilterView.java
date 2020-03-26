package ru.protei.portal.ui.ipreservation.client.activity.subnet.filter;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.SubnetOption;

import java.util.Set;

/**
 * Представление поиска подсетей
 */
public interface AbstractSubnetFilterView extends IsWidget {
    void setActivity(AbstractSubnetFilterActivity activity);

    HasValue<String> search();

    HasValue<En_SortField> sortField();
    HasValue< Boolean > sortDir();

    void resetFilter();
}