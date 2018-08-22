package ru.protei.portal.ui.contact.client.activity.table.concise;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.query.ContactQuery;
import ru.protei.portal.ui.common.client.events.ConfirmDialogEvents;
import ru.protei.portal.ui.common.client.events.ContactEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.ContactControllerAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.List;

public abstract class ContactConciseTableActivity implements AbstractContactConciseTableActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onShow(ContactEvents.ShowConciseTable event) {
        event.parent.clear();
        event.parent.add(view.asWidget());

        contactId = null;

        query = makeQuery(event.companyId);
        view.showEditableColumns(event.editable);

        requestContacts();
    }

    @Event
    public void onConfirmRemove(ConfirmDialogEvents.Confirm event) {
        if (!event.identity.equals(getClass().getName())) {
            return;
        }

        if (contactId == null) {
            return;
        }

        contactService.removeContact(contactId, new AsyncCallback<Boolean>() {
            @Override
            public void onFailure(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(throwable.getMessage(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(Boolean result) {
                contactId = null;
                if (result) {
                    fireEvent(new ContactEvents.Show());
                    fireEvent(new NotifyEvents.Show(lang.contactDeleted(), NotifyEvents.NotifyType.SUCCESS));
                } else {
                    fireEvent(new NotifyEvents.Show(lang.errInternalError(), NotifyEvents.NotifyType.ERROR));
                }
            }
        });
    }

    @Event
    public void onCancelRemove(ConfirmDialogEvents.Cancel event) {
        contactId = null;
    }

    @Override
    public void onItemClicked(Person value) {}

    @Override
    public void onEditClicked(Person value) {
        fireEvent(ContactEvents.Edit.byId(value.getId()));
    }

    @Override
    public void onRemoveClicked(Person value) {
        if (value != null) {
            contactId = value.getId();
            fireEvent(new ConfirmDialogEvents.Show(getClass().getName(), lang.contactRemoveConfirmMessage()));
        }
    }

    private ContactQuery makeQuery(Long companyId) {
        return new ContactQuery(companyId, null, false, null, En_SortField.person_full_name, En_SortDir.ASC);
    }

    private void requestContacts() {
        view.clearRecords();

        contactService.getContacts(query, new RequestCallback<List<Person>>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(List<Person> result) {
                view.setData(result);
            }
        });
    }

    @Inject
    Lang lang;
    @Inject
    AbstractContactConciseTableView view;
    @Inject
    ContactControllerAsync contactService;

    private Long contactId = null;

    private ContactQuery query;
}
