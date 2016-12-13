package ru.protei.portal.ui.common.client.common;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.UIObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Используется для фиксации позиционирования элементов при вертикальном скролинге страницы
 */
public class FixedPositioner {

    public FixedPositioner(){
        elemToHandler = new HashMap<>();
    }

    public void watch(UIObject element, int topOffset){
        watch(element, topOffset, element::addStyleName, element::removeStyleName);
    }

    public void watch(Element element, Integer topOffset){
        watch(element, topOffset, element::addClassName, element::removeClassName);
    }

    public void ignore(Object element){
        HandlerRegistration handler = elemToHandler.remove(element);
        if(handler == null)
            return;
        handler.removeHandler();
    }


    private void watch(Object elem, int topOffset, Consumer<String> addClassAction, Consumer<String> removeClassAction){
        positionElement(Window.getScrollTop(), topOffset, addClassAction, removeClassAction);
        HandlerRegistration handler =
                Window.addWindowScrollHandler(
                        event -> positionElement(event.getScrollTop(), topOffset, addClassAction, removeClassAction));
        elemToHandler.put(elem, handler);
    }

    private void positionElement(int topScroll, int topOffset, Consumer<String> addClassAction, Consumer<String> removeClassAction){
        if (topScroll >= topOffset)
            addClassAction.accept("fixed");
        else
            removeClassAction.accept("fixed");
    }

    public static final int NAVBAR_TOP_OFFSET = 50;
    private Map<Object, HandlerRegistration> elemToHandler;
}
