package ru.protei.portal.ui.product.client.activity.preview;

import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * Абстракция вида карточки просмотра продукта
 */
public interface AbstractProductPreviewView extends IsWidget {

    void setName( String name );

    void setActivity( AbstractProductPreviewActivity activity );

    void setInfo( String value );

    void setDirection(String direction);

    void setWikiLink(String value);

    void setConfiguration(String value);

    void setHistoryVersion(String value);

    void setCdrDescription(String value);

    void setTypeImage(String image);

    void showFullScreen(boolean isFullScreen);

    Widget asWidget(boolean isForTableView);
}
