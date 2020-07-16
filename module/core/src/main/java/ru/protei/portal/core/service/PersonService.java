package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.query.PersonQuery;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Сервис управления person
 */
public interface PersonService {
    Result<Person> getPerson(AuthToken token, Long personId);
    Result<PersonShortView> getPersonShortView(AuthToken token, Long personId);
    Result<List<PersonShortView>> shortViewList(AuthToken authToken, PersonQuery query);
    Result<List<PersonShortView>> shortViewListByIds(List<Long> ids);
    Result<Map<Long, String>> getPersonNames(Collection<Long> ids);
    Result<Person> getCommonManagerByProductId(AuthToken authToken, Long productId);
}
