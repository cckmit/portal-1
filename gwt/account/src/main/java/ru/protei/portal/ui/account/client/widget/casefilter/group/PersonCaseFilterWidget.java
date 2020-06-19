package ru.protei.portal.ui.account.client.widget.casefilter.group;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.portal.ui.account.client.widget.casefilter.item.PersonCaseFilterCallbacks;
import ru.protei.portal.ui.account.client.widget.casefilter.item.PersonCaseFilterItem;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.PersonCaseFilterControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.List;

abstract public class PersonCaseFilterWidget extends Composite implements Activity {

    @Inject
    public void init() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    public void setPersonId(Long personId) {
        itemContainer.clear();
        makeItemAndFillValue(null);

        this.personId = personId;
        controller.getCaseFilterByPersonId(personId, new FluentCallback<List<CaseFilterShortView>>()
                .withError(throwable -> {
                    fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                })
                .withSuccess(list -> {
                    if (!list.isEmpty()) {
                        itemContainer.clear();
                        list.forEach(this::makeItemAndFillValue);
                    }
                })
        );
    }

    private void makeItemAndFillValue(CaseFilterShortView value) {
        PersonCaseFilterItem personCaseFilterItem = itemProvider.get();
        personCaseFilterItem.setValue(value);
        personCaseFilterItem.setCallback(new PersonCaseFilterCallbacks() {
            @Override
            public void add(Long caseFilterId) {
                controller.addPersonToCaseFilter(personId, caseFilterId, new FluentCallback<Boolean>()
                        .withError(throwable -> fireEvent(new NotifyEvents.Show(lang.errPersonCaseFilterChangeError(), NotifyEvents.NotifyType.ERROR)))
                        .withSuccess(b -> fireEvent(new NotifyEvents.Show(lang.personCaseFilterChange(), NotifyEvents.NotifyType.SUCCESS))));
            }

            @Override
            public void remove(Long caseFilterId) {
                controller.removePersonToCaseFilter(personId, caseFilterId, new FluentCallback<Boolean>()
                        .withError(throwable -> fireEvent(new NotifyEvents.Show(lang.errPersonCaseFilterChangeError(), NotifyEvents.NotifyType.ERROR)))
                        .withSuccess(b -> fireEvent(new NotifyEvents.Show(lang.personCaseFilterChange(), NotifyEvents.NotifyType.SUCCESS))));
                if (itemContainer.getWidgetCount() > 1) {
                    itemContainer.remove(personCaseFilterItem);
                }
            }

            @Override
            public void change(Long oldCaseFilterId, Long newCaseFilterId) {
                controller.changePersonToCaseFilter(personId, oldCaseFilterId, newCaseFilterId, new FluentCallback<Boolean>()
                        .withError(throwable -> fireEvent(new NotifyEvents.Show(lang.errPersonCaseFilterChangeError(), NotifyEvents.NotifyType.ERROR)))
                        .withSuccess(b -> fireEvent(new NotifyEvents.Show(lang.personCaseFilterChange(), NotifyEvents.NotifyType.SUCCESS))));
            }
        });
        itemContainer.add(personCaseFilterItem);
    }

    @UiField
    HTMLPanel itemContainer;
    @UiField
    Lang lang;

    @Inject
    Provider<PersonCaseFilterItem> itemProvider;
    @Inject
    PersonCaseFilterControllerAsync controller;

    private Long personId;

    interface PersonCaseFilterGroupUiBinder extends UiBinder<HTMLPanel, PersonCaseFilterWidget> {}
    private static PersonCaseFilterGroupUiBinder ourUiBinder = GWT.create(PersonCaseFilterGroupUiBinder.class);
}