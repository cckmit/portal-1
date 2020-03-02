package ru.protei.portal.ui.ipreservation.client.activity.edit;

import com.google.gwt.user.client.ui.*;
import ru.protei.portal.core.model.dict.En_CustomerType;
import ru.protei.portal.core.model.dict.En_RegionState;
import ru.protei.portal.core.model.struct.ProductDirectionInfo;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonProjectMemberView;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

import java.util.Set;

/**
 * Абстракция вида карточки создания/редактирования подсети
 */
public interface AbstractSubnetEditView extends IsWidget {

    void setActivity(AbstractSubnetEditActivity activity);

    HasValue<String> address();
    HasValue<String> mask();
    HasValue<Boolean> local();
    HasText comment();

    HasValidable addressValidator();
    HasValidable maskValidator();

    HasEnabled saveEnabled();

    HasVisibility saveVisibility();
}
