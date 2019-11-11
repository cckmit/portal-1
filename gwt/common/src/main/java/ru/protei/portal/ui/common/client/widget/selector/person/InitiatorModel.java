package ru.protei.portal.ui.common.client.widget.selector.person;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.query.PersonQuery;
import ru.protei.portal.core.model.util.TransliterationUtils;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.PersonControllerAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.*;

/**
 * Модель заявителей по обращению
 */
public abstract class InitiatorModel implements Activity {

    @Event
    public void onInit(AuthEvents.Success event) {
        myId = event.profile.getId();
    }

    public void updateCompanies(SelectorWithModel<PersonShortView> selector, Set<Long> companyIds, boolean fired) {
        PersonQuery query = new PersonQuery(companyIds, null, fired, false, null, En_SortField.person_full_name, En_SortDir.ASC);
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
                transliteration(options);
                selector.fillOptions(options);
                selector.refreshValue();
            }
        });
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

    private void transliteration(List<PersonShortView> options) {
        options.forEach(option -> option.setDisplayShortName(TransliterationUtils.transliterate(option.getDisplayShortName(), LocaleInfo.getCurrentLocale().getLocaleName())));
    }

    @Inject
    PersonControllerAsync personService;
    @Inject
    Lang lang;

    private Long myId;
}
