package ru.protei.portal.ui.common.client.widget.selector.casemember;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_DevUnitPersonRoleType;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.PersonEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.LoadingHandler;
import ru.protei.portal.ui.common.client.selector.model.BaseSelectorModel;
import ru.protei.portal.ui.common.client.service.PersonControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Модель регионов
 */
public abstract class CaseMemberModelAsync extends BaseSelectorModel<PersonShortView>
        implements Activity {

    @Event
    public void onInit(AuthEvents.Success event) {
        requestCurrentPerson(event.profile.getId());
        clean();
    }

    @Event
    public void onPersonListChanged(PersonEvents.ChangePersonModel event) {
        clean();
    }

    public void setRole(En_DevUnitPersonRoleType role) {
        this.role = role;
    }

    @Override
    protected void requestData(LoadingHandler selector, String searchText ) {
        personController.getCaseMembersList(role, new FluentCallback<List<PersonShortView>>()
                .withError(throwable -> {
                    fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                })
                .withSuccess( result -> updateElements( setCurrentPersonFirst(result), selector ) ));
    }

    private Collection<PersonShortView> setCurrentPersonFirst(Collection<PersonShortView> result) {
        if (result.remove(currentPerson)) {
            List<PersonShortView> temp = new ArrayList<>();
            temp.add(currentPerson);
            temp.addAll(result);
            return temp;
        } else {
            return result;
        }
    }

    private void requestCurrentPerson(Long myId) {
        if (currentPerson != null && Objects.equals(currentPerson.getId(), myId)) {
            return;
        }
        currentPerson = null;
        personController.getPerson(myId, new FluentCallback<Person>().withSuccess(r -> currentPerson = r.toFullNameShortView()));
    }

    PersonShortView currentPerson;

    @Inject
    PersonControllerAsync personController;

    @Inject
    Lang lang;

    private En_DevUnitPersonRoleType role;
}
