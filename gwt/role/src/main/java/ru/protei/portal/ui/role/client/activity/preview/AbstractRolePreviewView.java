package ru.protei.portal.ui.role.client.activity.preview;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * Абстракция вида превью роли
 */
public interface AbstractRolePreviewView extends IsWidget {

    void setActivity( AbstractRolePreviewActivity activity );

    void setHeader( String value );

    void setName( String value );

    void setDescription( String value );
}
