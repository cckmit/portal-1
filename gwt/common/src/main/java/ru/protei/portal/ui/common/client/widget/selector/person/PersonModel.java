package ru.protei.portal.ui.common.client.widget.selector.person;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.query.PersonQuery;
import ru.protei.portal.core.model.util.TransliterationUtils;
import ru.protei.portal.core.model.view.AbsenceFilterShortView;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.LoadingHandler;
import ru.protei.portal.ui.common.client.selector.model.BaseSelectorModel;
import ru.protei.portal.ui.common.client.service.PersonControllerAsync;
import ru.protei.portal.ui.common.client.selector.pageable.SelectorModel;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.*;
import java.util.logging.Logger;

import static ru.protei.portal.core.model.helper.CollectionUtils.isEmpty;
import static ru.protei.portal.core.model.helper.CollectionUtils.nullIfEmpty;
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

    private PersonQuery query = new PersonQuery(null, null, null, false, null, En_SortField.person_full_name, En_SortDir.ASC, null);
    private Refreshable refreshable;

    private static final Logger log = Logger.getLogger( PersonModel.class.getName() );

    @Override
    protected void requestData( LoadingHandler selector, String searchText) {
        personService.getPersonViewList(query, new RequestCallback<List<PersonShortView>>() {

            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(List<PersonShortView> options) {
//                PersonModel.this.options = options;
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

    public void updateCompanies(Refreshable selector, Boolean people, Set<Long> companyIds, Boolean fired) {
        this.refreshable = selector;
        query.setCompanyIds( nullIfEmpty( companyIds ) );
        query.setPeople( people );
        query.setFired( fired );
        clean();
    }

    public Collection<PersonShortView> getValues() {
        return elements;
    }

    private List<PersonShortView> transliteration( List<PersonShortView> options) {
        options.forEach(option -> option.setName(TransliterationUtils.transliterate(option.getName(), LocaleInfo.getCurrentLocale().getLocaleName())));
        return options;
    }

    @Inject
    PersonControllerAsync personService;
    @Inject
    Lang lang;

    private Long myId;
}
