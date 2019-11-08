package ru.protei.portal.core.model.util;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.apache.logging.log4j.ThreadContext.isEmpty;

/**
 * результат сравнения двух коллекций (и map в том числе)
 * @param <T> тип элементов, хранящихся в сравниваемых коллекциях
 */
public class DiffCollectionResult<T> implements Serializable {

    /**
     * возвращает список добавленных узлов
     *
     * @return список добавленных узлов
     */
    public List<T> getAddedEntries() {
        return addedEntries;
    }

    /**
     * возвращает список удаленных узлов
     *
     * @return спсок удаленных узлов
     */
    public List<T> getRemovedEntries() {
        return removedEntries;
    }

    /**
     * возвращает список всех изменившихся узлов
     *
     * @return список всех изменившихся узлов
     */
    public List<T> getAllDiffEntries() {
        return allDiffEntries;
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
        allDiffEntries.add(entry);
    }

    public void putAddedEntries(Collection<T> entries) {
        if (entries == null) return;
        if (addedEntries == null) {
            addedEntries = new ArrayList<T>();
        }
        addedEntries.addAll(entries);
        allDiffEntries.addAll(entries);
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
        allDiffEntries.add(entry);
    }

    public void putRemovedEntries(Collection<T> entries) {
        if (entries == null) return;
        if (removedEntries == null) {
            removedEntries = new ArrayList<T>();
        }
        removedEntries.addAll(entries);
        allDiffEntries.addAll(entries);
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
        return !allDiffEntries.isEmpty();
    }

    public boolean hasSameEntries() {
        return !isEmpty(sameEntries);
    }

    private boolean isEmpty( List<T> entries ) {
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
     * все различающиеся узлы
     */
    private List<T> allDiffEntries = new ArrayList<T>();
}