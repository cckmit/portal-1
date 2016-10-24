package ru.protei.portal.ui.company.client.activity.preview;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * Абстракция представления превью компании
 */
public interface AbstractCompanyPreviewView extends IsWidget {

    void setActivity( AbstractCompanyPreviewActivity activity );
}
