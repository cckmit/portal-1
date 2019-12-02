package ru.protei.portal.core.service.authtoken;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.CompanyDAO;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dao.UserLoginDAO;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.UserLogin;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import static ru.protei.portal.api.struct.Result.ok;

public class AuthTokenServiceImpl implements AuthTokenService {

    @Override
    public Result<Person> getPerson(AuthToken token) {
        return ok(personDAO.get(token.getPersonId()));
    }

    @Override
    public Result<Company> getCompany(AuthToken token) {
        Company company = companyDAO.get(token.getCompanyId());
        jdbcManyRelationsHelper.fillAll(company);
        return ok(company);
    }

    @Override
    public Result<UserLogin> getUserLogin(AuthToken token) {
        UserLogin userLogin = userLoginDAO.get(token.getUserLoginId());
        jdbcManyRelationsHelper.fillAll(userLogin);
        return ok(userLogin);
    }

    @Autowired
    PersonDAO personDAO;
    @Autowired
    CompanyDAO companyDAO;
    @Autowired
    UserLoginDAO userLoginDAO;
    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;
}
