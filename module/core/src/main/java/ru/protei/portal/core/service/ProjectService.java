package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.core.model.struct.ProjectInfo;
import ru.protei.portal.core.model.struct.RegionInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Сервис управления проектами
 */
public interface ProjectService {

    /**
     * Возвращает проектную информацию по регионам
     * @param query    параметры запроса
     * @param roles
     */
    CoreResponse<List<RegionInfo>> listRegions( ProjectQuery query, Set< UserRole > roles );

    /**
     * Возвращает список проектов сгруппированных по регионам
     * @param query    параметры запроса
     * @param roles
     */
    CoreResponse<Map<String, List<ProjectInfo>>> listProjectsByRegions( ProjectQuery query, Set< UserRole > roles );

    /**
     * Получает информацию о проекте
     * @param id
     * @param roles
     */
    CoreResponse<ProjectInfo> getProject( Long id, Set< UserRole > roles );

    /**
     * Изменяем проект
     * @param roles
     * @param project    проект
     */
    CoreResponse saveProject( ProjectInfo project, Set< UserRole > roles );

    /**
     * Создает новый проект
     * @param creatorId
     * @param roles
     */
    CoreResponse<Long> createProject( Long creatorId, Set< UserRole > roles );
}
