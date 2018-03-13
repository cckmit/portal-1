package ru.protei.portal.ui.documentation.client.activity.filter;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.dict.En_OrganizationCode;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.Set;

public interface AbstractDocumentationFilterView extends IsWidget {

    void setActivity( AbstractDocumentationFilterActivity activity );

    HasValue<String> name();

    void resetFilter();

    HasValue<String> content();

    HasValue<PersonShortView> manager();

    HasValue<En_SortField> sortField();

    HasValue<Set<En_OrganizationCode>> organizationCodes();

    HasValue<DateInterval> dateRange();

/*
    HasValue<Set<En_EquipmentStage>> stages();

    HasValue<Set<En_EquipmentType>> types();


    HasValue<String> classifierCode();

    HasValue<String> registerNumber();

    HasValue<Boolean> sortDir();

    HasValue<EquipmentShortView> equipment();*/
}
