package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.core.model.struct.Project;
import ru.protei.portal.core.model.struct.RegionInfo;
import ru.protei.portal.core.model.view.EntityOption;

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
    Result<List<RegionInfo>> listRegions(AuthToken token, ProjectQuery query );

    /**
     * Возвращает список проектов сгруппированных по регионам
     * @param query    параметры запроса
     */
    @Privileged({ En_Privilege.PROJECT_VIEW, En_Privilege.REGION_VIEW })
    Result<Map<String, List<Project>>> listProjectsByRegions(AuthToken token, ProjectQuery query );

    /**
     * Получает информацию о проекте
     * @param id
     */
    @Privileged( En_Privilege.PROJECT_VIEW )
    Result<Project> getProject(AuthToken token, Long id );

    /**
     * Возвращает базовую информацию о проекте:
     * id, name, manager, contragent, productDirection, local
     * @param id
     */
    @Privileged( En_Privilege.PROJECT_VIEW )
    Result<Project> getProjectBaseInfo(AuthToken token, Long id);

    /**
     * Изменяем проект
     * @param project    проект
     */
    @Privileged( En_Privilege.PROJECT_EDIT )
    @Auditable( En_AuditType.PROJECT_MODIFY )
    Result<Project> saveProject(AuthToken token, Project project );

    /**
     * Создает новый проект
     * @param project    проект
     */
    @Privileged(En_Privilege.PROJECT_CREATE)
    @Auditable(En_AuditType.PROJECT_CREATE)
    Result<Project> createProject(AuthToken token, Project project);

    /**
     * Создает новый проект
     * @param creatorId
     */
    @Privileged( En_Privilege.PROJECT_CREATE )
    Result<Long> createProject( AuthToken token, Long creatorId );

    @Privileged( En_Privilege.PROJECT_REMOVE )
    @Auditable( En_AuditType.PROJECT_REMOVE )
    Result<Boolean> removeProject( AuthToken token, Long projectId );

    Result<List<Project>> listProjects(AuthToken token, ProjectQuery query );

    Result<List<EntityOption>> listFreeProjectsAsEntityOptions(AuthToken authToken);
}
