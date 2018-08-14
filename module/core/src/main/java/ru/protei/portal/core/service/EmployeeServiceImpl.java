package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dao.CompanyDAO;
import ru.protei.portal.core.model.dao.CompanyGroupHomeDAO;
import ru.protei.portal.core.model.dao.PersonAbsenceDAO;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.PersonAbsence;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.view.EmployeeDetailView;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.WorkerView;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;
import ru.protei.winter.jdbc.JdbcSort;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Реализация сервиса управления сотрудниками
 */
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    CompanyGroupHomeDAO groupHomeDAO;

    @Autowired
    PersonDAO personDAO;

    @Autowired
    CompanyDAO companyDAO;

    @Autowired
    PersonAbsenceDAO absenceDAO;

    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;

    @Override
    public EmployeeDetailView getEmployeeAbsences(Long id, Long tFrom, Long tTill, Boolean isFull) {

        Person p = personDAO.getEmployee( id );
        if (p == null) {
            return null;
        }

        List<PersonAbsence> personAbsences = absenceDAO.getForRange(id, new Date(tFrom), new Date(tTill));
        if(isFull){
            fillAbsencesOfCreators(personAbsences);
        }

        return new EmployeeDetailView().fill(personAbsences, isFull);
    }

    @Override
    public CoreResponse< Person > getEmployee( Long id ) {
        Person person = personDAO.getEmployee(id);

        return person != null ? new CoreResponse<Person>().success(person)
            : new CoreResponse<Person>().error(En_ResultStatus.NOT_FOUND);
    }

    public EmployeeDetailView getEmployeeProfile(Long id){

        Person p = personDAO.getEmployee( id );
        if (p == null) {
            return null;
        }

        EmployeeDetailView view = new EmployeeDetailView().fill(p);

        view.fill(absenceDAO.getForRange(p.getId(), null, null), false);

        return view;
    }

    @Override
    public CoreResponse<List<PersonShortView>> shortViewList(EmployeeQuery query) {
        List<Person> list = personDAO.getEmployees(query);

        if (list == null)
            new CoreResponse<List<PersonShortView>>().error(En_ResultStatus.GET_DATA_ERROR);

        List<PersonShortView> result = list.stream().map(Person::toShortNameShortView ).collect(Collectors.toList());

        return new CoreResponse<List<PersonShortView>>().success(result,result.size());
    }

    @Override
    public CoreResponse<List<Person>> employeeList(EmployeeQuery query) {
        List<Person> list = personDAO.getEmployees(query);

        if (list == null)
            new CoreResponse<List<Person>>().error(En_ResultStatus.GET_DATA_ERROR);

        jdbcManyRelationsHelper.fill(list, "workers");

        return new CoreResponse<List<Person>>().success(list);
    }

    @Override
    public CoreResponse<List<WorkerView>> list(String param) {

        // temp-hack, hardcoded company-id. must be replaced to sys_config.ownCompanyId
        Company our_comp = companyDAO.get(1L);

        param = HelperFunc.makeLikeArg(param, true);

        JdbcSort sort = new JdbcSort(JdbcSort.Direction.ASC, "displayName");

        List<WorkerView> result = personDAO.getListByCondition("company_id=? and isdeleted=? and displayName like ?", sort, our_comp.getId(), 0, param)
                .stream().map(p -> new WorkerView(p, our_comp))
                .collect(Collectors.toList());

        return new CoreResponse<List<WorkerView>>().success(result, result.size());
    }

    private void fillAbsencesOfCreators(List<PersonAbsence> personAbsences){
        if(personAbsences.size()==0)
            return;

        List<Long> ids = personAbsences.stream()
                .map(p -> p.getCreatorId())
                .distinct()
                .collect(Collectors.toList());

        HashMap<Long,String> creators = new HashMap<>();

        for (Person p : personDAO.partialGetListByKeys(ids, "displayShortName")){
            creators.put(p.getId(), p.getDisplayShortName());
        }

        for(PersonAbsence p:personAbsences)
            p.setCreator(creators.get(p.getCreatorId()));

    }
}
