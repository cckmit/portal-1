package ru.protei.portal.core.model.util;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * результат сравнения двух коллекций (и map в том числе)
 * @param <T> тип элементов, хранящихся в сравниваемых коллекциях
 */
public class DiffCollectionResult<T> implements Serializable {

    public static <T> DiffCollectionResult<T> from( ru.protei.winter.core.utils.collections.DiffCollectionResult<T> diffResult ) {
        DiffCollectionResult<T> result = new DiffCollectionResult<>();
        result.addedEntries = diffResult.getAddedEntries();
        result.removedEntries = diffResult.getRemovedEntries();
        result.changedEntries = diffResult.getChangedEntries();
        result.allDiffEntries = diffResult.getAllDiffEntries();

        return result;
    }

    /**
     * возвращает список добавленных узлов
     *
     * @return список добавленных узлов
     */
    public List<T> getAddedEntries() {
        return addedEntries;
    }

    /**
     * возвращает список измененных узлов
     *
     * @return список измененных узлов
     */
    public List<T> getChangedEntries() {
        return changedEntries;
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

    /**
     * включить в результат измененный узел
     *
     * @param entry измененный узел
     */
    public void putChangedEntry(T entry) {
        if (changedEntries == null) {
            changedEntries = new ArrayList<T>();
        }
        changedEntries.add(entry);
        allDiffEntries.add(entry);
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

    /**
     * удаленные узлы
     */
    private List<T> removedEntries;

    /**
     * добавленные узлы
     */
    private List<T> addedEntries;

    /**
     * измененные узлы
     */
    private List<T> changedEntries;

    /**
     * все различающиеся узлы
     */
    private List<T> allDiffEntries = new ArrayList<T>();
}