package ru.protei.portal.api.service;

import ru.protei.portal.api.model.DepartmentRecord;
import ru.protei.portal.api.model.Photo;
import ru.protei.portal.api.model.ServiceResult;
import ru.protei.portal.api.model.WorkerRecord;

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
     * запрос записи сотрудника по идентификатору id
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
     * Обновление записи сотрудника.
     */
    public ServiceResult updateWorker(WorkerRecord rec);

    /**
     * Обновление записей сотрудников.
     */
    public List<ServiceResult> updateWorkers(List<WorkerRecord> list);

    /**
     * Удаление сотрудника по идентификатору ext_id.
     */
    public ServiceResult deleteWorker(WorkerRecord rec);

    /**
     * Обновление фотографии сотрудника по идентификатору id.
     */
    public ServiceResult updatePhoto(Long id, byte[] buf);

    /**
     * получить список фотографий по списку id сотрудников
     */
    public List<Photo> getPhotos(List<Long> list);

    /**
     * Создание/обновление записи подразделения.
     */
    public ServiceResult updateDepartment(DepartmentRecord rec);

    /**
     * Удаление подразделения по идентификатору ext_id.
     */
    public ServiceResult deleteDepartment(DepartmentRecord rec);

}
