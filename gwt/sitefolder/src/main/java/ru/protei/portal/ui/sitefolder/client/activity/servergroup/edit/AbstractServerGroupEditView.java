package ru.protei.portal.ui.sitefolder.client.activity.servergroup.edit;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractServerGroupEditView extends IsWidget {
    HasValue<String> name();
}
