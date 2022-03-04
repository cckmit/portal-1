package ru.protei.portal.ui.common.client.widget.selector.person;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.query.PersonQuery;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.LoadingHandler;
import ru.protei.portal.ui.common.client.selector.model.BaseSelectorModel;
import ru.protei.portal.ui.common.client.service.PersonControllerAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import static ru.protei.portal.core.model.helper.CollectionUtils.isEmpty;
import static ru.protei.portal.core.model.helper.CollectionUtils.nullIfEmpty;
import static ru.protei.portal.core.model.util.TransliterationUtils.transliterate;

/**
 * Синхронная модель Person
 */
public abstract class PersonModel extends BaseSelectorModel<PersonShortView> implements Activity {

    @Event
    public void onInit(AuthEvents.Success event) {
        myId = event.profile.getId();
    }

    public boolean isCompaniesPresent() {
        return !isEmpty( query.getCompanyIds() );
    }

    public void setIsPeople( Boolean isPeople ) {
        query.setPeople( isPeople );
    }

    public void setIsFired( Boolean isFired ) {
        query.setFired( isFired );
    }

    @Override
    protected void requestData( LoadingHandler selector, String searchText) {
        personService.getPersonViewList(query, new RequestCallback<List<PersonShortView>>() {

            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(List<PersonShortView> options) {
                int value = options.indexOf(new PersonShortView("", myId, false));
                if (value > 0) {
                    options.add(0, options.remove(value));
                }
                options = transliteration(options);
                updateElements(options, selector);
                if(refreshable!=null){
                    refreshable.refresh();
                }
            }
        });
    }

    public void updateCompanies( Refreshable selector, Set<Long> companyIds ) {
        this.refreshable = selector;
        query.setCompanyIds( nullIfEmpty( companyIds ) );
        clean();
    }

    public Collection<PersonShortView> getValues() {
        return elements;
    }

    private List<PersonShortView> transliteration( List<PersonShortView> options) {
        options.forEach(option -> option.setName(transliterate(option.getName())));
        return options;
    }

    @Inject
    PersonControllerAsync personService;
    @Inject
    Lang lang;

    private PersonQuery query = new PersonQuery(null, null, false, false, null, En_SortField.person_full_name, En_SortDir.ASC, null);
    private Refreshable refreshable;

    private Long myId;

    private static final Logger log = Logger.getLogger( PersonModel.class.getName() );
}
