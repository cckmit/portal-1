package ru.protei.portal.core.model.struct;

import ru.protei.portal.core.model.ent.UserLoginShortView;

import java.util.HashSet;
import java.util.Set;

public class ReplaceLoginWithUsernameInfo<T> {
    private T object;
    private Set<UserLoginShortView> userLoginShortViews;

    public ReplaceLoginWithUsernameInfo(T object) {
        this(object, new HashSet<>());
    }

    public ReplaceLoginWithUsernameInfo(T object, Set<UserLoginShortView> userLoginShortViews) {
        this.object = object;
        this.userLoginShortViews = userLoginShortViews;
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

    public Set<UserLoginShortView> getUserLoginShortViews() {
        return userLoginShortViews == null ? new HashSet<>() : userLoginShortViews;
    }

    public void addData(UserLoginShortView data) {
        if (userLoginShortViews == null) {
            userLoginShortViews = new HashSet<>();
        }

        userLoginShortViews.add(data);
    }
}
