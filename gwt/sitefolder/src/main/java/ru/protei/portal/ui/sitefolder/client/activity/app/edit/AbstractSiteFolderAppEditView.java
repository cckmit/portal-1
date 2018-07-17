package ru.protei.portal.ui.sitefolder.client.activity.app.edit;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.struct.PathInfo;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

public interface AbstractSiteFolderAppEditView extends IsWidget {

    void setActivity(AbstractSiteFolderAppEditActivity activity);

    void setPlatformId(Long platformId);

    HasValue<String> name();

    HasValue<EntityOption> server();

    HasValue<String> comment();

    HasValidable nameValidator();

    HasWidgets pathsContainer();

    HasEnabled serverEnabled();

    HasValidable serverValidator();
}
