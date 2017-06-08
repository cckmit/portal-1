package ru.protei.portal.ui.product.client.activity.edit;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.ui.common.client.common.NameStatus;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

/**
 * Абстракция вида карточки создания/редактирования продукта
 */
public interface AbstractProductEditView extends IsWidget {

    void setActivity( AbstractProductEditActivity activity );

    HasValue<String> name();

    HasValidable nameValidator();

    HasValue<String> info();

    void setNameStatus ( NameStatus status );

    void setStateBtnText(String caption);

    HasVisibility state(  );

    HasEnabled save();
}
