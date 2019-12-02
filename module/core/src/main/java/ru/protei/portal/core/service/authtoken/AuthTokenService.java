package ru.protei.portal.core.service.authtoken;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.UserLogin;

public interface AuthTokenService {

    Result<Person> getPerson(AuthToken token);

    Result<Company> getCompany(AuthToken token);

    Result<UserLogin> getUserLogin(AuthToken token);
}
