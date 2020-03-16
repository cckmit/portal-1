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
import ru.protei.portal.ui.common.client.selector.AsyncSelectorModel;
import ru.protei.portal.ui.common.client.selector.LoadingHandler;
import ru.protei.portal.ui.common.client.selector.cache.SelectorDataCache;
import ru.protei.portal.ui.common.client.selector.cache.SelectorDataCacheLoadHandler;
import ru.protei.portal.ui.common.client.selector.model.BaseSelectorModel;
import ru.protei.portal.ui.common.client.service.PersonControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Модель контактных лиц
 */
public abstract class PersonModel extends BaseSelectorModel<PersonShortView> implements Activity {
    @Event
    public void onInit(AuthEvents.Success event) {
        requestCurrentPerson(event.profile.getId());
        cache.clearCache();
        cache.setLoadHandler(makeLoadHandler(makeQuery()));
    }

    @Override
    public PersonShortView get(int elementIndex, LoadingHandler handler) {
        if (StringUtils.isNotBlank(this.searchString)) {
            cache.clearCache();
            return super.get(elementIndex, handler);
        }

        clean();

        if (currentPerson == null) {
            return cache.get(elementIndex, handler);
        }
        if (elementIndex == 0) return currentPerson;
        PersonShortView personShortView = cache.get(--elementIndex, handler);
        if (Objects.equals(personShortView, currentPerson)) {
            return cache.get(++elementIndex, handler);
        }
        return personShortView;
    }

    @Override
    public void setSearchString(String searchString) {
        this.searchString = searchString;
        super.setSearchString(searchString);
    }

    protected void requestData(LoadingHandler selector, String searchText) {
        makeRequest(makeQuery(searchText), options -> updateElements(options, selector));
    }

    private SelectorDataCacheLoadHandler<PersonShortView> makeLoadHandler(PersonQuery query) {
        return (offset, limit, handler) -> {
            query.setOffset(offset);
            query.setLimit(limit);
            makeRequest(query, handler::onSuccess);
        };
    }

    private void makeRequest(PersonQuery query, Consumer<List<PersonShortView>> consumer) {
        personService.getPersonViewList(query, new FluentCallback<List<PersonShortView>>()
                .withError(throwable -> fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR)))
                .withSuccess(consumer)
        );
    }

    private void requestCurrentPerson(Long myId) {
        if (currentPerson != null && Objects.equals(currentPerson.getId(), myId)) {
            return;
        }

        currentPerson = null;
        personService.getPerson(myId, new FluentCallback<Person>()
                .withSuccess(this::savePerson)
        );
    }

    private void savePerson(Person person) {
        currentPerson = person.toFullNameShortView();
    }

    private PersonQuery makeQuery() {
        PersonQuery personQuery = new PersonQuery();
        personQuery.setDeleted(false);

        return personQuery;
    }

    private PersonQuery makeQuery(String searchString) {
        PersonQuery personQuery = makeQuery();
        personQuery.setSearchString(searchString);

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

