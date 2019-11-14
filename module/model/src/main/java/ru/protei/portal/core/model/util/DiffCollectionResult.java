package ru.protei.portal.core.model.util;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * результат сравнения двух коллекций (и map в том числе)
 * @param <T> тип элементов, хранящихся в сравниваемых коллекциях
 */
public class DiffCollectionResult<T> implements Serializable {

    /**
     * возвращает список добавленных узлов
     */
    public List<T> getAddedEntries() {
        return addedEntries;
    }

    /**
     * возвращает список удаленных узлов
     */
    public List<T> getRemovedEntries() {
        return removedEntries;
    }

    /**
     * возвращает список изменившихся узлов
     */
    public List<DiffResult<T>> getChangedEntries() {
        return changedEntries;
    }

    /**
     * возвращает список всех одинаковых узлов
     */
    public List<T> getSameEntries() {
        return sameEntries;
    }

    /**
     * включить в результат добавленный узел
     *
     * @param entry добавленный узел
     */
    public void putAddedEntry(T entry) {
        if (addedEntries == null) {
            addedEntries = new ArrayList<T>();
        }
        addedEntries.add(entry);
    }

    public void putAddedEntries(Collection<T> entries) {
        if (entries == null) return;
        if (addedEntries == null) {
            addedEntries = new ArrayList<T>();
        }
        addedEntries.addAll(entries);
    }

    /**
     * включить в результат удаленный узел
     *
     * @param entry удаленный узел
     */
    public void putRemovedEntry(T entry) {
        if (removedEntries == null) {
            removedEntries = new ArrayList<T>();
        }
        removedEntries.add(entry);
    }

    public void putRemovedEntries(Collection<T> entries) {
        if (entries == null) return;
        if (removedEntries == null) {
            removedEntries = new ArrayList<T>();
        }
        removedEntries.addAll(entries);
    }

    /**
     * включить в результат измененный узел
     */
    public void putChangedEntry(T initialState, T newState) {
        if (changedEntries == null) {
            changedEntries = new ArrayList<DiffResult<T>>();
        }
        DiffResult<T> diffResult = new DiffResult<>();
        diffResult.setInitialState( initialState );
        diffResult.setNewState( newState );
        changedEntries.add(diffResult);
    }

    public void putChangedEntries(Collection<DiffResult<T>> entries) {
        if (entries == null) return;
        if (changedEntries == null) {
            changedEntries = new ArrayList<DiffResult<T>>();
        }
        changedEntries.addAll(entries);
    }

    /**
     * включить в результат одинаковый в обоих коллекция узел
     *
     * @param entry одинаковый в обоих коллекция узел
     */
    public void putSameEntry(T entry) {
        if (sameEntries == null) {
            sameEntries = new ArrayList<T>();
        }
        sameEntries.add(entry);
    }

    public void putSameEntries( Collection<T> entries) {
        if (entries == null) return;
        if (sameEntries == null) {
            sameEntries = new ArrayList<T>();
        }
        sameEntries.addAll(entries);
    }

    public boolean hasDifferences() {
        return hasAdded() || hasRemovedEntries() || hasChanged();
    }

    public boolean hasSameEntries() {
        return !isEmpty(sameEntries);
    }

    public boolean hasRemovedEntries() {return !isEmpty(removedEntries); }

    public boolean hasChanged() {
        return !isEmpty(changedEntries);
    }

    public boolean hasAdded() {
        return !isEmpty(addedEntries);
    }

    private boolean isEmpty( Collection entries ) {
        return entries == null || entries.isEmpty();
    }

    /**
     * удаленные узлы
     */
    private List<T> removedEntries;

    /**
     * добавленные узлы
     */
    private List<T> addedEntries;

    /**
     * не измененные узлы
     */
    private List<T> sameEntries;

    /**
     * измененные узлы
     */
    private List<DiffResult<T>> changedEntries;

}