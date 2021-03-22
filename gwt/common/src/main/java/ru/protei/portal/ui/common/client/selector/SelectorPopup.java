package ru.protei.portal.ui.common.client.selector;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import ru.protei.portal.ui.common.client.events.AddHandler;
import ru.protei.portal.ui.common.client.widget.selector.popup.PopupHandler;
import ru.protei.portal.ui.common.client.widget.composite.popper.PopperComposite;

public interface SelectorPopup extends IsWidget {
    void setPopupHandler( PopupHandler tPopupSelector );

    HasWidgets getContainer();

    boolean isEmpty();

    void setSearchHandler( SearchHandler searchHandler );

    void showNear( Element showNear );

    void showNear( Element showNear, PopperComposite.Placement placement );

    void refreshPopupPosition();

    void showLoading( boolean isLoading );

    void setNoElements(boolean isSearchResultEmpty, String noElementsMessage );

    HandlerRegistration addAddHandler(AddHandler addhandler );

    void setAddButtonVisibility( boolean isVisible );

    void setAddButton(boolean addVisible, String text);

    void addStyleName(String style);

    Element getElement();

    void showNear(Element view, PopperComposite.Placement placement, int skidding, int distance);

    void hide();

    boolean isVisible();

    void setAutoResize(boolean isAutoResize);

    void setFixedStrategy(boolean isFixedStrategy);

    void focusPopup();

    void focusFirst();
}
