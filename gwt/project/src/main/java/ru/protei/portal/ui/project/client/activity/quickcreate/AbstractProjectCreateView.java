package ru.protei.portal.ui.project.client.activity.quickcreate;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_CustomerType;
import ru.protei.portal.core.model.dto.ProductDirectionInfo;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.widget.selector.person.AsyncPersonModel;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

import java.util.Set;

/**
 * Представление создания проекта с минимальным набором параметров
 */
public interface AbstractProjectCreateView extends IsWidget {
    void setActivity(AbstractProjectCreateActivity activity);
    void updateProductSelector(Set<Long> directionIds);
    HasValue<String> name();
    HasValue<String> description();
    HasValue<EntityOption> region();
    HasValue<Set<ProductDirectionInfo>> directions();
    HasValue<Set<ProductShortView>> products();
    HasEnabled productEnabled();
    HasValue<En_CustomerType> customerType();
    HasValue<EntityOption> company();
    HasValidable nameValidator();
    HasValidable regionValidator();
    HasValidable directionValidator();
    HasValidable customerTypeValidator();
    HasValidable companyValidator();

    HasValidable headManagersValidator();

    HasValue<Set<PersonShortView>> headManagers();

    void setManagersModel(AsyncPersonModel model);

}
