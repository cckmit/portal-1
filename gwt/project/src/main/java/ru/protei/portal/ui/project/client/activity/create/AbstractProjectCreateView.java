package ru.protei.portal.ui.project.client.activity.create;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_CustomerType;
import ru.protei.portal.core.model.struct.ProductDirectionInfo;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

import java.util.Set;

public interface AbstractProjectCreateView extends IsWidget {
    void setActivity(AbstractProjectCreateActivity activity);
    HasValue<String> name();
    HasValue<String> description();
    HasValue<EntityOption> region();
    HasValue<ProductDirectionInfo> direction();
    HasValue<En_CustomerType> customerType();
    HasValue<EntityOption> company();
    HasValue<Set<ProductShortView>> products();
    HasValidable nameValidator();
    HasValidable regionValidator();
    HasValidable directionValidator();
    HasValidable customerTypeValidator();
    HasValidable companyValidator();

}
