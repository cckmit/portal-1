package ru.protei.portal.ui.project.client.activity.quickcreate;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_CustomerType;
import ru.protei.portal.core.model.query.ProductQuery;
import ru.protei.portal.core.model.struct.ProductDirectionInfo;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

import java.util.Set;

/**
 * Представление создания проекта с минимальным набором параметров
 */
public interface AbstractProjectCreateView extends IsWidget {
    void setActivity(AbstractProjectCreateActivity activity);

    void updateProductQuery(ProductQuery productQuery);

    HasValue<String> name();
    HasValue<String> description();
    HasValue<EntityOption> region();
    HasValue<ProductDirectionInfo> direction();
    HasValue<En_CustomerType> customerType();
    HasValue<EntityOption> company();
    HasValue<ProductShortView> product();
    HasValidable nameValidator();
    HasValidable regionValidator();
    HasValidable directionValidator();
    HasValidable customerTypeValidator();
    HasValidable companyValidator();
//    void refreshProducts();
}
