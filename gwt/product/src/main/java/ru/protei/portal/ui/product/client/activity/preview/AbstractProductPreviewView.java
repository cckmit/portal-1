package ru.protei.portal.ui.product.client.activity.preview;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import ru.protei.portal.core.model.dict.En_DevUnitType;

/**
 * Абстракция вида карточки просмотра продукта
 */
public interface AbstractProductPreviewView extends IsWidget {

    void setName( String name );
    void setType(En_DevUnitType type );
    void setActivity( AbstractProductPreviewActivity activity );
    void watchForScroll(boolean isWatch);
    void setInfo( String value );

    void setWikiLink(String value);

    void setConfiguration(String value);

    void setHistoryVersion(String value);

    void setCdrDescription(String value);

    Widget asWidget(boolean isForTableView);

}
