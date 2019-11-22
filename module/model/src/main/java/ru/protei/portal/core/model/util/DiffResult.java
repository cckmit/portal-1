package ru.protei.portal.core.model.util;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;


/**
 * результат сравнения двух объектов
 * @param <T> тип объектов
 */
public class DiffResult<T> implements Serializable {


    public void setInitialState( T initialState ) {
        this.initialState = initialState;
    }

    public void setNewState( T newState ) {
        this.newState = newState;
    }

    public T getInitialState() {
        return initialState;
    }

    public T getNewState() {
        return newState;
    }

    public boolean hasInitialState() {
        return initialState != null;
    }

    public boolean hasNewState() {
        return initialState != null;
    }

    public boolean hasChanged() {
        return !Objects.equals(initialState, newState);
    }

    private T initialState;
    private T newState;

    @Override
    public boolean equals( Object o ) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DiffResult<?> that = (DiffResult<?>) o;
        return Objects.equals( initialState, that.initialState ) &&
                Objects.equals( newState, that.newState );
    }

    @Override
    public int hashCode() {
        return Objects.hash( initialState, newState );
    }

    @Override
    public String toString() {
        return "DiffResult{" +
                "initialState=" + initialState +
                ", newState=" + newState +
                '}';
    }
}