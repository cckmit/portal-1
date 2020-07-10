package ru.protei.portal.ui.equipment.client.activity.filter;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.view.EquipmentShortView;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

import java.util.Set;

/**
 * Абстракция вида фильтра оборудования
 */
public interface AbstractEquipmentFilterView extends IsWidget {

    void setActivity( AbstractEquipmentFilterActivity activity );

    HasValue<String> name();

    void resetFilter();

    HasValue< PersonShortView > manager();

    HasValue<Set<En_EquipmentType>> types();

    HasValue<Set<En_OrganizationCode>> organizationCodes();

    HasValue<String> classifierCode();

    HasValue<String> registerNumber();

    HasValue<En_SortField> sortField();

    HasValue<Boolean> sortDir();

    HasValue< EquipmentShortView > equipment();

    HasValidable classifierCodeValidator();

    HasValidable regNumValidator();
}