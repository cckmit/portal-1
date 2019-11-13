package ru.protei.portal.ui.common.client.service;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.List;

public abstract class HomeCompanyService implements Activity {
    @Event
    public void authEvent(AuthEvents.Success event) {
        companyService.getAllHomeCompanyIds(new FluentCallback<List<Long>>()
                .withSuccess(this::setHomeCompanyIds)
        );
    }

    public boolean isHomeCompany(Long companyId) {
        return homeCompanyIds.contains(companyId);
    }

    private void setHomeCompanyIds(List<Long> homeCompanyIds) {
        this.homeCompanyIds = homeCompanyIds;
    }

    @Inject
    CompanyControllerAsync companyService;

    private List<Long> homeCompanyIds;
}
