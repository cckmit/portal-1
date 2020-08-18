package ru.protei.portal.ui.common.client.common;

import com.google.gwt.event.dom.client.DomEvent;
import ru.protei.portal.ui.common.client.events.Draggable;

import java.util.function.BiConsumer;

public class DragAndDropElementsHandler<T> {
    public void addDraggableElement(T value, Draggable draggableElement) {
        draggableElement.addDragStartHandler(event -> dragStartHandler(value));
        draggableElement.addDragOverHandler(DomEvent::preventDefault);
        draggableElement.addDropHandler(event -> dropHandler(value));
    }

    public void addDropConsumer(BiConsumer<T, T> dropConsumer) {
        this.dropConsumer = dropConsumer;
    }

    private void dragStartHandler(T value) {
        src = value;
    }

    private void dropHandler(T dst) {
        dropConsumer.accept(src, dst);
    }

    private T src;

    private BiConsumer<T, T> dropConsumer = (src, dst) -> {};
}
