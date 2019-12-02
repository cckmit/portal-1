package ru.protei.portal.core.service.authtoken;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.CompanyDAO;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dao.UserLoginDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.UserLogin;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

public class AuthTokenServiceImpl implements AuthTokenService {

    @Override
    public Result<Person> getPerson(AuthToken token) {
        if (token == null) return error(En_ResultStatus.INVALID_SESSION_ID);
        return ok(personDAO.get(token.getPersonId()));
    }

    @Override
    public Result<Company> getCompany(AuthToken token) {
        if (token == null) return error(En_ResultStatus.INVALID_SESSION_ID);
        Company company = companyDAO.get(token.getCompanyId());
        jdbcManyRelationsHelper.fillAll(company);
        return ok(company);
    }

    @Override
    public Result<UserLogin> getUserLogin(AuthToken token) {
        if (token == null) return error(En_ResultStatus.INVALID_SESSION_ID);
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
