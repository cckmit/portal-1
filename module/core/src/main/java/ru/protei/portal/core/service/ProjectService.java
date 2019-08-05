package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
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
    @Privileged( En_Privilege.REGION_VIEW )
    CoreResponse<List<RegionInfo>> listRegions( AuthToken token, ProjectQuery query );

    /**
     * Возвращает список проектов сгруппированных по регионам
     * @param query    параметры запроса
     */
    @Privileged({ En_Privilege.PROJECT_VIEW, En_Privilege.REGION_VIEW })
    CoreResponse<Map<String, List<ProjectInfo>>> listProjectsByRegions( AuthToken token, ProjectQuery query );

    /**
     * Получает информацию о проекте
     * @param id
     */
    @Privileged( En_Privilege.PROJECT_VIEW )
    CoreResponse<ProjectInfo> getProject( AuthToken token, Long id );

    /**
     * Изменяем проект
     * @param project    проект
     */
    @Privileged( En_Privilege.PROJECT_EDIT )
    @Auditable( En_AuditType.PROJECT_MODIFY )
    CoreResponse<ProjectInfo> saveProject( AuthToken token, ProjectInfo project );

    /**
     * Создает новый проект
     * @param project    проект
     */
    @Privileged(En_Privilege.PROJECT_CREATE)
    @Auditable(En_AuditType.PROJECT_CREATE)
    CoreResponse<ProjectInfo> createProject(AuthToken token, ProjectInfo project);

    /**
     * Создает новый проект
     * @param creatorId
     */
    @Privileged( En_Privilege.PROJECT_CREATE )
    CoreResponse<Long> createProject( AuthToken token, Long creatorId );

    @Privileged( En_Privilege.PROJECT_REMOVE )
    @Auditable( En_AuditType.PROJECT_REMOVE )
    CoreResponse<Boolean> removeProject( AuthToken token, Long projectId );

    CoreResponse<List<ProjectInfo>> listProjects( AuthToken token, ProjectQuery query );
}
