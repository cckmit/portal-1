package ru.protei.portal.core.service.dict;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.HttpListResult;
import ru.protei.portal.core.model.dao.CompanyDAO;
import ru.protei.portal.core.model.dao.CompanyGroupHomeDAO;
import ru.protei.portal.core.model.dao.PersonAbsenceDAO;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.PersonAbsence;
import ru.protei.portal.core.model.view.EmployeeDetailView;
import ru.protei.portal.core.model.view.WorkerView;
import ru.protei.portal.core.utils.HelperFunc;
import ru.protei.winter.jdbc.JdbcSort;

import java.util.*;


/**
 * Created by michael on 06.04.16.
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

    @Override
    public EmployeeDetailView getEmployeeAbsences(Long id, Long tFrom, Long tTill, Boolean isFull) {

        Person p = personDAO.getEmployeeById(id);
        if (p == null) {
            return null;
        }

        List<PersonAbsence> personAbsences = absenceDAO.getForRange(id, new Date(tFrom), new Date(tTill));
        if(isFull){
            fillAbsencesOfCreators(personAbsences);
        }

        return new EmployeeDetailView().fill(personAbsences, isFull);
    }

    private void fillAbsencesOfCreators(List<PersonAbsence> personAbsences){
        if(personAbsences.size()==0)
            return;

        TreeSet<Long> ids = new TreeSet<>();
        for(PersonAbsence pa: personAbsences)
            ids.add(pa.getCreatorId());

        HashMap<Long,String> creators = new HashMap<>();

        Iterator<Long> iterator = ids.iterator();
        for (Person p : personDAO.partialGetListByKeys(ids, "displayShortName")){
            creators.put(iterator.next(), p.getDisplayShortName());
        }


//        System.out.println("**********************************");
////        for (Long l : creators.keySet())
////            System.out.println(l);
////        for (String s : creators.values())
////            System.out.println(s);
//        System.out.print(creators.toString());


        for(PersonAbsence p:personAbsences)
            p.setCreator(creators.get(p.getCreatorId()));

    }




    public EmployeeDetailView getEmployeeProfile(Long id){

        Person p = personDAO.getEmployeeById(id);
        if (p == null) {
            return null;
        }

        EmployeeDetailView view = new EmployeeDetailView().fill(p);

        view.fill(absenceDAO.getForRange(p.getId(), null, null), false);

        return view;
    }


//    public String getCurrentMissingEmployeeIDs() {
//        List<PersonAbsence> currentAbsences = absenceDAO.getCurrentAbsences(null);
//
//        StringBuilder IDs = new StringBuilder();
//        IDs.append("[");
//
//        for(int i = 0; i < currentAbsences.size(); i++){
//            if(i>0)
//                IDs.append(",");
//            IDs.append(currentAbsences.get(i).getPersonId());
//        }
//
//        IDs.append("]");
//        return IDs.toString();
//    }


    @Override
    public HttpListResult<WorkerView> list(String param) {

        // temp-hack, hardcoded company-id. must be replaced to sys_config.ownCompanyId
        Company our_comp = companyDAO.get(1L);

        List<WorkerView> r = new ArrayList<>();

        param = HelperFunc.makeLikeArg(param, true);

        JdbcSort sort = new JdbcSort(JdbcSort.Direction.ASC, "displayName");

        for (Person p : personDAO.getListByCondition("company_id=? and isdeleted=? and displayName like ?", sort, our_comp.getId(), 0, param)) {
            r.add(new WorkerView(p, our_comp));
        }

        return new HttpListResult<>(r, false);
    }

}
