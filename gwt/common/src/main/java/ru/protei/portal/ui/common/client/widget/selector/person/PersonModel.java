package ru.protei.portal.ui.common.client.widget.selector.person;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.query.PersonQuery;
import ru.protei.portal.core.model.util.TransliterationUtils;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.AsyncSelectorModel;
import ru.protei.portal.ui.common.client.selector.LoadingHandler;
import ru.protei.portal.ui.common.client.selector.cache.SelectorDataCache;
import ru.protei.portal.ui.common.client.selector.cache.SelectorDataCacheLoadHandler;
import ru.protei.portal.ui.common.client.service.PersonControllerAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.List;

/**
 * Модель контактов домашней компании
 */
public abstract class PersonModel implements Activity, AsyncSelectorModel<PersonShortView> {

    @Event
    public void onInit( AuthEvents.Success event ) {
        cache.clearCache();
        cache.setLoadHandler(makeLoadHandler(makeQuery()));
    }

    @Override
    public PersonShortView get(int elementIndex, LoadingHandler handler) {
        return cache.get(elementIndex, handler);
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
                            transliteration(options);
                            handler.onSuccess(options);
                        }
                    }
            );
        };
    }

    private void transliteration(List<PersonShortView> options) {
        options.forEach(option -> option.setName(TransliterationUtils.transliterate(option.getName(), LocaleInfo.getCurrentLocale().getLocaleName())));
    }

    private PersonQuery makeQuery() {
        PersonQuery query = new PersonQuery();
        query.setFired(false);
        query.setDeleted(false);
        query.setOnlyPeople(true);

        return query;
    }

    @Inject
    PersonControllerAsync personService;

    @Inject
    Lang lang;

    private SelectorDataCache<PersonShortView> cache = new SelectorDataCache<>();
}

