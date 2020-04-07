package ru.protei.portal.ui.role.client.activity.preview;

import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_PrivilegeEntity;

import java.util.List;
import java.util.Map;

/**
 * Абстракция вида превью роли
 */
public interface AbstractRolePreviewView extends IsWidget {

    void setActivity( AbstractRolePreviewActivity activity );

    void setName( String value );

    void setDescription( String value );

    void setPrivileges( Map< En_PrivilegeEntity, String > privileges );
}
