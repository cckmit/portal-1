package ru.protei.portal.ui.official.client.activity.filter;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.ProductShortView;

/**
 * Абстракция вида фильтра должностных лиц
 */
public interface AbstractOfficialFilterView extends IsWidget{

    void setActivity(AbstractOfficialFilterActivity activity);

    HasValue<String> searchPattern();

    HasValue<DateInterval> dateRange();

    HasValue<En_SortField> sortField();

    HasValue< Boolean > sortDir();

    HasValue<ProductShortView> product();

    HasValue<EntityOption> region();

    void resetFilter();


}
