package ru.protei.portal.ui.common.client.widget.selector.login;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_AdminState;
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.UserLoginShortView;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.UserLoginShortViewQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.AsyncSearchSelectorModel;
import ru.protei.portal.ui.common.client.selector.LoadingHandler;
import ru.protei.portal.ui.common.client.selector.cache.SelectorDataCacheLoadHandler;
import ru.protei.portal.ui.common.client.selector.cache.SelectorDataCacheWithFirstElements;
import ru.protei.portal.ui.common.client.service.AccountControllerAsync;
import ru.protei.portal.ui.common.client.service.CompanyControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.*;
import java.util.stream.Collectors;

public abstract class UserLoginModel implements AsyncSearchSelectorModel<UserLoginShortView>, Activity {
    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        Company personCompany = event.profile.getCompany();

        this.personCompanyIds.clear();
        this.personCompanyIds.add(personCompany.getId());
        if (En_CompanyCategory.HOME.equals(personCompany.getCategory())) {
            companyService.getSingleHomeCompanies(new FluentCallback<List<EntityOption>>()
                    .withSuccess(companies -> {
                        this.personCompanyIds.addAll(companies.stream().map(EntityOption::getId).collect(Collectors.toSet()));
                    }));
        }

        cache.clearCache();
        cache.setLoadHandler(makeLoadHandler(makeQuery(null, personCompanyIds, initiatorCompanyId)));
    }

    @Override
    public UserLoginShortView get(int elementIndex, LoadingHandler handler) {
        return cache.get(elementIndex, handler);
    }

    @Override
    public void setSearchString(String searchString) {
        cache.setIgnoreFirstElements(StringUtils.isNotBlank(searchString));

        cache.clearCache();
        cache.setLoadHandler(makeLoadHandler(makeQuery(searchString, personCompanyIds, initiatorCompanyId)));
    }

    public void setPersonFirstId(Long personId) {
        if (personId == null) {
            return;
        }

        UserLoginShortViewQuery accountQuery = makeQuery(null, personCompanyIds, initiatorCompanyId);
        accountQuery.setPersonIds(new HashSet<>(Collections.singleton(personId)));

        accountService.getUserLoginShortViewList(accountQuery, new FluentCallback<List<UserLoginShortView>>()
                .withSuccess(result -> cache.setFirstElements(result))
        );
    }

    public void setInitiatorCompanyId(Long initiatorCompanyId) {
        this.initiatorCompanyId = initiatorCompanyId;
    }

    private SelectorDataCacheLoadHandler<UserLoginShortView> makeLoadHandler(UserLoginShortViewQuery query) {
        return (offset, limit, handler) -> {
            query.setOffset(offset);
            query.setLimit(limit);
            accountService.getUserLoginShortViewList(query, new FluentCallback<List<UserLoginShortView>>()
                    .withError(throwable -> fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR)))
                    .withSuccess(handler::onSuccess)
            );
        };
    }

    private UserLoginShortViewQuery makeQuery(final String searchString, final Set<Long> personCompanyIds, final Long initiatorCompanyId) {
        UserLoginShortViewQuery accountQuery = new UserLoginShortViewQuery();
        accountQuery.setSearchString(searchString);
        accountQuery.setAdminState(En_AdminState.UNLOCKED);

        HashSet<Long> companyIds = new HashSet<>();
        companyIds.addAll(personCompanyIds);
        if (initiatorCompanyId != null) {
            companyIds.add(initiatorCompanyId);
        }

        accountQuery.setCompanyIds(companyIds);

        return accountQuery;
    }

    @Inject
    AccountControllerAsync accountService;
    @Inject
    CompanyControllerAsync companyService;

    @Inject
    Lang lang;

    private Set<Long> personCompanyIds = new HashSet<>();
    private Long initiatorCompanyId;

    private SelectorDataCacheWithFirstElements<UserLoginShortView> cache = new SelectorDataCacheWithFirstElements<>();
}
