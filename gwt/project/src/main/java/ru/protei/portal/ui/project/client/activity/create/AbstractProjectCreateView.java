package ru.protei.portal.ui.project.client.activity.create;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_CustomerType;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

import java.util.Set;

public interface AbstractProjectCreateView extends IsWidget {
    void setActivity(AbstractProjectCreateActivity activity);
    HasValue<String> name();
    HasValue<String> description();
    HasValue<Set<ProductShortView>> products();
    HasValue<EntityOption> company();
    HasValue<En_CustomerType> customerType();
    HasValidable nameValidator();
    HasWidgets createProductContainer();
}
