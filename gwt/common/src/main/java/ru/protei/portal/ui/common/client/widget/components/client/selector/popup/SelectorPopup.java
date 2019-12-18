package ru.protei.portal.ui.common.client.widget.components.client.selector.popup;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.UIObject;
import ru.protei.portal.ui.common.client.widget.components.client.selector.search.SearchHandler;

public interface SelectorPopup {
    void setPopupHandler( PopupHandler tPopupSelector );

    HasWidgets getChildContainer();

    void setSearchHandler( SearchHandler searchHandler );

    void showNear( UIObject showNear );

    void showLoading( boolean isLoading );

    void setNoElements( boolean isSearchResultEmpty, String noElementsMessage );

    void hide();
}
