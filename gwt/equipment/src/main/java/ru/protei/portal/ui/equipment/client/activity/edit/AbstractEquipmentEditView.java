package ru.protei.portal.ui.equipment.client.activity.edit;

import com.google.gwt.user.client.ui.*;
import ru.protei.portal.core.model.dict.En_EquipmentType;
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.core.model.struct.Project;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.EquipmentShortView;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;

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

    HasValue<EquipmentShortView> linkedEquipment();

    HasValue< PersonShortView > manager();

    HasValue<EntityOption> project();

    HasValue<String> date();

    HasWidgets documents();

    void setLinkedEquipmentFilter(Selector.SelectorFilter<EquipmentShortView> filter);

    void setVisibilitySettingsForCreated(boolean isVisible);

    boolean isDecimalNumbersCorrect();

    void setNumbers(List<DecimalNumber> decimalNumbers, boolean isEditable);

    List<DecimalNumber> getNumbers();

    HasEnabled createDocumentButtonEnabled();

    HasVisibility documentsVisibility();
}
