package ru.protei.portal.ui.official.client.activity.edit;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.ProductShortView;

/**
 * Абстрактное представление формы редактирования матрицы принятия решений
 */
public interface AbstractOfficialEditView extends IsWidget{

    void setActivity(AbstractOfficialEditActivity activity);

    HasValue<EntityOption> region();

    HasValue<ProductShortView> product();

    HasValue<String> info();
}