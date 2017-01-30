package ru.protei.portal.ui.equipment.client.activity.edit;

import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

import java.util.Date;

/**
 * Created by michael on 02.11.16.
 */
public interface AbstractEquipmentEditView extends IsWidget {
    void setActivity( AbstractEquipmentEditActivity activity );
}
