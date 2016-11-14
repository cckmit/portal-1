package ru.protei.portal.core.model.dao;

import ru.protei.winter.jdbc.JdbcDAO;
import ru.protei.winter.jdbc.JdbcSort;

import java.util.List;

/**
 * Created by michael on 25.05.16.
 */
public interface PortalBaseDAO<T> extends JdbcDAO<Long,T> {

    /**
     * @return Возвращает максимальное значение ключа для записей в таблице. если таблица пустая, то null
     */
    Long getMaxId();

    /**
     *
     * @param cond условие
     * @param args аргументы
     * @return максимальное значение ID по указанному условию
     */
    Long getMaxId(String cond, Object... args);

    /**
     * Делает проверку ID записи и выполняет либо обновление (update), либо вставку
     * @param entity
     * @return
     */
    boolean saveOrUpdate(T entity);

    /**
     * Максимальное значение поля field по заданному условию cond, с ожидаемым типом itemType
     * @param field
     * @param type
     * @param cond
     * @param args
     * @param <V>
     * @return
     */
    <V> V getMaxValue(String field, Class<V> type, String cond, Object... args);

    /**
     * возвращает значение ID для объекта
     * @param obj
     * @return
     */
    Long getIdValue(T obj);

    /**
     * сортировка списка в памяти по указанному полю
     * @param entries
     * @param fieldName
     * @param dir
     * @return
     */
    List<T> sortByField(List<T> entries, String fieldName, JdbcSort.Direction dir);

    /**
     * Метод заполняет только поля, которые непосредственно размещены в таблице (без join).
     *
     * @param query
     * @param sort
     * @param args
     * @return
     */
    List<T> plainListByCondition (String query, JdbcSort sort, List<Object> args);
    List<T> plainListByCondition (String query, JdbcSort sort, List<Object> args, int offset, int limit);

    /**
     * Метод делает тоже, что и get, но при этом заполнены будут только поля относящиеся к самой таблицы (без join)
     * @param id
     * @return
     */
    T plainGet (Long id);
}
