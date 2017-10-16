package ru.protei.portal.ui.equipment.client.activity.edit;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_EquipmentStage;
import ru.protei.portal.core.model.dict.En_EquipmentType;
import ru.protei.portal.core.model.ent.Equipment;
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.core.model.view.EquipmentShortView;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.List;


/**
 *  Абстрактное представление карточки редактирования единицы оборудования
 */
public interface AbstractEquipmentEditView extends IsWidget {
    void setActivity( AbstractEquipmentEditActivity activity );

    HasValue<String> nameSldWrks();

    HasValue<String> name();

    HasValue<String> comment();

    HasEnabled nameEnabled();

    HasEnabled typeEnabled();

    HasValue<En_EquipmentType> type();

    HasValue<En_EquipmentStage> stage();

    HasValue<EquipmentShortView> linkedEquipment();

    HasValue<List<DecimalNumber> > numbers();

    HasValue< PersonShortView > manager();

    HasValue<String> project();

    HasValue<String> date();

    void setVisibilitySettingsForCreated(boolean isVisible);
}
