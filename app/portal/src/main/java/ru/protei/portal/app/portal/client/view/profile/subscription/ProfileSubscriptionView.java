package ru.protei.portal.app.portal.client.view.profile.subscription;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.inject.Inject;
import ru.protei.portal.app.portal.client.activity.profile.subscription.AbstractProfileSubscriptionActivity;
import ru.protei.portal.app.portal.client.activity.profile.subscription.AbstractProfileSubscriptionView;
import ru.protei.portal.app.portal.client.widget.casefilter.group.PersonCaseFilterWidget;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeMultiSelector;

import java.util.List;
import java.util.Set;

public class ProfileSubscriptionView extends Composite implements AbstractProfileSubscriptionView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
    }

    @Override
    public void setActivity(AbstractProfileSubscriptionActivity activity) {
        this.activity = activity;
    }

    @UiHandler("persons")
    public void onPersonsSelected(ValueChangeEvent<Set<PersonShortView>> event) {
        if (activity != null) {
            activity.onPersonsChanged();
        }
    }

    @Override
    public HasValue<Set<PersonShortView>> persons() {
        return persons;
    }

    @Override
    public void setPersonId(Long personId) {
        personCaseFilterWidget.setPersonId(personId);
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        persons.setAddEnsureDebugId(DebugIds.PROFILE.SUBSCRIPTION.EMPLOYEE_SELECTOR_ADD_BUTTON);
        persons.setClearEnsureDebugId(DebugIds.PROFILE.SUBSCRIPTION.EMPLOYEE_SELECTOR_CLEAR_BUTTON);
        persons.setItemContainerEnsureDebugId(DebugIds.PROFILE.SUBSCRIPTION.EMPLOYEE_SELECTOR_ITEM_CONTAINER);
        persons.setLabelEnsureDebugId(DebugIds.PROFILE.SUBSCRIPTION.LABEL.EMPLOYEE_SELECTOR);
    }

    @Inject
    @UiField(provided = true)
    EmployeeMultiSelector persons;

    @Inject
    @UiField( provided = true )
    PersonCaseFilterWidget personCaseFilterWidget;

    private AbstractProfileSubscriptionActivity activity;

    private static ProfileSubscriptionViewUiBinder ourUiBinder = GWT.create(ProfileSubscriptionViewUiBinder.class);
    interface ProfileSubscriptionViewUiBinder extends UiBinder<HTMLPanel, ProfileSubscriptionView> {}
}