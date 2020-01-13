package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.PersonAbsence;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.view.*;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;
import ru.protei.winter.jdbc.JdbcSort;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;


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
    EmployeeShortViewDAO employeeShortViewDAO;

    @Autowired
    WorkerEntryShortViewDAO workerEntryShortViewDAO;

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
    public Result< Person > getEmployee( Long id ) {
        Person person = personDAO.getEmployee(id);
        if ( person == null ) {
            return ok(person);
        }

        // RESET PRIVACY INFO
        person.setPassportInfo(null);

        return ok(person);
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
    public Result<List<PersonShortView>> shortViewList( EmployeeQuery query) {
        List<Person> list = personDAO.getEmployees(query);

        if (list == null) {
            return Result.error( En_ResultStatus.GET_DATA_ERROR);
        }

        List<PersonShortView> result = list.stream().map( Person::toShortNameShortView ).collect(Collectors.toList());

        return ok(result);
    }

    @Override
    public Result<PersonShortView> getEmployeeById( AuthToken token, Long emploeeId ) {
        Person person = personDAO.get( emploeeId );
        if(person==null) return error( En_ResultStatus.NOT_FOUND );
        return ok(person.toShortNameShortView());
    }

    @Override
    public Result<SearchResult<EmployeeShortView>> employeeList( AuthToken token, EmployeeQuery query) {

        SearchResult<EmployeeShortView> sr = employeeShortViewDAO.getSearchResult(query);
        List<EmployeeShortView> results = sr.getResults();

        if (CollectionUtils.isNotEmpty(results)) {
            List<Long> employeeIds = results.stream().map(e -> e.getId()).collect(Collectors.toList());
            List<WorkerEntryShortView> workerEntries = workerEntryShortViewDAO.listByPersonIds(employeeIds);
            results.forEach(employee ->
                employee.setWorkerEntries(workerEntries.stream().filter(workerEntry -> workerEntry.getPersonId().equals(employee.getId())).collect(Collectors.toList()))
             );
        }
        return ok(sr);
    }

    @Override
    public Result<List<WorkerView>> list( String param) {

        // temp-hack, hardcoded company-id. must be replaced to sys_config.ownCompanyId
        Company our_comp = companyDAO.get(1L);

        param = HelperFunc.makeLikeArg(param, true);

        JdbcSort sort = new JdbcSort(JdbcSort.Direction.ASC, "displayName");

        List<WorkerView> result = personDAO.getListByCondition("company_id=? and isdeleted=? and displayName like ?", sort, our_comp.getId(), 0, param)
                .stream().map(p -> new WorkerView(p, our_comp))
                .collect(Collectors.toList());

        return ok(result);
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
