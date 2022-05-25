package ru.protei.portal.core.service;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.event.ProjectPauseTimeHasComeEvent;
import ru.protei.portal.core.event.SchedulePauseTimeOnStartupEvent;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dto.Project;
import ru.protei.portal.core.model.dto.ProjectInfo;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonProjectMemberView;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static ru.protei.portal.config.MainConfiguration.BACKGROUND_TASKS;

/**
 * Сервис управления проектами
 */
public interface ProjectService {

    @Privileged(En_Privilege.PROJECT_VIEW)
    Result<SearchResult<Project>> projects(AuthToken token, ProjectQuery query);

    Result<List<EntityOption>> listOptionProjects(AuthToken token, ProjectQuery query);

    Result<List<ProjectInfo>> listInfoProjects(AuthToken token, ProjectQuery query);

    /**
     * Получает информацию о проекте
     * @param id
     */
    @Privileged(En_Privilege.PROJECT_VIEW)
    Result<Project> getProject(AuthToken token, Long id);

    /**
     * Возвращает базовую информацию о проекте:
     * id, name, manager, contragent, productDirection, local
     * @param id
     */
    Result<ProjectInfo> getProjectInfo(AuthToken token, Long id);

    /**
     * Изменяем проект
     * @param project    проект
     */
    @Privileged(En_Privilege.PROJECT_EDIT)
    @Auditable(En_AuditType.PROJECT_MODIFY)
    Result<Project> saveProject(AuthToken token, Project project);

    /**
     * Создает новый проект
     * @param project проект
     */
    @Privileged(En_Privilege.PROJECT_CREATE)
    @Auditable(En_AuditType.PROJECT_CREATE)
    Result<Project> createProject(AuthToken token, Project project);

    @Privileged(En_Privilege.PROJECT_REMOVE)
    @Auditable(En_AuditType.PROJECT_REMOVE)
    Result<Long> removeProject(AuthToken token, Long projectId);

    @EventListener
    @Async(BACKGROUND_TASKS)
    void schedulePauseTimeNotificationsOnPortalStartup( SchedulePauseTimeOnStartupEvent event );

    @EventListener
    @Async(BACKGROUND_TASKS)
    void onPauseTimeNotification( ProjectPauseTimeHasComeEvent event );

    @Privileged(En_Privilege.PROJECT_VIEW)
    Result<List<PersonProjectMemberView>> getProjectTeam(AuthToken token, Long projectId);

    Result<PersonShortView> getProjectLeader(AuthToken authToken, Long projectId);

    Result<Boolean> notifyExpiringProjectTechnicalSupportValidity(LocalDate now);
}
