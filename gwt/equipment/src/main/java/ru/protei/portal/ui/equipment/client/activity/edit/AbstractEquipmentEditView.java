package ru.protei.portal.ui.equipment.client.activity.edit;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_EquipmentStage;
import ru.protei.portal.core.model.dict.En_EquipmentType;
import ru.protei.portal.ui.common.shared.model.DecimalNumber;


/**
 *  Абстрактное представление карточки редактирования единицы оборудования
 */
public interface AbstractEquipmentEditView extends IsWidget {
    void setActivity( AbstractEquipmentEditActivity activity );

    HasValue<String> nameBySldWrks();

    HasValue<String> nameBySpecification();

    HasValue<String> comment();

    HasEnabled nameBySpecificationEnabled();

    HasEnabled typeEnabled();

    HasValue<En_EquipmentType> type();

    HasValue<En_EquipmentStage> stage();

    HasValue<DecimalNumber> pdraNumber();

    HasValue<DecimalNumber> pamrNumber();

    HasEnabled pamrNumberEnabled();

    HasEnabled pdraNumberEnabled();
}
