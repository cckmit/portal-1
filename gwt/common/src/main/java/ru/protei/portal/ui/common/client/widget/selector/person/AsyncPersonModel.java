package ru.protei.portal.ui.common.client.widget.selector.person;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.PersonQuery;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.AsyncSearchSelectorModel;
import ru.protei.portal.ui.common.client.selector.LoadingHandler;
import ru.protei.portal.ui.common.client.selector.cache.SelectorDataCache;
import ru.protei.portal.ui.common.client.selector.cache.SelectorDataCacheLoadHandler;
import ru.protei.portal.ui.common.client.service.PersonControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Асинхронная модель Person
 */
public abstract class AsyncPersonModel implements AsyncSearchSelectorModel<PersonShortView>, Activity {
    @Event
    public void onInit(AuthEvents.Success event) {
        requestCurrentPerson(event.profile.getId());
        cache.clearCache();
        cache.setLoadHandler(makeLoadHandler(makeQuery(null)));
    }

    @Override
    public PersonShortView get(int elementIndex, LoadingHandler handler) {
        if (currentPerson == null || StringUtils.isNotBlank(searchString)) {
            return cache.get(elementIndex, handler);
        }
        if (elementIndex == 0) return currentPerson;
        return cache.get(--elementIndex, handler);
    }

    @Override
    public void setSearchString(String searchString) {
        this.searchString = searchString;
        cache.clearCache();
        cache.setLoadHandler(makeLoadHandler(makeQuery(searchString)));
    }

    private static final Logger log = Logger.getLogger( AsyncPersonModel.class.getName() );
    private SelectorDataCacheLoadHandler<PersonShortView> makeLoadHandler(PersonQuery query) {
        return (offset, limit, handler) -> {
            query.setOffset(offset);
            query.setLimit(limit);
            log.info( "makeLoadHandler(): AsyncPersonModel.java" );
            personService.getPersonViewList(query, new FluentCallback<List<PersonShortView>>()
                    .withError(throwable -> fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR)))
                    .withSuccess(handler::onSuccess)
            );
        };
    }

    private void requestCurrentPerson(Long myId) {
        if (currentPerson != null && Objects.equals(currentPerson.getId(), myId)) {
            return;
        }

        currentPerson = null;
        personService.getPersonShortView(myId, new FluentCallback<PersonShortView>().withSuccess(r->currentPerson=r));
    }

    public void updateCompanies(Set<Long> companyIds) {
        cache.clearCache();
        cache.setLoadHandler(makeLoadHandler(makeQuery(searchString, companyIds)));
    }

    private PersonQuery makeQuery(String searchString) {
        return makeQuery(searchString, null);
    }

    private PersonQuery makeQuery(String searchString, Set<Long> companyIds) {
        PersonQuery personQuery = new PersonQuery();
        personQuery.setDeleted(false);
        personQuery.setPeople(true);
        personQuery.setSearchString(searchString);
        personQuery.setCompanyIds(companyIds);

        return personQuery;
    }


    @Inject
    PersonControllerAsync personService;

    @Inject
    Lang lang;

    private PersonShortView currentPerson;
    private String searchString;
    private SelectorDataCache<PersonShortView> cache = new SelectorDataCache<>();
}

