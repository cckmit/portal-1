package ru.protei.portal.api.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.*;
import protei.sql.query.Tm_SqlQueryHelper;
import ru.protei.portal.api.config.WSConfig;
import ru.protei.portal.api.model.*;
import ru.protei.portal.api.tools.migrate.WSMigrationManager;
import ru.protei.portal.api.utils.HelperService;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.net.Inet4Address;
import java.net.URLDecoder;
import java.text.ParseException;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

@RestController
@RequestMapping(value = "/api/worker", headers = "Accept=application/xml")
public class WorkerController {

    private static Logger logger = Logger.getLogger(WorkerController.class);

    @Autowired
    private PersonDAO personDAO;

    @Autowired
    private CompanyGroupHomeDAO companyGroupHomeDAO;

    @Autowired
    private CompanyDepartmentDAO companyDepartmentDAO;

    @Autowired
    private WorkerPositionDAO workerPositionDAO;

    @Autowired
    private WorkerEntryDAO workerEntryDAO;

    @Autowired
    TransactionTemplate transactionTemplate;

    @Autowired
    WSMigrationManager migrationManager;

    @RequestMapping(method = RequestMethod.GET, value = "/get.person")
    public @ResponseBody WorkerRecord getPerson(@RequestParam(name = "id") Long id) {

        /**
         * @review нужно объединить в один вызов logger.debug, не надо выделять сообщения строками вида "==="
         * для логирования вызовов функций сервиса можно вообще написать аспект
         */
        logger.debug("=== getPerson ===");

        /**
         * @review Юля, использование log4j буду запрещать на уровне приказа по отделу,
         * используйте slf4j в качестве front-end api и log4j или logback в качестве основы,
         * тогда код отладочного вывода становится и читабельнее, и эффективнее с точки зрения производительности
         */
        logger.debug("id = " + id);

        try {

            /**
             * @review слишком много проверочного кода, который бесполезен.
             * ok, пусть id будет null, зачем нужна проверка и как она повлияет на конечный результат?
             * никак. будет либо NPE, либо dao точно также вернет null в качестве результата.
             *
             */
            if (id != null) {
                Person person = personDAO.get (id);

                /**
                 * @review тоже самое здесь. если person будет Null, то будет NPE и функция вернет null
                 * зачем тогда нужна проверка?
                 *
                 */
                if (person != null) {
                    return new WorkerRecord(person);
                }
            }
        } catch (Exception e) {
            logger.error ("error while get worker", e);
        }
        return null;
        /**
         * @review вот мое видение, как нужно реализовать данный метод:
         *
         * logger.debug ("get person by id {}", id)
         * try {
         *     return new WorkerRecord (personDAO.get(id));
         * }
         * catch (Throwable e) {
         *     logger.error ("...", e);
         * }
         * return null;
         */
    }

    @RequestMapping(method = RequestMethod.GET, value = "/get.worker")
    public @ResponseBody WorkerRecord getWorker(@RequestParam(name = "id") String id, @RequestParam(name = "companyCode") String companyCode) {

        // @review далее, я уже не буду повторять про одно и тоже
        logger.debug("=== getWorker ===");

        try {

            String companyDecode = URLDecoder.decode(companyCode, "UTF-8");
            logger.debug("companyCode = " + companyDecode);

            String idDecode = URLDecoder.decode(id, "UTF-8");
            logger.debug("id = " + id);

            /**
             * если в @RequestParam обозначить параметр как обязательный, то проверка ниже не требуется
             */
            if (id != null && HelperFunc.isNotEmpty(companyDecode)) {
                CompanyHomeGroupItem item = companyGroupHomeDAO.getByExternalCode(companyDecode.trim());

                /** Далее код эквивалентен:
                 *
                 * return item == null ? null : new WorkerRecord (workerEntryDAO.getByExternalId(idDecode.trim(), item.getCompanyId()));
                 */
                if (item != null) {
                    WorkerEntry worker = workerEntryDAO.getByExternalId(idDecode.trim(), item.getCompanyId());
                    if (worker != null) {
                        return new WorkerRecord(worker);
                    }
                }
            }
        } catch (Exception e) {
            logger.error ("error while get worker", e);
        }
        return null;
    }


    private <R> R withHomeCompany (String id, String companyCode, BiFunction<String,CompanyHomeGroupItem, R> func) throws Exception {
        String companyDecode = URLDecoder.decode(companyCode, "UTF-8").trim();
        logger.debug("companyCode = " + companyDecode);

        String idDecode = URLDecoder.decode(id, "UTF-8").trim();
        logger.debug("id = " + id);

        if (id != null && HelperFunc.isNotEmpty(companyDecode)) {
            CompanyHomeGroupItem item = companyGroupHomeDAO.getByExternalCode(companyDecode.trim());
            return item == null ? null : func.apply(id, item);
        }

        return null;
    }


