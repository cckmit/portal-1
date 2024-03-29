package ru.protei.portal.ui.common.client.service;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class HomeCompanyService implements Activity {
    @Event
    public void authEvent(AuthEvents.Success event) {
        if (homeCompanies != null) {
            return;
        }

        companyService.getAllHomeCompanies(new FluentCallback<List<EntityOption>>()
                .withSuccess(this::setHomeCompanies)
        );
    }

    public void getHomeCompany(Long companyId, Consumer<EntityOption> successConsumer) {
        if (homeCompanies != null) {
            findCompanyById(companyId, homeCompanies, successConsumer);
        } else {
            companyService.getAllHomeCompanies(new FluentCallback<List<EntityOption>>()
                    .withSuccess(companies -> {
                        setHomeCompanies(companies);
                        findCompanyById(companyId, companies, successConsumer);
                    })
            );
        }
    }

    public void isHomeCompany(Long companyId, Consumer<Boolean> successConsumer) {
        if (homeCompanies != null) {
            successConsumer.accept(homeCompanies.stream().anyMatch(company -> Objects.equals(company.getId(), companyId)));
        } else {
            companyService.getAllHomeCompanies(new FluentCallback<List<EntityOption>>()
                    .withSuccess(companies -> {
                        setHomeCompanies(companies);
                        successConsumer.accept(homeCompanies.stream().anyMatch(company -> Objects.equals(company.getId(), companyId)));
                    })
            );
        }
    }

    public void getAllHomeCompanies(Consumer<List<EntityOption>> successConsumer) {
        if (homeCompanies != null) {
            successConsumer.accept(homeCompanies);
        } else {
            companyService.getAllHomeCompanies(new FluentCallback<List<EntityOption>>()
                    .withSuccess(companies -> {
                        setHomeCompanies(companies);
                        successConsumer.accept(companies);
                    })
            );
        }
    }

    private void setHomeCompanies(List<EntityOption> homeCompanies) {
        this.homeCompanies = homeCompanies;
    }

    private void findCompanyById(Long companyId, List<EntityOption> homeCompanies, Consumer<EntityOption> successConsumer) {
        homeCompanies
                .stream()
                .filter(company -> Objects.equals(company.getId(), companyId))
                .findAny()
                .ifPresent(successConsumer);
    }

    @Inject
    CompanyControllerAsync companyService;

    private List<EntityOption> homeCompanies;
}
