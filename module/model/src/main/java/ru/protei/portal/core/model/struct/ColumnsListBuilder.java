package ru.protei.portal.core.model.struct;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ColumnsListBuilder<T> {
    private List<T> list = new ArrayList<>();

    public ColumnsListBuilder<T> add(T element) {
        list.add(element);
        return this;
    }

    public ColumnsListBuilder<T> addIf(T element, boolean condition) {
        if (condition) {
            list.add(element);
        }

        return this;
    }

    public List<T> build() {
        return Collections.unmodifiableList(list);
    }
}
