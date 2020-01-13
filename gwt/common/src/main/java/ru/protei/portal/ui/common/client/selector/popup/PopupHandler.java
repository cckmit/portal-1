package ru.protei.portal.ui.common.client.selector.popup;

import ru.protei.portal.ui.common.client.selector.SelectorPopup;

public interface PopupHandler  {

    void onPopupUnload( SelectorPopup selectorPopup );

    void onEndOfScroll();

}
