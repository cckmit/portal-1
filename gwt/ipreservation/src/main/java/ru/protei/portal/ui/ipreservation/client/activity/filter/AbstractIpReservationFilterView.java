package ru.protei.portal.ui.ipreservation.client.activity.filter;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.Subnet;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.SubnetOption;

import java.util.Set;

/**
 * Представление поиска зарезервированного IP
 */
public interface AbstractIpReservationFilterView extends IsWidget {
    void setActivity( AbstractIpReservationFilterActivity activity);

    HasValue<String> search();
    HasValue<Set<SubnetOption>> subnets();
    HasValue<PersonShortView> owner();
    HasValue<DateInterval> reserveDate();
    HasValue<DateInterval> releaseDate();
    HasValue<DateInterval> lastActiveDate();

    HasValue<En_SortField> sortField();
    HasValue< Boolean > sortDir();

    void resetFilter();

    HasVisibility subnetsVisibility();
    HasVisibility ownerVisibility();
    HasVisibility datesVisibility();
}