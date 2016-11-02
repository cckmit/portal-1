package ru.protei.portal.ui.product.client.activity.preview;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * Абстракция вида карточки просмотра продукта
 */
public interface AbstractProductPreviewView extends IsWidget {

    void setActivity( AbstractProductPreviewActivity activity );
    void setInfo( String value );

}
