package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.core.model.struct.RegionInfo;

import java.util.List;

/**
 * Сервис управления проектами
 */
public interface ProjectService {

    /**
     * Возвращает проектную информацию по регионам
     * @param query    параметры запроса
     */
    CoreResponse<List<RegionInfo>> listByRegions( ProjectQuery query );
}