    @RequestMapping(method = RequestMethod.GET, value = "/get.department")
    public @ResponseBody DepartmentRecord getDepartment(@RequestParam(name = "id") String id, @RequestParam(name = "companyCode") String companyCode) {

        logger.debug("=== getDepartment ===");

        try {

            /**
             * @review обрати внимание, что код этого метода практически дублирует код предыдущего
             * я добавляю метод withHomeCompany (см. выше), после чего твой код транслируется в это:
             *
             * return withHomeCompany(id, companyCode,
             *      (recid,item) -> new DepartmentRecord(companyDepartmentDAO.getByExternalId(recid, item.getCompanyId()))
             * );
             *
             * не правда ли компактнее?
             */


            String companyDecode = URLDecoder.decode(companyCode, "UTF-8");
            logger.debug("companyCode = " + companyDecode);

            String idDecode = URLDecoder.decode(id, "UTF-8");
            logger.debug("id = " + id);

            if (id != null && HelperFunc.isNotEmpty(companyDecode)) {
                CompanyHomeGroupItem item = companyGroupHomeDAO.getByExternalCode(companyDecode.trim());
                if (item != null) {
                    CompanyDepartment department = companyDepartmentDAO.getByExternalId(idDecode.trim(), item.getCompanyId());
                    if (department != null) {
                        return new DepartmentRecord(department);
                    }
                }
            }
        } catch (Exception e) {
            logger.error ("error while get worker", e);
        }

        return null;
    }

/*
    @RequestMapping(method = RequestMethod.GET, value = "/get.result")
    public @ResponseBody ServiceResult getServiceResult() {

        return ServiceResult.failResult (En_ErrorCode.NOT_CREATE.getCode (), En_ErrorCode.NOT_CREATE.getMessage (), 0L);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/get.results")
    public @ResponseBody ServiceResultList getServiceResultList() {

        ServiceResultList serviceResultList = new ServiceResultList();
        serviceResultList.getServiceResults().add(ServiceResult.failResult (En_ErrorCode.NOT_CREATE.getCode (), En_ErrorCode.NOT_CREATE.getMessage (), 0L));
        serviceResultList.getServiceResults().add(ServiceResult.successResult(1L));
        return serviceResultList;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/get.photo")
    public @ResponseBody Photo getPhoto() {

        Photo photo = new Photo();
        photo.setId(2L);
        byte[] bytes = { 0, 0, 0, 25 };
        photo.setPhoto(bytes);
        return photo;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/get.photos")
    public @ResponseBody PhotoList getPhotos() {

        PhotoList photoList = new PhotoList();
        Photo photo = new Photo();
        photo.setId(2L);
        byte[] bytes = { 0, 0, 0, 25 };
        photo.setPhoto(bytes);
        photoList.getPhotos().add(photo);
        return photoList;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/get.ids")
    public @ResponseBody IdList getIds() {

        IdList list = new IdList();
        list.getIds().add(1L);
        list.getIds().add(2L);
        list.getIds().add(3L);
        return list;
    }
*/

