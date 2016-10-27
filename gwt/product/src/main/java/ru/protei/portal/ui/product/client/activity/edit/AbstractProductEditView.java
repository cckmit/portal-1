package ru.protei.portal.ui.product.client.activity.edit;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.ui.common.client.service.NameStatus;

/**
 * Вид карточки создания/редактирования продукта
 */
public interface AbstractProductEditView extends IsWidget {

    void setActivity( AbstractProductEditActivity activity );

    HasValue<String> name();

    HasValue<String> info();

    void setNameStatus ( NameStatus status );

    void setStateBtnText(String caption);

    HasVisibility state(  );

    HasEnabled save();

}
