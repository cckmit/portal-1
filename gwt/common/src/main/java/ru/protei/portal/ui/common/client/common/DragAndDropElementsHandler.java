package ru.protei.portal.ui.common.client.common;

import com.google.gwt.event.dom.client.DomEvent;
import ru.protei.portal.ui.common.client.events.Draggable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.POINTER_EVENTS_NONE;

public class DragAndDropElementsHandler<T> {
    public void addDraggableElement(T value, Draggable draggableElement) {
        draggableElement.addDragStartHandler(event -> dragStartHandler(value));
        draggableElement.addDragOverHandler(DomEvent::preventDefault);
        draggableElement.addDropHandler(event -> dropHandler(value));
        draggableElement.addDragEndHandler(event -> dragEndHandler());

        valueToDraggableElement.put(value, draggableElement);
    }

    public void addDropConsumer(BiConsumer<T, T> dropConsumer) {
        this.dropConsumer = dropConsumer;
    }

    private void dragStartHandler(T value) {
        removeElementEvents(valueToDraggableElement.values());
        src = value;
    }

    private void dragEndHandler() {
        recoverElementEvents(valueToDraggableElement.values());
    }

    private void dropHandler(T dst) {
        dropConsumer.accept(src, dst);
    }

//    pointer-events: none предотвращает вызов dragLeave при пересечении границ дочерних элементов контейнера
    private void removeElementEvents(Collection<Draggable> elements) {
        elements.forEach(element -> element.getInnerContainer().addClassName(POINTER_EVENTS_NONE));
    }

    private void recoverElementEvents(Collection<Draggable> elements) {
        elements.forEach(element -> element.getInnerContainer().removeClassName(POINTER_EVENTS_NONE));
    }

    private T src;

    private BiConsumer<T, T> dropConsumer = (src, dst) -> {};
    private Map<T, Draggable> valueToDraggableElement = new HashMap<>();
}
