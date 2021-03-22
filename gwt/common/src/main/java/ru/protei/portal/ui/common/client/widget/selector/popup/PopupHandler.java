package ru.protei.portal.ui.common.client.widget.selector.popup;

import ru.protei.portal.ui.common.client.selector.SelectorPopup;

public interface PopupHandler  {

    void onPopupHide(SelectorPopup selectorPopup );

    void onEndOfScroll();

}
