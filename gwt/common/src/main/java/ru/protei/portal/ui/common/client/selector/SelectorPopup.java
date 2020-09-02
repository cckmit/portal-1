package ru.protei.portal.ui.common.client.selector;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.UIObject;
import ru.protei.portal.ui.common.client.events.AddHandler;
import ru.protei.portal.ui.common.client.popup.BasePopupView;
import ru.protei.portal.ui.common.client.selector.popup.PopupHandler;

public interface SelectorPopup {
    void setPopupHandler( PopupHandler tPopupSelector );

    HasWidgets getChildContainer();

    void setSearchHandler( SearchHandler searchHandler );

    void showNear( UIObject showNear );

    void showNear( UIObject showNear, BasePopupView.Position position, Integer width );

    void showLoading( boolean isLoading );

    HasVisibility searchVisibility();

    void setNoElements(boolean isSearchResultEmpty, String noElementsMessage );

    HandlerRegistration addAddHandler( AddHandler addhandler );

    void setAddButtonVisibility( boolean isVisible );

    void setAddButton(boolean addVisible, String text);

    void addStyleName(String style);

    void hide();
}
