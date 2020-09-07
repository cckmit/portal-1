package ru.protei.portal.core.model.struct;

import java.util.HashSet;
import java.util.Set;

public class ReplacementInfo<T> {
    private T object;
    private Set<String> dataSet;

    public ReplacementInfo(T object) {
        this(object, new HashSet<>());
    }

    private ReplacementInfo(T object, Set<String> dataSet) {
        this.object = object;
        this.dataSet = dataSet;
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

    public Set<String> getDataSet() {
        return dataSet == null ? new HashSet<>() : dataSet;
    }

    public void addData(String data) {
        if (dataSet == null) {
            dataSet = new HashSet<>();
        }

        dataSet.add(data);
    }
}
