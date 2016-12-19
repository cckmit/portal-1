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
        elemsToTopOffsets.put(element, topOffset);
        positionElement(element, Window.getScrollTop(), topOffset);

        if(windowScrollHandler == null)
            windowScrollHandler = bindWindowScroll();
    }

    public void ignore(UIObject uiObject){
        ignore(uiObject.getElement());
    }

    public void ignore(Element element){
        elemsToTopOffsets.remove(element);
        element.removeClassName(FIX_CLASS_NAME);

        if(elemsToTopOffsets.isEmpty() && windowScrollHandler != null){
            windowScrollHandler.removeHandler();
            windowScrollHandler = null;
        }
    }

    private void positionElement(Element element, int topScroll, int topOffset){
        if (topScroll >= topOffset)
            element.addClassName(FIX_CLASS_NAME);
        else
            element.removeClassName(FIX_CLASS_NAME);
    }

    private HandlerRegistration bindWindowScroll(){
        return
            Window.addWindowScrollHandler(
                event -> elemsToTopOffsets.forEach(
                        (element, topOffset) -> positionElement(element, event.getScrollTop(), topOffset)));
    }

    public static final int NAVBAR_TOP_OFFSET = 50;
    private Map<Element, Integer> elemsToTopOffsets = new HashMap<>();
    private HandlerRegistration windowScrollHandler;
    private final String FIX_CLASS_NAME = "fixed";
}
