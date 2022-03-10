package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.query.DataQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcDAO;
import ru.protei.winter.jdbc.JdbcQueryParameters;
import ru.protei.winter.jdbc.JdbcSort;

import java.util.Collection;
import java.util.List;

/**
 * Created by michael on 25.05.16.
 */
public interface PortalBaseDAO<T> extends JdbcDAO<Long,T> {

    /**
     * интерфейс к логике создания выражения where (SqlCondition) по структуре запроса query
     */
    interface QueryConditionBuilder<Q extends DataQuery> {
        SqlCondition buildCondition (Q query);
    }

    /**
     * класс исключения для случая, когда не найдена реализация логики создания условия SqlCondition для типа queryType
     */
    class NoConditionBuilderDefinedException extends RuntimeException {
        public NoConditionBuilderDefinedException (PortalBaseDAO dao, Class<? extends DataQuery> queryType) {
            super("DAO " + dao.getClass().getName() + " has not registered implementation for the query type " + queryType.getName());
        }
    }

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
     * Делает проверку ID записи и выполняет либо обновление (update), либо вставку
     * @param entities
     * @return
     */
    Long saveOrUpdateBatch(Collection<T> entities);

    /**
     * Максимальное значение поля field по заданному условию cond, с ожидаемым типом itemType
     * @param field
     * @param type
     * @param cond
     * @param args
     * @param <V>
     * @return
     */
    <V> V getMaxValue(String field, Class<V> type, String join, String cond, Object... args);

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


    /**
     * Метод регистрирует для типа запроса queryType логигу builder, которая будет использована для построения
     * условия SqlCondition
     * @param queryType
     * @param builder
     */
    <Q extends DataQuery> void registerConditionBuilder (Class<Q> queryType, QueryConditionBuilder<Q> builder);


    /**
     * Метод возвращает SqlCondition для запроса query.
     *
     * Если для query нет подходящей логики формирования SqlCondition, то будет выброшено исключение NoConditionBuilderDefinedException
     *
     * @param query
     * @return
     */
    SqlCondition createSqlCondition (DataQuery query);

    /**
     * Возвращает количество записей для запроса query
     *
     * Реализация получает QueryConditionBuilder
     *
     * @param query
     * @return
     */
    Long count (DataQuery query);

    /**
     * Возвращает количество записей для условия expression и аргументов args
     * @param expression
     * @param args
     * @return
     */
    Long countByExpression(String expression, Object...args);

    /**
     * Возвращает список для запроса query.
     * Реализация конкретного DAO должна содержать метод или набор методов
     * помеченных аннотацией QueryConditionBuilder, принимающих в качестве аргумента конкретный тип Query
     * и возвращающих объект SqlCondition
     * @param query
     * @return
     */
    List<T> listByQuery (DataQuery query);

    /**
     * Возвращает данные и:
     * 1. Общее число строк в базе данных, если offset не установлен и limit установлен
     * 2. Количество возвращаемых данных в любом другом случае
     */
    SearchResult<T> getSearchResultByQuery(DataQuery query);

    /**
     * Возвращает данные и:
     * 1. Общее число строк в базе данных, если offset не установлен и limit установлен
     * 2. Количество возвращаемых данных в любом другом случае
     */
    SearchResult<T> getSearchResult(JdbcQueryParameters parameters);


    <K> List<T> listByColumnIn (String column, Collection<K> values);

    List<Long> keys ();

    <K> List<K> listColumnValue ( String column, Class<K> type);
    <K> List<K> listColumnValue (String column, Class<K> type, String condition, Object...args);
}
