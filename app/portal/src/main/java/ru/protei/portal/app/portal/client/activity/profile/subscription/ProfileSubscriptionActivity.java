package ru.protei.portal.app.portal.client.activity.profile.subscription;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.app.portal.client.service.PersonSubscriptionControllerAsync;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.Set;
import java.util.function.Consumer;

public abstract class ProfileSubscriptionActivity implements AbstractProfileSubscriptionActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onShow(AppEvents.ShowProfileSubscriptions event) {
        event.parent.clear();
        event.parent.add(view.asWidget());
        requestPersonSubscriptions(this::fillView);
    }

    private void requestPersonSubscriptions(Consumer<Set<PersonShortView>> successAction) {
        personSubscriptionController.getPersonSubscriptions(
                new FluentCallback<Set<PersonShortView>>()
                        .withSuccess(persons -> successAction.accept(persons)));
    }

    private void fillView(Set<PersonShortView> persons) {
        view.persons().setValue(persons);
    }

    @Override
    public void onPersonsChanged() {
        personSubscriptionController.updatePersonSubscriptions(view.persons().getValue(),
                new FluentCallback<Set<PersonShortView>>()
                        .withSuccess(persons -> {
                            fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                            view.persons().setValue(persons);
                        }));

    }

    @Inject
    Lang lang;

    @Inject
    AbstractProfileSubscriptionView view;

    @Inject
    PersonSubscriptionControllerAsync personSubscriptionController;
}
