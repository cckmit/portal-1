package ru.protei.portal.ui.common.client.selector;

import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.UIObject;
import ru.protei.portal.ui.common.client.events.AddHandler;
import ru.protei.portal.ui.common.client.selector.popup.PopupHandler;
import ru.protei.portal.ui.common.client.widget.composite.popper.PopperComposite;

public interface SelectorPopup extends IsWidget, HasCloseHandlers {
    void setPopupHandler( PopupHandler tPopupSelector );

    HasWidgets getChildContainer();

    void setSearchHandler( SearchHandler searchHandler );

    void showNear( UIObject showNear );

    void showNear( UIObject showNear, PopperComposite.Placement placement );

    void showLoading( boolean isLoading );

    void setNoElements( boolean isSearchResultEmpty, String noElementsMessage );

    HandlerRegistration addAddHandler( AddHandler addhandler );

    void setAddButtonVisibility( boolean isVisible );

    void setAddButton(boolean addVisible, String text);

    void hide();

    boolean isVisible();
}
