package ru.protei.portal.ui.common.client.service;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.List;
import java.util.function.Consumer;

public abstract class HomeCompanyService implements Activity {
    @Event
    public void authEvent(AuthEvents.Success event) {
        if (homeCompanyIds != null) {
            return;
        }

        companyService.getAllHomeCompanyIds(new FluentCallback<List<Long>>()
                .withSuccess(this::setHomeCompanyIds)
        );
    }

    public void isHomeCompany(Long companyId, Consumer<Boolean> companyConsumer) {
        if (homeCompanyIds != null) {
            companyConsumer.accept(homeCompanyIds.contains(companyId));
        } else {
            companyService.getAllHomeCompanyIds(new FluentCallback<List<Long>>()
                    .withError(throwable -> companyConsumer.accept(null))
                    .withSuccess(companyIds -> {
                        setHomeCompanyIds(companyIds);
                        companyConsumer.accept(homeCompanyIds.contains(companyId));
                    })
            );
        }
    }

    private void setHomeCompanyIds(List<Long> homeCompanyIds) {
        this.homeCompanyIds = homeCompanyIds;
    }

    @Inject
    CompanyControllerAsync companyService;

    private List<Long> homeCompanyIds;
}
