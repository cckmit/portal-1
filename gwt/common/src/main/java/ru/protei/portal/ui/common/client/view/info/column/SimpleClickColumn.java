package ru.protei.portal.ui.common.client.view.info.column;

import com.google.gwt.user.client.Element;
import ru.protei.portal.ui.common.client.columns.ClickColumn;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SimpleClickColumn<T> extends ClickColumn<T> {
    public SimpleClickColumn<T> withColumnHeaderConsumer(Consumer<Element> columnHeaderConsumer) {
        this.columnHeaderConsumer = columnHeaderConsumer;
        return this;
    }

    public SimpleClickColumn<T> withColumnValueConsumer(BiConsumer<Element, T> columnValueConsumer) {
        this.columnValueConsumer = columnValueConsumer;
        return this;
    }

    public SimpleClickColumn<T> withClassName(String className) {
        this.className = className;
        return this;
    }

    @Override
    protected void fillColumnHeader(Element columnHeader) {
        if (columnHeaderConsumer != null) {
            columnHeaderConsumer.accept(columnHeader);
        }

        columnHeader.addClassName(className);
    }

    @Override
    protected void fillColumnValue(Element cell, T value) {
        if (columnValueConsumer != null) {
            columnValueConsumer.accept(cell, value);
        }

        cell.addClassName(className);
    }

    private BiConsumer<Element, T> columnValueConsumer;
    private Consumer<Element> columnHeaderConsumer;
    private String className = "";
}
