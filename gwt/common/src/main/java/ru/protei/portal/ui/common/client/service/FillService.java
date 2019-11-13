package ru.protei.portal.ui.common.client.service;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.util.HomeCompaniesUtils;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.LinkedList;
import java.util.List;

public abstract class FillService implements Activity {
    @Event
    public void authEvent(AuthEvents.Success event) {
        companyService.getAllHomeCompanyIds(new FluentCallback<List<Long>>()
                .withError(throwable -> HomeCompaniesUtils.setHomeCompanyIds(new LinkedList<>()))
                .withSuccess(HomeCompaniesUtils::setHomeCompanyIds)
        );
    }

    @Inject
    CompanyControllerAsync companyService;
}
