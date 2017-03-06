package ru.protei.portal.ui.equipment.client.activity.filter;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_EquipmentStage;
import ru.protei.portal.core.model.dict.En_EquipmentType;
import ru.protei.portal.core.model.dict.En_OrganizationCode;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.core.model.view.EntityOption;

import java.util.List;
import java.util.Set;

/**
 * Абстракция вида фильтра оборудования
 */
public interface AbstractEquipmentFilterView extends IsWidget {

    void setActivity( AbstractEquipmentFilterActivity activity );

    HasValue<String> name();

    void resetFilter();

    HasValue<Set<En_EquipmentStage>> stages();

    HasValue<Set<En_EquipmentType>> types();

    HasValue<Set<En_OrganizationCode>> organizationCodes();

    HasValue<String> classifierCode();

    HasValue<String> registerNumber();
}