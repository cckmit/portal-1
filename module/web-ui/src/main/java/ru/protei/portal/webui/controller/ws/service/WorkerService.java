package ru.protei.portal.webui.controller.ws.service;

import ru.protei.portal.webui.controller.ws.model.DepartmentRecord;
import ru.protei.portal.webui.controller.ws.model.Photo;
import ru.protei.portal.webui.controller.ws.model.ServiceResult;
import ru.protei.portal.webui.controller.ws.model.WorkerRecord;

import javax.jws.WebService;
import java.util.List;

/**
 * Portal-API Web-Сервис
 *
 * Created by turik on 18.08.16.
 */
@WebService
public interface WorkerService {

    /**
     * запрос записи сотрудника по идентификатору
     */
    public WorkerRecord getWorker(Long id);

    /**
     * получить список записей сотрудников по выражению поиска expr
     *
     * Если expr равен NULL или пустой строке, то возвращаются все записи, иначе
     * expr используется как аргумент оператора like для полей ФИО. Символ '%'
     * добавляется в конец строки expr автоматически.
     *
     * Данный запрос исключает записи помеченные в базе как "удалена"
     *
     */
    public List<WorkerRecord> getWorkers(String expr);

    /**
     * Добавление записи сотрудника
     */
    public ServiceResult addWorker(WorkerRecord rec);

    /**
     * Обновление записи сотрудника. Атрибут "id" должен быть задан на
     * вызывающей стороне
     */
    public ServiceResult updateWorker(WorkerRecord rec);

    public List<ServiceResult> updateWorkers(List<WorkerRecord> list);

    /**
     * Удаление сотрудника по идентификатору id. При этом, запись фактически не
     * удаляется из базы, а только помечается как удаленная
     */
    public ServiceResult deleteWorker(Long id);

    /**
     * Обновление фотографии сотрудника по идентификатору id.
     */
    public ServiceResult updatePhoto(Long id, byte[] buf);

    /**
     * получить список фотографий по списку id сотрудников
     */
    public List<Photo> getPhotos(List<Long> list);

    /**
     * Добавление записи подразделения
     */
    public ServiceResult addDepartment(DepartmentRecord rec);

    /**
     * Обновление записи подразделения. Атрибут "id" должен быть задан на
     * вызывающей стороне
     */
    public ServiceResult updateDepartment(DepartmentRecord rec);

    /**
     * Удаление подразделения по идентификатору id.
     */
    public ServiceResult deleteDepartment(Long id);

}
