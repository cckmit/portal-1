package ru.protei.portal.core.model.youtrack;

import java.util.Objects;

public class YtFieldDescriptor {

    public Class<?> clazz;
    public String fieldName;

    public YtFieldDescriptor(Class<?> clazz, String fieldName) {
        this.clazz = clazz;
        this.fieldName = fieldName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        YtFieldDescriptor that = (YtFieldDescriptor) o;
        return Objects.equals(clazz, that.clazz) &&
                Objects.equals(fieldName, that.fieldName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz, fieldName);
    }
}
