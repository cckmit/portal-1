package ru.protei.portal.ui.product.client.activity.preview;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * Абстракция вида карточки просмотра продукта
 */
public interface AbstractProductPreviewView extends IsWidget {

    void setName( String name );
    void setActivity( AbstractProductPreviewActivity activity );
    void watchForScroll(boolean isWatch);
    void setTypeImage(String image);
    void setInfo(String value );
    Widget asWidget(boolean isForTableView);

}
