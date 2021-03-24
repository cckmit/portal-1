package ru.protei.portal.core.model.dict;

import ru.protei.portal.core.model.query.BaseQuery;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.ProjectQuery;

import java.util.ArrayList;
import java.util.List;

public enum En_CaseFilterType {
    /**
     * Фильтр по задачам
     */
    CASE_OBJECTS(CaseQuery.class),

    /**
     * Фильтр по затраченному времени
     */
    CASE_TIME_ELAPSED(CaseQuery.class),

    /**
     * Фильтр по времени завершения
     */
    CASE_RESOLUTION_TIME(CaseQuery.class),

    /**
     * Фильтр проектам
     */
    PROJECT(ProjectQuery.class);

    En_CaseFilterType(Class<? extends BaseQuery> queryClass) {
        this.queryClass = queryClass;
    }

    public Class<? extends BaseQuery> getQueryClass() {
        return queryClass;
    }

    public static List<En_CaseFilterType> getTypesByClass(Class<? extends BaseQuery> queryClass) {
        List<En_CaseFilterType> result = new ArrayList<>();

        for (En_CaseFilterType nextType : values()) {
            if (nextType.getQueryClass().equals(queryClass)) {
                result.add(nextType);
            }
        }

        return result;
    }

    private final Class<? extends BaseQuery> queryClass;
}
