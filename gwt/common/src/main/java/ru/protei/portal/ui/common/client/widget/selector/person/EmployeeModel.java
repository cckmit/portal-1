package ru.protei.portal.ui.common.client.widget.selector.person;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.EmployeeControllerAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.LifecycleSelectorModel;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.List;

/**
 * Модель контактов домашней компании
 */
public abstract class EmployeeModel extends LifecycleSelectorModel<PersonShortView> {

    @Event
    public void onInit( AuthEvents.Success event ) {
        myId = event.profile.getId();
        clear();
    }

    @Override
    protected void refreshOptions() {
        if (requested) return;
        requested = true;
        employeeService.getEmployeeViewList(query, new FluentCallback<List<PersonShortView>>()
                .withResult(() -> requested = false)
                .withError(throwable -> {
                    fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                })
                .withSuccess(options -> {
                    int value = options.indexOf( new PersonShortView("", myId, false ) );
                    if ( value > 0 ) {
                        options.add(0, options.remove(value));
                    }
                    notifySubscribers(options);
                }));
    }

    @Inject
    EmployeeControllerAsync employeeService;
    @Inject
    Lang lang;

    private Long myId;
    private boolean requested;
    private EmployeeQuery query = new EmployeeQuery(false, false, true, null, null, En_SortField.person_full_name, En_SortDir.ASC);
}
