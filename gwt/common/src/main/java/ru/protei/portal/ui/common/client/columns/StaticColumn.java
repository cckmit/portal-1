package ru.protei.portal.ui.common.client.columns;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import ru.brainworm.factory.widget.table.client.ColumnHeader;
import ru.brainworm.factory.widget.table.client.ColumnValue;

public abstract class StaticColumn<T> {

    public ColumnHeader header = new ColumnHeader() {
        @Override
        public void handleEvent(Event event) {}

        @Override
        public void fillHeader(Element columnHeader) {
            fillColumnHeader(columnHeader);
        }
    };

    public ColumnValue<T> values = new ColumnValue<T>() {
        @Override
        public void handleEvent(Event event, T value) {}

        @Override
        public void fillValue(Element cell, T value) {
            fillColumnValue(cell, value);
        }
    };

    protected abstract void fillColumnHeader(Element columnHeader);
    public abstract void fillColumnValue(Element cell, T value);
}
