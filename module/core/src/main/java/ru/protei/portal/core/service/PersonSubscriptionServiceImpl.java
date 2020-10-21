package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.PersonNotifierDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.PersonNotifier;
import ru.protei.portal.core.model.struct.PersonSubscriptionChangeRequest;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.List;
import java.util.Objects;
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
    @Transactional
    public Result<Set<PersonShortView>> updatePersonSubscriptions(AuthToken token, PersonSubscriptionChangeRequest changeRequest) {

        if (changeRequest == null || changeRequest.getId() == null || changeRequest.getPersons() == null)
            return error(En_ResultStatus.INCORRECT_PARAMS);

        if (!Objects.equals(changeRequest.getId(), token.getPersonId()))
            return error(En_ResultStatus.PERMISSION_DENIED);

        personNotifierDAO.removeByNotifierId(token.getPersonId());
        personNotifierDAO.persistBatch(changeRequest.getPersons()
                .stream()
                .map(person -> new PersonNotifier(person.getId(), token.getPersonId()))
                .collect(Collectors.toList()));

        List<PersonNotifier> result = personNotifierDAO.getByNotifierId(token.getPersonId());

        return ok(
                result
                        .stream()
                        .map(PersonNotifier::getPerson)
                        .map(Person::toFullNameShortView)
                        .collect(Collectors.toSet())
        );
    }
}
