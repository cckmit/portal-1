package ru.protei.portal.core.service.dict;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import ru.protei.portal.api.struct.HttpListResult;
import ru.protei.portal.core.model.dao.CompanyDAO;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.utils.HelperFunc;
import ru.protei.winter.jdbc.JdbcSort;

/**
 * Created by michael on 27.09.16.
 */
public class CompanyServiceImpl implements CompanyService {

    @Autowired
    CompanyDAO companyDAO;

    @Override
    public HttpListResult<Company> list(@RequestParam(name = "q", defaultValue = "") String param) {

        param = HelperFunc.makeLikeArg(param,true);

        JdbcSort sort = new JdbcSort(JdbcSort.Direction.ASC, "cname");

        return new HttpListResult<>(companyDAO.getListByCondition("cname like ?", sort, param), false);
    }
}
