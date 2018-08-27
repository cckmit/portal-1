package ru.protei.portal.ui.sitefolder.client.activity.app.edit;

import com.google.gwt.user.client.ui.*;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

public interface AbstractApplicationEditView extends IsWidget {

    void setActivity(AbstractApplicationEditActivity activity);

    void setPlatformId(Long platformId);

    HasValue<ProductShortView> component();

    HasValue<String> name();

    HasValue<EntityOption> server();

    HasValue<String> comment();

    HasValidable nameValidator();

    HasWidgets pathsContainer();

    HasEnabled serverEnabled();

    HasValidable serverValidator();
}
