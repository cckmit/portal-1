package ru.protei.portal.core.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.utils.HelperFunc;

import java.util.Date;
import java.util.List;

/**
 * Реализация сервиса управления контактами
 */
public class CaseServiceImpl implements CaseService {

    private static Logger log = Logger.getLogger(CaseServiceImpl.class);

    @Autowired
    CaseObjectDAO caseObjectDAO;

    @Override
    public CoreResponse<List<CaseObject>> contactList(CaseQuery query) {
        List<CaseObject> list = caseObjectDAO.getCases( query );

        if ( list == null )
            new CoreResponse<List<CaseObject>>().error(En_ResultStatus.GET_DATA_ERROR);

        return new CoreResponse<List<CaseObject>>().success(list);
    }

    @Override
    public CoreResponse<CaseObject> getCaseObject(long id) {
        CaseObject caseObject = caseObjectDAO.get( id );

        return caseObject != null ? new CoreResponse<CaseObject>().success(caseObject)
                : new CoreResponse<CaseObject>().error(En_ResultStatus.NOT_FOUND);
    }


    @Override
    public CoreResponse<Person> saveContact(Person p) {
//        if ( caseObjectDAO.isEmployee(p)) {
//            log.warn(String.format("person %d is employee",p.getId()));
//            return new CoreResponse<Person>().error(En_ResultStatus.VALIDATION_ERROR);
//        }
//
//        if (HelperFunc.isEmpty(p.getFirstName()) || HelperFunc.isEmpty(p.getLastName())
//                || p.getCompanyId() == null)
//            return new CoreResponse<Person>().error(En_ResultStatus.VALIDATION_ERROR);
//
//        if (HelperFunc.isEmpty(p.getDisplayName())) {
//            p.setDisplayName(p.getLastName() + " " + p.getFirstName());
//        }
//
//        if (HelperFunc.isEmpty(p.getDisplayShortName())) {
//            StringBuilder b = new StringBuilder();
//            b.append (p.getLastName()).append(" ")
//                    .append (p.getFirstName().substring(0,1).toUpperCase()).append(".")
//            ;
//
//            if (!p.getSecondName().isEmpty()) {
//                b.append(" ").append(p.getSecondName().substring(0,1).toUpperCase()).append(".");
//            }
//
//            p.setDisplayShortName(b.toString());
//        }
//
//        if (p.getCreated() == null)
//            p.setCreated(new Date());
//
//        if (p.getCreator() == null)
//            p.setCreator("service");
//
//        if (p.getGender() == null)
//            p.setGender( En_Gender.UNDEFINED );
//
//        if ( caseObjectDAO.saveOrUpdate(p)) {
//            return new CoreResponse<Person>().success(p);
//        }
//
//        return new CoreResponse<Person>().error(En_ResultStatus.INTERNAL_ERROR);
    }
}
