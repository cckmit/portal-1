package ru.protei.portal.app.portal.client.activity.profile.subscription;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.app.portal.client.service.PersonSubscriptionControllerAsync;
import ru.protei.portal.core.model.struct.PersonSubscriptionChangeRequest;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.Profile;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toCollection;

public abstract class ProfileSubscriptionActivity implements AbstractProfileSubscriptionActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onShow(AppEvents.ShowProfileSubscriptions event) {
        event.parent.clear();
        event.parent.add(view.asWidget());
        requestPersonSubscriptions(this::fillPersonSubscriptionsView);
        fillFilterSubscriptionsView(policyService.getProfile());
    }

    private void requestPersonSubscriptions(Consumer<Set<PersonShortView>> successAction) {
        personSubscriptionController.getPersonSubscriptions(
                new FluentCallback<Set<PersonShortView>>()
                        .withSuccess(persons -> successAction.accept(sortByName(persons))));
    }

    private void fillPersonSubscriptionsView(Set<PersonShortView> persons) {
        view.persons().setValue(persons);
    }

    private void fillFilterSubscriptionsView(Profile profile) {
        view.setPersonId(profile.getId());
    }

    @Override
    public void onPersonsChanged() {
        personSubscriptionController.updatePersonSubscriptions(
                new PersonSubscriptionChangeRequest(policyService.getProfile().getId(), view.persons().getValue()),
                new FluentCallback<Set<PersonShortView>>()
                        .withSuccess(persons -> {
                            fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                            view.persons().setValue(sortByName(persons));
                        }));

    }

    private Set<PersonShortView> sortByName(Set<PersonShortView> persons) {
        return persons.stream().sorted(Comparator.comparing(PersonShortView::getName))
                               .collect(toCollection(LinkedHashSet::new));
    }

    @Inject
    Lang lang;

    @Inject
    AbstractProfileSubscriptionView view;

    @Inject
    PersonSubscriptionControllerAsync personSubscriptionController;

    @Inject
    PolicyService policyService;
}