    @RequestMapping(method = RequestMethod.GET, value = "/get.persons")
    public @ResponseBody WorkerRecordList getPersons(@RequestParam(name = "expr") String expr) {

        logger.debug("=== getPersons ===");

        WorkerRecordList workers = new WorkerRecordList();

        try {
            String exprDecode = URLDecoder.decode( expr, "UTF-8" );
            exprDecode = Tm_SqlQueryHelper.makeLikeArgEx (exprDecode);
            logger.debug("expr = " + exprDecode);

            EmployeeQuery query = new EmployeeQuery(null, null, exprDecode,
                    En_SortField.person_full_name, En_SortDir.ASC);
            query.setSearchByContactInfo(false);

            /**
             * @review а почему не просто:
             *
             * return personDAO.getEmployees(query).stream().map(p->new WorkerRecord(p)).collect(..)
             */
            List<Person> persons = personDAO.getEmployees(query);

            persons.forEach(person ->
                    workers.getWorkerRecords().add(new WorkerRecord(person))
            );

        } catch (Exception e) {
            logger.error ("error while get workers", e);
        }
        return workers;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/add.worker")
    public @ResponseBody ServiceResult addWorker(@RequestBody WorkerRecord rec) {

        logger.debug("=== addWorker ===");

        try {

            BeanInfo infoRec = Introspector.getBeanInfo(rec.getClass());

            logger.debug("properties from 1C:");
            for (PropertyDescriptor pl : infoRec.getPropertyDescriptors()) {
                logger.debug(pl.getDisplayName() + " = " + (pl.getReadMethod() != null ? pl.getReadMethod().invoke(rec,null) : null));
            }

            ServiceResult isValid = isValidWorkerRecord (rec);
            if (!isValid.isSuccess ()) {
                logger.debug("error result, " + isValid.getErrInfo());
                return isValid;
            }

            if (rec.isDeleted() || rec.isFired()) {
                logger.debug("error result, " + En_ErrorCode.DELETED_OR_FIRED_RECORD.getMessage());
                return ServiceResult.failResult(En_ErrorCode.DELETED_OR_FIRED_RECORD.getCode(), En_ErrorCode.DELETED_OR_FIRED_RECORD.getMessage(), rec.getId());
            }

            CompanyHomeGroupItem item = companyGroupHomeDAO.getByExternalCode(rec.getCompanyCode().trim());
            if (item == null) {
                logger.debug("error result, " + En_ErrorCode.UNKNOWN_COMP.getMessage());
                return ServiceResult.failResult(En_ErrorCode.UNKNOWN_COMP.getCode(), En_ErrorCode.UNKNOWN_COMP.getMessage(), rec.getId());
            }

            CompanyDepartment department = companyDepartmentDAO.getByExternalId(rec.getDepartmentId().trim(), item.getCompanyId ());
            if (department == null) {
                logger.debug("error result, " + En_ErrorCode.UNKNOWN_DEP.getMessage());
                return ServiceResult.failResult(En_ErrorCode.UNKNOWN_DEP.getCode(), En_ErrorCode.UNKNOWN_DEP.getMessage(), rec.getId());
            }

            if (workerEntryDAO.checkExistsByExternalId(rec.getWorkerId().trim(), item.getCompanyId ())) {
                logger.debug("error result, " + En_ErrorCode.EXIST_WOR.getMessage());
                return ServiceResult.failResult(En_ErrorCode.EXIST_WOR.getCode(), En_ErrorCode.EXIST_WOR.getMessage(), rec.getId());
            }

            return transactionTemplate.execute(transactionStatus -> {

                try {

                    Person person = null;
                    if (rec.getId () != null) {
                        person = personDAO.get (rec.getId ());
                    }

                    if (person == null) {
                        person = new Person ();
                        person.setCreated (new Date());
                        person.setCreator ("portal-api@" + Inet4Address.getLocalHost ().getHostAddress());
                        person.setCompanyId (item.getMainId());
                    }

                    copy (rec, person);

                    person.setFired(false);
                    person.setDeleted(false);

                    if (person.getId () == null) {
                        personDAO.persist(person);
                        logger.debug("created person with id = " + person.getId());
                    } else {
                        personDAO.merge(person);
                    }

                    WorkerPosition position = getValidPosition (rec.getPositionName (), item.getCompanyId ());

                    WorkerEntry worker = new WorkerEntry ();
                    worker.setCreated (new Date ());
                    worker.setPersonId (person.getId ());
                    worker.setCompanyId (item.getCompanyId ());
                    worker.setDepartmentId (department.getId ());
                    worker.setPositionId (position.getId ());
                    worker.setHireDate (HelperFunc.isNotEmpty(rec.getHireDate ()) ? HelperService.DATE.parse (rec.getHireDate ()) : null);
                    worker.setHireOrderNo (HelperFunc.isNotEmpty(rec.getHireOrderNo ()) ? rec.getHireOrderNo ().trim () : null);
                    worker.setActiveFlag (rec.getActive ());
                    worker.setExternalId(rec.getWorkerId().trim());

                    workerEntryDAO.persist (worker);

                    if (WSConfig.getInstance().isEnableMigration()) {
                        migrationManager.savePerson (person);
                    }

                    logger.debug("success result, workerRowId = " + worker.getId());
                    return ServiceResult.successResult (person.getId ());

                } catch (Exception e) {
                    logger.error ("error while transaction execution", e);
                    throw new RuntimeException();
                }
            });


        } catch (Exception e) {
            logger.error ("error while add worker's record", e);
            return ServiceResult.failResult (En_ErrorCode.NOT_CREATE.getCode (), En_ErrorCode.NOT_CREATE.getMessage (), null);
        }
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/update.worker")
    public @ResponseBody ServiceResult updateWorker(@RequestBody WorkerRecord rec) {

        logger.debug("=== updateWorker ===");

        try {

            /** @review
             *  дублирование, напиши отдельный метод dumpBeanInfo (object)
             */
            BeanInfo infoRec = Introspector.getBeanInfo(rec.getClass());

            logger.debug("properties from 1C:");
            for (PropertyDescriptor pl : infoRec.getPropertyDescriptors()) {
                logger.debug(pl.getDisplayName() + " = " + (pl.getReadMethod() != null ? pl.getReadMethod().invoke(rec,null) : null));
            }

            ServiceResult isValid = isValidWorkerRecord (rec);
            if (!isValid.isSuccess ()) {
                logger.debug("error result, " + isValid.getErrInfo());
                return isValid;
            }

            if (rec.getId () == null || rec.getId () < 0) {
                logger.debug("error result, " + En_ErrorCode.EMPTY_PER_ID.getMessage());
                return ServiceResult.failResult(En_ErrorCode.EMPTY_PER_ID.getCode(), En_ErrorCode.EMPTY_PER_ID.getMessage(), rec.getId());
            }

            CompanyHomeGroupItem item = companyGroupHomeDAO.getByExternalCode(rec.getCompanyCode ().trim ());
            if (item == null) {
                logger.debug("error result, " + En_ErrorCode.UNKNOWN_COMP.getMessage());
                return ServiceResult.failResult(En_ErrorCode.UNKNOWN_COMP.getCode(), En_ErrorCode.UNKNOWN_COMP.getMessage(), rec.getId());
            }

            CompanyDepartment department = companyDepartmentDAO.getByExternalId(rec.getDepartmentId().trim(), item.getCompanyId ());
            if (department == null) {
                logger.debug("error result, " + En_ErrorCode.UNKNOWN_DEP.getMessage());
                return ServiceResult.failResult(En_ErrorCode.UNKNOWN_DEP.getCode(), En_ErrorCode.UNKNOWN_DEP.getMessage(), rec.getId());
            }

            Person person = personDAO.get (rec.getId ());
            if (person == null) {
                logger.debug("error result, " + En_ErrorCode.UNKNOWN_PER.getMessage());
                return ServiceResult.failResult(En_ErrorCode.UNKNOWN_PER.getCode(), En_ErrorCode.UNKNOWN_PER.getMessage(), rec.getId());
            }

            WorkerEntry worker = workerEntryDAO.getByExternalId(rec.getWorkerId().trim(), item.getCompanyId ());
            if (worker == null || !worker.getPersonId().equals(person.getId ())) {
                logger.debug("error result, " + En_ErrorCode.UNKNOWN_WOR.getMessage());
                return ServiceResult.failResult(En_ErrorCode.UNKNOWN_WOR.getCode(), En_ErrorCode.UNKNOWN_WOR.getMessage(), rec.getId());
            }

            return transactionTemplate.execute(transactionStatus -> {

                try {

                    copy (rec, person);

                    if (rec.isFired () || rec.isDeleted()) {

                        if (rec.isFired()) {
                            logger.debug("=== fireWorker ===");
                        }
                        if (rec.isDeleted()) {
                            logger.debug("=== deletedWorker ===");
                        }

                        workerEntryDAO.remove (worker);

                        if (!workerEntryDAO.checkExistsByPersonId(person.getId())) {
                            person.setFired (rec.isFired ());
                            person.setDeleted(rec.isDeleted());
                        }

                        personDAO.merge (person);
                        if (WSConfig.getInstance().isEnableMigration()) {
                            migrationManager.savePerson (person);
                        }

                        logger.debug("success result, workerRowId = " + worker.getId());
                        return ServiceResult.successResult (person.getId ());
                    }

                    personDAO.merge (person);

                    WorkerPosition position = getValidPosition (rec.getPositionName (), item.getCompanyId ());

                    worker.setDepartmentId (department.getId ());

                    worker.setPositionId (position.getId ());
                    worker.setHireDate (HelperFunc.isNotEmpty(rec.getHireDate ()) ? HelperService.DATE.parse (rec.getHireDate ()) : null);
                    worker.setHireOrderNo (HelperFunc.isNotEmpty(rec.getHireOrderNo ()) ? rec.getHireOrderNo ().trim () : null);
                    worker.setActiveFlag (rec.getActive ());

                    workerEntryDAO.merge (worker);

                    if (WSConfig.getInstance().isEnableMigration()) {
                        migrationManager.savePerson (person);
                    }

                    logger.debug("success result, workerRowId = " + worker.getId());
                    return ServiceResult.successResult (person.getId ());

                } catch (Exception e) {
                    logger.error ("error while transaction execution", e);
                    throw new RuntimeException();
                }
            });

        } catch (Exception e) {
            logger.error ("error while update worker's record", e);
            return ServiceResult.failResult (En_ErrorCode.NOT_UPDATE.getCode (), En_ErrorCode.NOT_UPDATE.getCode (), null);
        }
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/update.workers")
    public @ResponseBody ServiceResultList updateWorkers(@RequestBody WorkerRecordList list) {

        logger.debug("=== updateWorkers ===");

        ServiceResultList results = new ServiceResultList();

        try {
            // @review, бесполезная проверка. NPE или пустой список на входе приведут к пустому списку на выходе и без нее
            if (list != null && list.getWorkerRecords() != null && !list.getWorkerRecords().isEmpty ()) {
                for (WorkerRecord wr : list.getWorkerRecords()) {
                    results.getServiceResults().add(updateWorker(wr));
                }
            }
        } catch (Exception e) {
            logger.error ("error while update workers", e);
        }
        return results;
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/delete.worker")
    public @ResponseBody ServiceResult deleteWorker(@RequestParam(name = "externalId") String externalId, @RequestParam(name = "companyCode") String companyCode) {

        logger.debug("=== deleteWorker ===");

        try {

            if (HelperFunc.isEmpty(companyCode)) {
                logger.debug("error result, " + En_ErrorCode.EMPTY_COMP_CODE.getMessage());
                return ServiceResult.failResult(En_ErrorCode.EMPTY_COMP_CODE.getCode(), En_ErrorCode.EMPTY_COMP_CODE.getMessage(), null);
            }
            String companyDecode = URLDecoder.decode(companyCode, "UTF-8");
            logger.debug("companyCode = " + companyDecode);

            if (HelperFunc.isEmpty(externalId)) {
                logger.debug("error result, " + En_ErrorCode.EMPTY_WOR_ID.getMessage());
                return ServiceResult.failResult(En_ErrorCode.EMPTY_WOR_ID.getCode(), En_ErrorCode.EMPTY_WOR_ID.getMessage(), null);
            }
            String externalIdDecode = URLDecoder.decode(externalId, "UTF-8");
            logger.debug("externalId = " + externalIdDecode);

            CompanyHomeGroupItem item = companyGroupHomeDAO.getByExternalCode(companyDecode.trim ());
            if (item == null) {
                logger.debug("error result, " + En_ErrorCode.UNKNOWN_COMP.getMessage());
                return ServiceResult.failResult(En_ErrorCode.UNKNOWN_COMP.getCode(), En_ErrorCode.UNKNOWN_COMP.getMessage(), null);
            }

            WorkerEntry worker = workerEntryDAO.getByExternalId(externalIdDecode.trim(), item.getCompanyId ());
            if (worker == null) {
                logger.debug("error result, " + En_ErrorCode.UNKNOWN_WOR.getMessage());
                return ServiceResult.failResult(En_ErrorCode.UNKNOWN_WOR.getCode(), En_ErrorCode.UNKNOWN_WOR.getMessage(), null);
            }

            return transactionTemplate.execute(transactionStatus -> {
                try {

                    Long personId = worker.getPersonId ();

                    workerEntryDAO.remove (worker);

                    if (!workerEntryDAO.checkExistsByPersonId(personId)){
                        Person person = personDAO.get (personId);
                        person.setDeleted (true);
                        personDAO.merge (person);
                        if (WSConfig.getInstance().isEnableMigration()) {
                            migrationManager.deletePerson (person);
                        }
                    }
                    logger.debug("success result, workerRowId = " + worker.getId());
                    return ServiceResult.successResult (worker.getId());

                } catch (Exception e) {
                    logger.error ("error while transaction execution", e);
                    throw new RuntimeException();
                }
            });

        } catch (Exception e) {
            logger.error ("error while remove worker's record", e);
            return ServiceResult.failResult (En_ErrorCode.NOT_DELETE.getCode (), En_ErrorCode.NOT_DELETE.getMessage (), null);
        }
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/update.photo")
    public @ResponseBody ServiceResult updatePhoto(@RequestBody Photo photo) {

        logger.debug("=== updatePhoto ===");
        logger.debug("properties from 1C:");
        logger.debug("personId = " + (photo == null ? null : photo.getId()));
        logger.debug("photo's length = " + (photo == null || photo.getContent() == null ? null : photo.getContent().length()));
        logger.debug("photo's content in Base64 = " + (photo == null ? null : photo.getContent()));

        /**
         * @review
         * Здесь я сдался и начал менять код
         */

        if (photo == null) {
            logger.debug("error result, " + En_ErrorCode.EMPTY_PHOTO.getMessage());
            return ServiceResult.failResult(En_ErrorCode.EMPTY_PHOTO.getCode(), En_ErrorCode.EMPTY_PHOTO.getMessage(), null);
        }

        if (photo.getId() == null || photo.getId() < 0) {
            logger.debug("error result, " + En_ErrorCode.EMPTY_PER_ID.getMessage());
            return ServiceResult.failResult(En_ErrorCode.EMPTY_PER_ID.getCode(), En_ErrorCode.EMPTY_PER_ID.getMessage(), photo.getId());
        }

        if (HelperFunc.isEmpty(photo.getContent())) {
            logger.debug("error result, " + En_ErrorCode.EMPTY_PHOTO_CONTENT.getMessage());
            return ServiceResult.failResult(En_ErrorCode.EMPTY_PHOTO_CONTENT.getCode(), En_ErrorCode.EMPTY_PHOTO_CONTENT.getMessage(), photo.getId());
        }

        Person person = personDAO.get (photo.getId());
        if (person == null) {
            logger.debug("error result, " + En_ErrorCode.UNKNOWN_PER.getMessage());
            return ServiceResult.failResult(En_ErrorCode.UNKNOWN_PER.getCode(), En_ErrorCode.UNKNOWN_PER.getMessage(), photo.getId());
        }

        /** лучше бы сделать отдельно реализацию для формирования из Photo строки с именем файла, дабы править потом в одном месте
         *
         */
        String fileName = WSConfig.getInstance ().getDirPhotos () + photo.getId() + ".jpg";

        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(fileName))) {

            logger.debug("fileName = " + fileName);

            out.write (Base64.getDecoder().decode(photo.getContent()));
            out.flush();

            logger.debug("success result, personId = " + photo.getId());
            return ServiceResult.successResult (photo.getId());

        } catch (Exception e) {
            logger.error ("error while update photo", e);
        }

        return ServiceResult.failResult (En_ErrorCode.NOT_UPDATE.getCode (), En_ErrorCode.NOT_UPDATE.getMessage (), null);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/get.photos")
    public @ResponseBody PhotoList getPhotos(@RequestBody IdList list) {

        logger.debug("=== getPhotos ===");
        logger.debug("properties from 1C:");
        logger.debug("list = " + list.getIds());

        InputStream in = null;
        PhotoList photos = new PhotoList();

        try {

            for (Long id : list.getIds()) {

                logger.debug("personId = " + id);
                String fileName = WSConfig.getInstance ().getDirPhotos () + id + ".jpg";
                logger.debug("fileName = " + fileName);
                File file = new File(fileName);
                if (file.exists()) {

                    /**
                     * @review мне кажется, что у apache-commons или apache-io должны быть уже
                     * реализованы методы по чтению и конвертации файлов в base64
                     * здесь у тебя дважды выделяется память: для buf и потом еще для строки
                     * И еще, мне кажется бесполезно создавать BufferedInput если ты сама создаешь буфер
                     * на весь файл и делаешь одну операцию чтения.
                     */
                    in = new BufferedInputStream(new FileInputStream(file));
                    Long size = file.length();
                    byte[] buf = new byte[size.intValue()];
                    in.read(buf);

                    Photo photo = new Photo ();
                    photo.setId (id);
                    photo.setContent (Base64.getEncoder().encodeToString(buf));
                    photos.getPhotos().add (photo);

                    logger.debug("file exists");
                    logger.debug("photo's length = " + (photo.getContent().length()));
                    logger.debug("photo's content in Base64 = " + photo.getContent());
                } else {
                    logger.debug ("file doesn't exist");
                }
            }

        } catch (Exception e) {
            logger.error ("error while get photos", e);
        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (Exception e) {}
        }

        logger.debug("result, size of photo's list = " + photos.getPhotos().size());
        return photos;
    }

    /*
        @review, все здесь я остановился. далее нужно либо дождаться исправления первичных замечаний,
        либо обсуждать устно.
     */

    @RequestMapping(method = RequestMethod.PUT, value = "/update.department")
    public @ResponseBody ServiceResult updateDepartment(@RequestBody DepartmentRecord rec) {

        logger.debug("=== updateDepartment ===");

        try {

            BeanInfo infoRec = Introspector.getBeanInfo(rec.getClass());

            logger.debug("properties from 1C:");
            for (PropertyDescriptor pl : infoRec.getPropertyDescriptors()) {
                logger.debug(pl.getDisplayName() + " = " + (pl.getReadMethod() != null ? pl.getReadMethod().invoke(rec,null) + ";" : "null;"));
            }

            ServiceResult isValid = isValidDepartmentRecord (rec);
            if (!isValid.isSuccess ()) {
                logger.debug("error result, " + isValid.getErrInfo());
                return isValid;
            }

            CompanyHomeGroupItem item = companyGroupHomeDAO.getByExternalCode(rec.getCompanyCode ().trim ());
            if (item == null) {
                logger.debug("error result, " + En_ErrorCode.UNKNOWN_COMP.getMessage());
                return ServiceResult.failResult(En_ErrorCode.UNKNOWN_COMP.getCode(), En_ErrorCode.UNKNOWN_COMP.getMessage(), null);
            }

            CompanyDepartment parentDepartment = null;
            if (HelperFunc.isNotEmpty(rec.getParentId ())) {
                parentDepartment = companyDepartmentDAO.getByExternalId(rec.getParentId ().trim(), item.getCompanyId ());
                if (parentDepartment == null) {
                    logger.debug("error result, " + En_ErrorCode.UNKNOWN_PAR_DEP.getMessage());
                    return ServiceResult.failResult(En_ErrorCode.UNKNOWN_PAR_DEP.getCode(), En_ErrorCode.UNKNOWN_PAR_DEP.getMessage(), null);
                }
            }

            WorkerEntry headWorker = null;
            if (HelperFunc.isNotEmpty(rec.getHeadId())) {
                headWorker = workerEntryDAO.getByExternalId(rec.getHeadId().trim(), item.getCompanyId ());
                if (headWorker == null) {
                    logger.debug("error result, " + En_ErrorCode.UNKNOWN_WOR.getMessage());
                    return ServiceResult.failResult(En_ErrorCode.UNKNOWN_WOR.getCode(), En_ErrorCode.UNKNOWN_WOR.getMessage(), null);
                }
            }

            CompanyDepartment department = companyDepartmentDAO.getByExternalId(rec.getDepartmentId().trim(), item.getCompanyId ());
            if (department == null) {
                department = new CompanyDepartment ();
                department.setCreated (new Date ());
                department.setCompanyId (item.getCompanyId ());
                department.setTypeId (1);
                department.setExternalId(rec.getDepartmentId().trim());
            }

            department.setName (rec.getDepartmentName ().trim ());
            department.setParentId (parentDepartment == null ? null : parentDepartment.getId());
            department.setHeadId (headWorker == null ? null : headWorker.getId());

            if (department.getId () == null)
                companyDepartmentDAO.persist (department);
            else
                companyDepartmentDAO.merge (department);

            logger.debug("success result, departmentRowId = " + department.getId());
            return ServiceResult.successResult (department.getId());

        } catch (Exception e) {
            logger.error ("error while update department's record", e);
        }

        return ServiceResult.failResult (En_ErrorCode.NOT_UPDATE.getCode (), En_ErrorCode.NOT_UPDATE.getMessage (), null);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/delete.department")
    public @ResponseBody ServiceResult deleteDepartment(@RequestParam(name = "externalId") String externalId, @RequestParam(name = "companyCode") String companyCode) {

        logger.debug("=== deleteDepartment ===");

        try {

            if (HelperFunc.isEmpty(companyCode)) {
                logger.debug("error result, " + En_ErrorCode.EMPTY_COMP_CODE.getMessage());
                return ServiceResult.failResult(En_ErrorCode.EMPTY_COMP_CODE.getCode(), En_ErrorCode.EMPTY_COMP_CODE.getMessage(), null);
            }

            if (HelperFunc.isEmpty(externalId)) {
                logger.debug("error result, " + En_ErrorCode.EMPTY_DEP_ID.getMessage());
                return ServiceResult.failResult(En_ErrorCode.EMPTY_DEP_ID.getCode(), En_ErrorCode.EMPTY_DEP_ID.getMessage(), null);
            }

            String companyDecode = URLDecoder.decode( companyCode, "UTF-8" );
            logger.debug("companyCode = " + companyDecode);
            String externalIdDecode = URLDecoder.decode(externalId, "UTF-8");
            logger.debug("externalId = " + externalIdDecode);

            CompanyHomeGroupItem item = companyGroupHomeDAO.getByExternalCode(companyDecode.trim ());
            if (item == null) {
                logger.debug("error result, " + En_ErrorCode.UNKNOWN_COMP.getMessage());
                return ServiceResult.failResult(En_ErrorCode.UNKNOWN_COMP.getCode(), En_ErrorCode.UNKNOWN_COMP.getMessage(), null);
            }

            CompanyDepartment department = companyDepartmentDAO.getByExternalId(externalIdDecode.trim(), item.getCompanyId ());
            if (department == null) {
                logger.debug("error result, " + En_ErrorCode.UNKNOWN_DEP.getMessage());
                return ServiceResult.failResult(En_ErrorCode.UNKNOWN_DEP.getCode(), En_ErrorCode.UNKNOWN_DEP.getMessage(), null);
            }

            if (companyDepartmentDAO.checkExistsByParentId(department.getId ())) {
                logger.debug("error result, " + En_ErrorCode.EXIST_CHILD_DEP.getMessage());
                return ServiceResult.failResult(En_ErrorCode.EXIST_CHILD_DEP.getCode(), En_ErrorCode.EXIST_CHILD_DEP.getMessage(), null);
            }

            if (workerEntryDAO.checkExistsByDepId(department.getId ())) {
                logger.debug("error result, " + En_ErrorCode.EXIST_DEP_WOR.getMessage());
                return ServiceResult.failResult(En_ErrorCode.EXIST_DEP_WOR.getCode(), En_ErrorCode.EXIST_DEP_WOR.getMessage(), null);
            }

            companyDepartmentDAO.remove (department);

            logger.debug("success result, departmentRowId = " + department.getId());
            return ServiceResult.successResult (department.getId());

        } catch (Exception e) {
            logger.error ("error while remove department's record", e);
        }

        return ServiceResult.failResult (En_ErrorCode.NOT_DELETE.getCode (), En_ErrorCode.NOT_DELETE.getMessage (), null);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/update.position")
    public @ResponseBody ServiceResult updatePosition(@RequestParam(name = "oldName") String oldName, @RequestParam(name = "newName") String newName, @RequestParam(name = "companyCode") String companyCode) {

        logger.debug("=== updatePosition ===");
        try {

            if (HelperFunc.isEmpty(companyCode)) {
                logger.debug("error result, " + En_ErrorCode.EMPTY_COMP_CODE.getMessage());
                return ServiceResult.failResult(En_ErrorCode.EMPTY_COMP_CODE.getCode(), En_ErrorCode.EMPTY_COMP_CODE.getMessage(), null);
            }

            if (HelperFunc.isEmpty(oldName) || HelperFunc.isEmpty(newName)) {
                logger.debug("error result, " + En_ErrorCode.EMPTY_POS.getMessage());
                return ServiceResult.failResult(En_ErrorCode.EMPTY_POS.getCode(), En_ErrorCode.EMPTY_POS.getMessage(), null);
            }

            String oldNameDecode = URLDecoder.decode(oldName, "UTF-8");
            logger.debug("oldName = " + oldNameDecode);
            String newNameDecode = URLDecoder.decode(newName, "UTF-8");
            logger.debug("newName = " + newNameDecode);
            String companyDecode = URLDecoder.decode(companyCode, "UTF-8");
            logger.debug("companyCode = " + companyDecode);

            CompanyHomeGroupItem item = companyGroupHomeDAO.getByExternalCode(companyDecode.trim ());
            if (item == null) {
                logger.debug("error result, " + En_ErrorCode.UNKNOWN_COMP.getMessage());
                return ServiceResult.failResult(En_ErrorCode.UNKNOWN_COMP.getCode(), En_ErrorCode.UNKNOWN_COMP.getMessage(), null);
            }

            WorkerPosition existsPosition = workerPositionDAO.getByName(newNameDecode, item.getCompanyId());
            if (existsPosition != null) {
                logger.debug("error result, " + En_ErrorCode.EXIST_POS.getMessage());
                return ServiceResult.failResult(En_ErrorCode.EXIST_POS.getCode(), En_ErrorCode.EXIST_POS.getMessage(), null);
            }

            WorkerPosition position = workerPositionDAO.getByName(oldNameDecode, item.getCompanyId());
            if (position == null) {
                logger.debug("error result, " + En_ErrorCode.UNKNOWN_POS.getMessage());
                return ServiceResult.failResult(En_ErrorCode.UNKNOWN_POS.getCode(), En_ErrorCode.UNKNOWN_POS.getMessage(), null);
            }

            position.setName(newNameDecode.trim());

            workerPositionDAO.merge(position);

            logger.debug("success result, positionRowId = " + position.getId());
            return ServiceResult.successResult (position.getId());

        } catch (Exception e) {
            logger.error ("error while update position's record", e);
        }

        return ServiceResult.failResult (En_ErrorCode.NOT_UPDATE.getCode (), En_ErrorCode.NOT_UPDATE.getMessage (), null);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/delete.position")
    public @ResponseBody ServiceResult deletePosition(@RequestParam(name = "name") String name, @RequestParam(name = "companyCode") String companyCode) {

        logger.debug("=== deletePosition ===");

        try {

            if (HelperFunc.isEmpty(companyCode)) {
                logger.debug("error result, " + En_ErrorCode.EMPTY_COMP_CODE.getMessage());
                return ServiceResult.failResult(En_ErrorCode.EMPTY_COMP_CODE.getCode(), En_ErrorCode.EMPTY_COMP_CODE.getMessage(), null);
            }

            if (HelperFunc.isEmpty(name)) {
                logger.debug("error result, " + En_ErrorCode.EMPTY_POS.getMessage());
                return ServiceResult.failResult(En_ErrorCode.EMPTY_POS.getCode(), En_ErrorCode.EMPTY_POS.getMessage(), null);
            }

            String nameDecode = URLDecoder.decode( name, "UTF-8" );
            logger.debug("name = " + nameDecode);
            String companyDecode = URLDecoder.decode( companyCode, "UTF-8" );
            logger.debug("companyCode = " + companyDecode);

            CompanyHomeGroupItem item = companyGroupHomeDAO.getByExternalCode(companyDecode.trim ());
            if (item == null) {
                logger.debug("error result, " + En_ErrorCode.UNKNOWN_COMP.getMessage());
                return ServiceResult.failResult(En_ErrorCode.UNKNOWN_COMP.getCode(), En_ErrorCode.UNKNOWN_COMP.getMessage(), null);
            }

            WorkerPosition position = workerPositionDAO.getByName(nameDecode, item.getCompanyId ());
            if (position == null) {
                logger.debug("error result, " + En_ErrorCode.UNKNOWN_POS.getMessage());
                return ServiceResult.failResult(En_ErrorCode.UNKNOWN_POS.getCode(), En_ErrorCode.UNKNOWN_POS.getMessage(), null);
            }

            if (workerEntryDAO.checkExistsByPosId(position.getId ())) {
                logger.debug("error result, " + En_ErrorCode.EXIST_POS_WOR.getMessage());
                return ServiceResult.failResult(En_ErrorCode.EXIST_POS_WOR.getCode(), En_ErrorCode.EXIST_POS_WOR.getMessage(), null);
            }

            workerPositionDAO.remove (position);

            logger.debug("success result, positionRowId = " + position.getId());
            return ServiceResult.successResult (position.getId());

        } catch (Exception e) {
            logger.error ("error while remove position's record", e);
        }

        return ServiceResult.failResult (En_ErrorCode.NOT_DELETE.getCode (), En_ErrorCode.NOT_DELETE.getMessage (), null);
    }

    private ServiceResult isValidWorkerRecord(WorkerRecord rec) {

        if (HelperFunc.isEmpty(rec.getCompanyCode ())) {
            return ServiceResult.failResult(En_ErrorCode.EMPTY_COMP_CODE.getCode(), En_ErrorCode.EMPTY_COMP_CODE.getMessage(), rec.getId());
        }

        if (HelperFunc.isEmpty(rec.getDepartmentId())) {
            return ServiceResult.failResult(En_ErrorCode.EMPTY_DEP_ID.getCode(), En_ErrorCode.EMPTY_DEP_ID.getMessage(), rec.getId());
        }

        if (!rec.getDepartmentId().trim ().matches("^\\S{1,30}$")) {
            return ServiceResult.failResult(En_ErrorCode.INV_FORMAT_DEP_CODE.getCode(), En_ErrorCode.INV_FORMAT_DEP_CODE.getMessage(), rec.getId());
        }

        if (HelperFunc.isEmpty(rec.getPositionName ())) {
            return ServiceResult.failResult(En_ErrorCode.EMPTY_POS.getCode(), En_ErrorCode.EMPTY_POS.getMessage(), rec.getId());
        }

        if (HelperFunc.isEmpty(rec.getWorkerId())) {
            return ServiceResult.failResult(En_ErrorCode.EMPTY_WOR_ID.getCode(), En_ErrorCode.EMPTY_WOR_ID.getMessage(), rec.getId());
        }

        if (!rec.getWorkerId().trim ().matches("^\\S{1,30}$")) {
            return ServiceResult.failResult(En_ErrorCode.INV_FORMAT_WOR_CODE.getCode(), En_ErrorCode.INV_FORMAT_WOR_CODE.getMessage(), rec.getId());
        }

        if (HelperFunc.isEmpty(rec.getFirstName ())) {
            return ServiceResult.failResult(En_ErrorCode.EMPTY_FIRST_NAME.getCode(), En_ErrorCode.EMPTY_FIRST_NAME.getMessage(), rec.getId());
        }

        if (HelperFunc.isEmpty(rec.getLastName ())) {
            return ServiceResult.failResult(En_ErrorCode.EMPTY_LAST_NAME.getCode(), En_ErrorCode.EMPTY_LAST_NAME.getMessage(), rec.getId());
        }

        if (HelperFunc.isNotEmpty(rec.getIpAddress ()) &&
                !rec.getIpAddress ().trim ().matches("^[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}$")) {
            return ServiceResult.failResult(En_ErrorCode.INV_FORMAT_IP.getCode(), En_ErrorCode.INV_FORMAT_IP.getMessage(), rec.getId());
        }

        return ServiceResult.successResult (rec.getId ());
    }

    private ServiceResult isValidDepartmentRecord(DepartmentRecord rec) {

        if (HelperFunc.isEmpty(rec.getCompanyCode ())) {
            return ServiceResult.failResult(En_ErrorCode.EMPTY_COMP_CODE.getCode(), En_ErrorCode.EMPTY_COMP_CODE.getMessage(), null);
        }

        if (HelperFunc.isEmpty(rec.getDepartmentId())) {
            return ServiceResult.failResult(En_ErrorCode.EMPTY_DEP_ID.getCode(), En_ErrorCode.EMPTY_DEP_ID.getMessage(), null);
        }

        if (!rec.getDepartmentId().trim ().matches("^\\S{1,30}$")) {
            return ServiceResult.failResult(En_ErrorCode.INV_FORMAT_DEP_CODE.getCode(), En_ErrorCode.INV_FORMAT_DEP_CODE.getMessage(), null);
        }

        if (HelperFunc.isEmpty(rec.getDepartmentName ())) {
            return ServiceResult.failResult(En_ErrorCode.EMPTY_DEP_NAME.getCode(), En_ErrorCode.EMPTY_DEP_NAME.getMessage(), null);
        }

        return ServiceResult.successResult (null);
    }

    private void copy(WorkerRecord rec, Person person) throws ParseException {

        person.setUpdated(new Date());

        person.setFirstName (rec.getFirstName ().trim ());
        person.setLastName (rec.getLastName ().trim ());
        person.setSecondName (HelperFunc.isEmpty(rec.getSecondName ()) ? null : rec.getSecondName ().trim ());
        person.setDisplayName (HelperService.generateDisplayName (person.getFirstName (), person.getLastName (), person.getSecondName ()));
        person.setDisplayShortName (HelperService.generateDisplayShortName (person.getFirstName (), person.getLastName (), person.getSecondName ()));
        person.setGender (rec.getSex () == null ? En_Gender.UNDEFINED : rec.getSex () == 1 ? En_Gender.MALE : En_Gender.FEMALE);
        person.setBirthday (HelperFunc.isEmpty(rec.getBirthday()) ? null : HelperService.DATE.parse(rec.getBirthday()));
        person.setIpAddress (HelperFunc.isEmpty(rec.getIpAddress ()) ? null : rec.getIpAddress ().trim ());
        person.setPassportInfo (HelperFunc.isEmpty(rec.getPassportInfo ()) ? null : rec.getPassportInfo ().trim ());
        person.setInfo (HelperFunc.isEmpty(rec.getInfo ()) ? null : rec.getInfo ().trim ());

        PlainContactInfoFacade contactInfoFacade = new PlainContactInfoFacade(person.getContactInfo());
        contactInfoFacade.setWorkPhone (HelperFunc.isEmpty(rec.getPhoneWork ()) ? null : rec.getPhoneWork ().trim ());
        contactInfoFacade.setMobilePhone (HelperFunc.isEmpty(rec.getPhoneMobile ()) ? null : rec.getPhoneMobile ().trim ());
        contactInfoFacade.setHomePhone (HelperFunc.isEmpty(rec.getPhoneHome ()) ? null : rec.getPhoneHome ().trim ());
        contactInfoFacade.setLegalAddress (HelperFunc.isEmpty(rec.getAddress ()) ? null : rec.getAddress ().trim ());
        contactInfoFacade.setHomeAddress (HelperFunc.isEmpty(rec.getAddressHome ()) ? null : rec.getAddressHome ().trim ());
        contactInfoFacade.setEmail (HelperFunc.isEmpty(rec.getEmail ()) ? null : rec.getEmail ().trim ());
        contactInfoFacade.setEmail_own (HelperFunc.isEmpty(rec.getEmailOwn ()) ? null : rec.getEmailOwn ().trim ());
        contactInfoFacade.setFax (HelperFunc.isEmpty(rec.getFax ()) ? null : rec.getFax ().trim ());
    }

    private WorkerPosition getValidPosition(String positionName, Long companyId) {

        WorkerPosition position = workerPositionDAO.getByName(positionName.trim(), companyId);

        if (position != null)
            return position;

        position = new WorkerPosition ();
        position.setCompanyId (companyId);
        position.setName (positionName.trim ());
        Long id = workerPositionDAO.persist (position);
        position.setId (id);

        return position;
    }
}
