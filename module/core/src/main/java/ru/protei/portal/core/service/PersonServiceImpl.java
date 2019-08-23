package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.ent.UserSessionDescriptor;
import ru.protei.portal.core.model.query.PersonQuery;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.service.user.AuthService;

import java.util.*;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.CoreResponse.error;
import static ru.protei.portal.api.struct.CoreResponse.ok;
/**
 * Сервис управления person
 */
public class PersonServiceImpl implements PersonService {

    @Autowired
    PersonDAO personDAO;

    @Override
    public CoreResponse< List< PersonShortView > > shortViewList(AuthToken authToken, PersonQuery query) {
        query = processQueryByPolicyScope(authToken, query);

        List<Person> list = personDAO.getPersons( query );

        if ( list == null )
            return error(En_ResultStatus.GET_DATA_ERROR );

        List< PersonShortView > result = list.stream().map( Person::toFullNameShortView ).collect( Collectors.toList() );

        return ok(result);
    }

    @Override
    public CoreResponse<Map<Long, String>> getPersonNames(Collection<Long> ids) {
        Collection<Person> list = personDAO.partialGetListByKeys(ids, "id", "displayname");

        if ( list == null )
            return error(En_ResultStatus.GET_DATA_ERROR );

        Map<Long, String> names = new HashMap<>(list.size());
        list.forEach(a -> names.put(a.getId(), a.getDisplayName()));
        return ok(names );
    }

    private PersonQuery processQueryByPolicyScope(AuthToken token, PersonQuery personQuery ) {
        UserSessionDescriptor descriptor = authService.findSession( token );
        Set<UserRole> roles = descriptor.getLogin().getRoles();
        if (policyService.hasGrantAccessFor( roles, En_Privilege.COMPANY_VIEW )) {
            return personQuery;
        }

        if (personQuery.getCompanyIds() != null) {
            personQuery.getCompanyIds().retainAll( descriptor.getAllowedCompaniesIds() );
        }

        log.info("processQueryByPolicyScope(): PersonQuery modified: {}", personQuery);
        return personQuery;
    }

    @Autowired
    AuthService authService;

    @Autowired
    PolicyService policyService;

    private static final Logger log = LoggerFactory.getLogger(PersonServiceImpl.class);

}
