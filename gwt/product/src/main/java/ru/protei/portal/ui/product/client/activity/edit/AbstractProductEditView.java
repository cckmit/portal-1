package ru.protei.portal.ui.product.client.activity.edit;

import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.ui.common.client.service.NameStatus;

/**
 * Вид карточки создания/редактирования продукта
 */
public interface AbstractProductEditView extends IsWidget {

    void setActivity( AbstractProductEditActivity activity );

    void reset();

    void setName( String name );
    HasText getName();

    void setInfo( String info );
    HasText getInfo();

    void setState( boolean state );
    HasValue<Boolean> getState ();

    void setNameChecked ( NameStatus status );

}
