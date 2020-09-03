package ru.protei.portal.core.model.struct;

public class ReplaceNameWithLoginInfo<T> {
    private T object;
    private Boolean replaced;

    public ReplaceNameWithLoginInfo(T object, Boolean replaced) {
        this.object = object;
        this.replaced = replaced;
    }

    public T getObject() {
        return object;
    }

    public Boolean getReplaced() {
        return replaced;
    }
}
