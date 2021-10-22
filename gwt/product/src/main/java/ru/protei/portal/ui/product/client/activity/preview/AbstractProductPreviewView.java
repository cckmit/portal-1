package ru.protei.portal.ui.product.client.activity.preview;

import com.google.gwt.user.client.ui.*;

import java.util.Map;

/**
 * Абстракция вида карточки просмотра продукта
 */
public interface AbstractProductPreviewView extends IsWidget {

    void setName( String name );

    void setActivity( AbstractProductPreviewActivity activity );

    void setInfo( String value );

    void setDirection(String direction);

    void setInternalDocLink(String value);

    void setExternalDocLink(String value);

    HasVisibility parentsContainerVisibility();

    void setParents(Map<String, String> nameToLink);


    void setTypeImage(String image);

    void showFullScreen(boolean isFullScreen);

    Widget asWidget(boolean isForTableView);
}
