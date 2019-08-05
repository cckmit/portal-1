package ru.protei.portal.ui.product.client.activity.quickcreate;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.ui.common.client.common.NameStatus;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

public interface AbstractProductCreateView extends IsWidget {
    void setActivity(AbstractProductCreateActivity activity);

    HasValue<String> name();
    HasValidable nameValidator();
    HasValue<String> info();
    void setNameStatus(NameStatus status);
}
