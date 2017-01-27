package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.core.model.struct.ProjectInfo;
import ru.protei.portal.core.model.struct.RegionInfo;

import java.util.List;
import java.util.Map;

/**
 * Сервис управления проектами
 */
public interface ProjectService {

    /**
     * Возвращает проектную информацию по регионам
     * @param query    параметры запроса
     */
    CoreResponse<List<RegionInfo>> listRegions( ProjectQuery query );

    /**
     * Возвращает список проектов сгруппированных по регионам
     * @param query    параметры запроса
     */
    CoreResponse<Map<String, List<ProjectInfo>>> listProjectsByRegions( ProjectQuery query );

    /**
     * Получает информацию о проекте
     * @param id
     */
    CoreResponse<ProjectInfo> getProject( Long id );
}
