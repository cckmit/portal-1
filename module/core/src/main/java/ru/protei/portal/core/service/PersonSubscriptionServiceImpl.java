package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.PersonNotifierDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.PersonNotifier;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

public class PersonSubscriptionServiceImpl implements PersonSubscriptionService {

    @Autowired
    PersonNotifierDAO personNotifierDAO;

    @Override
    public Result<Set<PersonShortView>> getPersonSubscriptions(AuthToken token) {

        List<PersonNotifier> result = personNotifierDAO.getByNotifierId(token.getPersonId());
        if (result == null)
            return error(En_ResultStatus.GET_DATA_ERROR);

        return ok(result.stream().map(PersonNotifier::getPerson).map(Person::toFullNameShortView).collect(Collectors.toSet()));
    }

    @Override
    public Result<Set<PersonShortView>> updatePersonSubscriptions(AuthToken token, Set<PersonShortView> persons) {

        if (persons == null)
            return error(En_ResultStatus.INCORRECT_PARAMS);

        personNotifierDAO.removeAll();
        personNotifierDAO.persistBatch(persons
                .stream()
                .map(person -> new PersonNotifier(person.getId(), token.getPersonId()))
                .collect(Collectors.toList()));

        List<PersonNotifier> result = personNotifierDAO.getByNotifierId(token.getPersonId());
        if (result == null)
            return error(En_ResultStatus.GET_DATA_ERROR);

        return ok(result.stream().map(PersonNotifier::getPerson).map(Person::toFullNameShortView).collect(Collectors.toSet()));
    }
}
