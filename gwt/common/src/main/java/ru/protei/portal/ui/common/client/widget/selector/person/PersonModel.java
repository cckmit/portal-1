package ru.protei.portal.ui.common.client.widget.selector.person;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.query.PersonQuery;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.AsyncSelectorModel;
import ru.protei.portal.ui.common.client.selector.LoadingHandler;
import ru.protei.portal.ui.common.client.selector.cache.SelectorDataCache;
import ru.protei.portal.ui.common.client.selector.cache.SelectorDataCacheLoadHandler;
import ru.protei.portal.ui.common.client.service.PersonControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.List;
import java.util.Objects;

/**
 * Модель контактных лиц
 */
public abstract class PersonModel implements Activity, AsyncSelectorModel<PersonShortView> {
    @Event
    public void onInit(AuthEvents.Success event) {
        requestCurrentPerson(event.profile.getId());
        cache.clearCache();
        cache.setLoadHandler(makeLoadHandler(new PersonQuery()));
        cache.setChunkSize(CHUNK_SIZE);
    }

    @Override
    public PersonShortView get(int elementIndex, LoadingHandler handler) {
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

    public void clear() {
        cache.clearCache();
    }

    private SelectorDataCacheLoadHandler<PersonShortView> makeLoadHandler(PersonQuery query) {
        return (offset, limit, handler) -> {
            query.setOffset(offset);
            query.setLimit(limit);
            personService.getPersonViewList(query, new RequestCallback<List<PersonShortView>>() {
                        @Override
                        public void onError(Throwable throwable) {
                            fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                        }

                        @Override
                        public void onSuccess(List<PersonShortView> options) {
                            handler.onSuccess(options);
                        }
                    }
            );
        };
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

    @Inject
    PersonControllerAsync personService;

    @Inject
    Lang lang;

    private PersonShortView currentPerson;
    private SelectorDataCache<PersonShortView> cache = new SelectorDataCache<>();
    private static final Integer CHUNK_SIZE = 1000;
}

