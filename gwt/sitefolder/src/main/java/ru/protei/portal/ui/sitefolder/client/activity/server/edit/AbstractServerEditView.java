package ru.protei.portal.ui.sitefolder.client.activity.server.edit;

import com.google.gwt.user.client.ui.*;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

public interface AbstractServerEditView extends IsWidget {

    void setActivity(AbstractServerEditActivity activity);

    void setCompanyId(Long companyId);

    HasValue<String> name();

    HasValue<EntityOption> platform();

    HasValue<String> ip();

    HasValue<String> parameters();

    HasValue<String> comment();

    HasWidgets listContainer();

    HasVisibility listContainerVisibility();

    HasVisibility listContainerHeaderVisibility();

    HasEnabled platformEnabled();

    HasValidable nameValidator();

    HasValidable platformValidator();

    HasVisibility createButtonVisibility();

    HasVisibility openButtonVisibility();
}
