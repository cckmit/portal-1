package ru.protei.portal.app.portal.client.widget.casefilter.group;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.view.FilterShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.app.portal.client.widget.casefilter.item.PersonCaseFilterCallbacks;
import ru.protei.portal.app.portal.client.widget.casefilter.item.PersonCaseFilterItem;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.PersonCaseFilterControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.List;

import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;

abstract public class PersonCaseFilterWidget extends Composite implements Activity {

    @Inject
    public void init() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        setTestAttributes();
    }

    public void setPersonId(Long personId) {
        itemContainer.clear();
        makeItemAndFillValue(null);

        this.personId = personId;
        controller.getCaseFilterByPersonId(personId, new FluentCallback<List<FilterShortView>>()
                .withError(throwable -> {
                    fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                })
                .withSuccess(list -> {
                    if (!list.isEmpty()) {
                        itemContainer.clear();
                        list.forEach(this::makeItemAndFillValue);
                    }
                    updateFilterSelectors(list);
                })
        );
    }

    private void makeItemAndFillValue(FilterShortView value) {
        PersonCaseFilterItem personCaseFilterItem = itemProvider.get();
        personCaseFilterItem.setValue(value);
        personCaseFilterItem.setCallback(new PersonCaseFilterCallbacks() {
            @Override
            public void add(Long caseFilterId) {
                controller.addPersonToCaseFilter(personId, caseFilterId, new FluentCallback<Boolean>()
                        .withError(throwable -> fireEvent(new NotifyEvents.Show(lang.errPersonCaseFilterChangeError(), NotifyEvents.NotifyType.ERROR)))
                        .withSuccess(b -> {
                            fireEvent(new NotifyEvents.Show(lang.personCaseFilterChange(), NotifyEvents.NotifyType.SUCCESS));
                            updateFilterSelectors();
                        }));
            }

            @Override
            public void remove(Long caseFilterId) {
                controller.removePersonToCaseFilter(personId, caseFilterId, new FluentCallback<Long>()
                        .withError(throwable -> fireEvent(new NotifyEvents.Show(lang.errPersonCaseFilterChangeError(), NotifyEvents.NotifyType.ERROR)))
                        .withSuccess(result -> {
                            fireEvent(new NotifyEvents.Show(lang.personCaseFilterChange(), NotifyEvents.NotifyType.SUCCESS));
                            updateFilterSelectors();
                        }));
                if (itemContainer.getWidgetCount() > 1) {
                    itemContainer.remove(personCaseFilterItem);
                }
            }

            @Override
            public void change(Long oldCaseFilterId, Long newCaseFilterId) {
                controller.changePersonToCaseFilter(personId, oldCaseFilterId, newCaseFilterId, new FluentCallback<Boolean>()
                        .withError(throwable -> fireEvent(new NotifyEvents.Show(lang.errPersonCaseFilterChangeError(), NotifyEvents.NotifyType.ERROR)))
                        .withSuccess(b -> {
                            fireEvent(new NotifyEvents.Show(lang.personCaseFilterChange(), NotifyEvents.NotifyType.SUCCESS));
                            updateFilterSelectors();
                        }));
            }
        });
        itemContainer.add(personCaseFilterItem);
    }

    private void updateFilterSelectors() {
        controller.getCaseFilterByPersonId(personId, new FluentCallback<List<FilterShortView>>()
                .withError(throwable -> fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR)))
                .withSuccess(list -> updateFilterSelectors(list))
        );
    }

    private void updateFilterSelectors(List<FilterShortView> filters) {
        if (CollectionUtils.isEmpty(filters)) {
            itemContainer.forEach(item -> ((PersonCaseFilterItem)item).setSelectorFilter(null));
        } else {
            itemContainer.forEach(item -> ((PersonCaseFilterItem)item).setSelectorFilter(value -> !filters.contains(value)));
        }
    }

    private void setTestAttributes() {
        itemContainer.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.PERSON_CASE_FILTER.ITEM_CONTAINER);
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
