package ru.protei.portal.ui.product.client.activity.preview;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * Абстракция вида карточки просмотра продукта
 */
public interface AbstractProductPreviewView extends IsWidget {

    void setName( String name );
    void setType(String type);
    void setActivity( AbstractProductPreviewActivity activity );
    void watchForScroll(boolean isWatch);

    void setInfo(String value );

    void setWikiLink(String value);

    void setConfiguration(String value);

    void setHistoryVersion(String value);

    void setCdrDescription(String value);

    Widget asWidget(boolean isForTableView);

    void showFullScreen(boolean value);
}
