package ru.protei.portal.ui.common.client.widget.selector.person;

import com.google.gwt.core.client.GWT;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.query.PersonQuery;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.PersonControllerAsync;
import ru.protei.portal.ui.common.client.util.SimpleProfiler;
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

    public void subscribe( SelectorWithModel<PersonShortView> selector) {
        subscribers.add(selector);
        selector.fillOptions(list);
    }

    public void updateCompanies(Set<Long> companyIds, boolean fired) {
        refreshOptions(companyIds, fired);
    }

    public Collection getList() {
        return new ArrayList(list);
    }

    private void notifySubscribers() {
        sp.push();
        for (SelectorWithModel<PersonShortView> selector : subscribers) {
            selector.fillOptions(list);
            sp.check( " fillOptions " + selector.getClass().getSimpleName() );
            selector.refreshValue();
            sp.check( " refreshValue " );
        }
        sp.pop();
    }
SimpleProfiler sp = new SimpleProfiler( SimpleProfiler.ON, ( message, currentTime ) -> GWT.log("InitiatorModel "+getClass().getSimpleName()+ " "+message + " t="+currentTime));
    private void refreshOptions(Set<Long> companyIds, boolean fired) {

        PersonQuery query = new PersonQuery(companyIds, null, fired, false, null, En_SortField.person_full_name, En_SortDir.ASC);
        sp.start( " start ");
        personService.getPersonViewList(query, new RequestCallback<List<PersonShortView>>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(List<PersonShortView> options) {
                sp.check( " success ");
                int value = options.indexOf(new PersonShortView("", myId, false));
                if (value > 0) {
                    options.add(0, options.remove(value));
                }
                sp.check( " options ");
                list.clear();
                list.addAll(options);
                sp.check( " add options ");
                notifySubscribers();
                sp.stop( " notifySubscribers ");
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

    @Inject
    PersonControllerAsync personService;

    @Inject
    Lang lang;

    private List<PersonShortView> list = new ArrayList<>();

    List<SelectorWithModel<PersonShortView>> subscribers = new ArrayList<>();

    private Long myId;

}
