package ru.protei.portal.ui.product.client.activity.edit;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.ui.common.client.common.NameStatus;
import ru.protei.portal.ui.common.client.widget.subscription.model.Subscription;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

import java.util.List;

/**
 * Абстракция вида карточки создания/редактирования продукта
 */
public interface AbstractProductEditView extends IsWidget {

    void setActivity( AbstractProductEditActivity activity );

    HasValue<String> name();

    HasValue<En_DevUnitType> type();

    HasValidable nameValidator();

    HasValue<String> info();

    void setNameStatus ( NameStatus status );

    void setStateBtnText(String caption);

    HasVisibility state(  );

    HasEnabled save();

    HasValue<List<Subscription>> productSubscriptions();
    HasValidable productSubscriptionsValidator();

}
