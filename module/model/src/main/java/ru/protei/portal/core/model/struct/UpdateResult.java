package ru.protei.portal.core.model.struct;

public class UpdateResult<T> {

    private T object;
    private boolean isUpdated;

    public UpdateResult() {}

    public UpdateResult(T object, boolean isUpdated) {
        this.object = object;
        this.isUpdated = isUpdated;
    }

    public T getObject() {
        return object;
    }

    public boolean isUpdated() {
        return isUpdated;
    }

    @Override
    public String toString() {
        return "UpdateResult{" +
                "object=" + object +
                ", isUpdated=" + isUpdated +
                '}';
    }
}
