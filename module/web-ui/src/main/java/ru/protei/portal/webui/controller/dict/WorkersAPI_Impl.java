package ru.protei.portal.webui.controller.dict;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import ru.protei.portal.core.model.dao.CompanyDAO;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.view.WorkerView;
import ru.protei.portal.webui.api.struct.HttpListResult;
import ru.protei.winter.jdbc.JdbcSort;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michael on 06.04.16.
 */
public class WorkersAPI_Impl implements WorkersAPI {

    @Autowired
    PersonDAO personDAO;

    @Autowired
    CompanyDAO companyDAO;

    public HttpListResult<WorkerView> list(@RequestParam(name = "q", defaultValue = "") String param) {

        // temp-hack, hardcoded company-id. must be replaced to sys_config.ownCompanyId
        Company our_comp = companyDAO.get(1L);

        List<WorkerView> r = new ArrayList<>();

        if (param == null || param.isEmpty()) {
            param = "%";
        }
        else {
            if (!param.startsWith("%"))
                param = "%" + param;

            if (!param.endsWith("%"))
                param = param + "%";
        }

        JdbcSort sort = new JdbcSort (JdbcSort.Direction.ASC, "displayName");

        for (Person p : personDAO.getListByCondition("company_id=? and isdeleted=? and displayName like ?", sort , our_comp.getId(), 0, param)) {
            r.add(new WorkerView(p, our_comp));
        }

        return new HttpListResult<>(r, false);
    }
}
