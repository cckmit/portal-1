package ru.protei.portal.ui.common.client.widget.selector.person;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.inject.Inject;
import liquibase.util.CollectionUtil;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.query.PersonQuery;
import ru.protei.portal.core.model.util.TransliterationUtils;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.PersonControllerAsync;
import ru.protei.portal.ui.common.client.selector.pageable.SelectorModel;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.*;

import static ru.protei.portal.core.model.helper.CollectionUtils.nullIfEmpty;
import static ru.protei.portal.core.model.helper.CollectionUtils.size;
/**
 * Синхронная модель Person
 */
public abstract class PersonModel implements Activity, SelectorModel<PersonShortView> {

    @Event
    public void onInit(AuthEvents.Success event) {
        myId = event.profile.getId();
    }

    void updateCompanies(Refreshable selector, Boolean people, Set<Long> companyIds, Boolean fired) {
        PersonQuery query = new PersonQuery(nullIfEmpty( companyIds ), people, fired, false, null, En_SortField.person_full_name, En_SortDir.ASC, null);
        personService.getPersonViewList(query, new RequestCallback<List<PersonShortView>>() {

            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(List<PersonShortView> options) {
                PersonModel.this.options = options;
                int value = options.indexOf(new PersonShortView("", myId, false));
                if (value > 0) {
                    options.add(0, options.remove(value));
                }
                options = transliteration(options);
                if(selector!=null){
                    selector.refresh();
                }
            }
        });
    }

    public Collection<PersonShortView> getValues() {
        return options;
    }

    public static Set<Long> makeCompanyIds(Company company) {
        if (company == null) {
            return null;
        }
        return makeCompanyIds(company.getId());
    }

    public static Set<Long> makeCompanyIds(Long companyId) {
        if (companyId == null) {
            return null;
        }
        Set<Long> companyIds = new HashSet<>();
        companyIds.add(companyId);
        return companyIds;
    }

    private List<PersonShortView> transliteration( List<PersonShortView> options) {
        options.forEach(option -> option.setName(TransliterationUtils.transliterate(option.getName(), LocaleInfo.getCurrentLocale().getLocaleName())));
        return options;
    }

    @Override
    public PersonShortView get( int elementIndex ) {
        return CollectionUtils.get( options, elementIndex );
    }

    @Inject
    PersonControllerAsync personService;
    @Inject
    Lang lang;
    private List<PersonShortView> options;
    private Long myId;
}
