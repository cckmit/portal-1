package ru.protei.portal.ui.common.client.common;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.UIObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Используется для фиксации позиционирования элементов при вертикальном скролинге страницы
 */
public class FixedPositioner {

    public void watch(UIObject uiObject, int topOffset){
        watch(uiObject.getElement(), topOffset);
    }

    public void watch(Element element, int topOffset){
        positionElement(element, Window.getScrollTop(), topOffset);
        HandlerRegistration handler =
                Window.addWindowScrollHandler(
                        event -> positionElement(element, event.getScrollTop(), topOffset));
        elemToHandler.put(element, handler);
    }

    public void ignore(UIObject uiObject){
        ignore(uiObject.getElement());
    }

    public void ignore(Element element){
        HandlerRegistration handler = elemToHandler.remove(element);
        if(handler == null)
            return;
        element.removeClassName(FIX_CLASS_NAME);
        handler.removeHandler();
    }

    private void positionElement(Element element, int topScroll, int topOffset){
        if (topScroll >= topOffset)
            element.addClassName(FIX_CLASS_NAME);
        else
            element.removeClassName(FIX_CLASS_NAME);
    }

    public static final int NAVBAR_TOP_OFFSET = 50;
    private Map<Element, HandlerRegistration> elemToHandler = new HashMap<>();
    private final String FIX_CLASS_NAME = "fixed";
}
