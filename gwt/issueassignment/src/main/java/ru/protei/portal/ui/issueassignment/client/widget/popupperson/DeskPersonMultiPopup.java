package ru.protei.portal.ui.issueassignment.client.widget.popupperson;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.UIObject;
import com.google.inject.Inject;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.AbstractPopupSelector;
import ru.protei.portal.ui.common.client.selector.SelectorItem;
import ru.protei.portal.ui.common.client.selector.pageable.AbstractPageableSelector;
import ru.protei.portal.ui.common.client.selector.pageable.MultiValuePageableSelector;
import ru.protei.portal.ui.common.client.widget.selector.item.PopupSelectableItem;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeModel;

import java.util.Set;
import java.util.function.Consumer;

public class DeskPersonMultiPopup extends AbstractPopupSelector<PersonShortView> implements HasValue<Set<PersonShortView>> {

    @Inject
    public void init(EmployeeModel model, Lang lang) {
        this.model = model;
        setAsyncModel(model);
        setFilter(personView -> !personView.isFired());
        setItemRenderer(PersonShortView::getName);
        setPageSize(CrmConstants.DEFAULT_SELECTOR_PAGE_SIZE);
        setEmptyListText(lang.emptySelectorList());
        setEmptySearchText(lang.searchNoMatchesFound());
    }

    public void show(UIObject relative, Consumer<Set<PersonShortView>> onDone) {
        this.relative = relative;
        setPopupUnloadHandler(() -> onDone.accept(getValue()));
        getPopup().getChildContainer().clear();
        getSelector().fillFromBegin(this);
        getPopup().showNear(relative);
    }

    @Override
    public Set<PersonShortView> getValue() {
        return selector.getValue();
    }

    @Override
    public void setValue(Set<PersonShortView> value) {
        setValue(value, false);
    }

    @Override
    public void setValue(Set<PersonShortView> value, boolean fireEvents) {
        selector.setValue(value);
        if (fireEvents) {
            ValueChangeEvent.fire(this, selector.getValue());
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Set<PersonShortView>> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public boolean isEnabled() { return true; }

    @Override
    public void setEnabled(boolean enabled) {}

    @Override
    protected void onSelectionChanged() {
        getPopup().showNear(relative);
        ValueChangeEvent.fire(this, getValue());
    }

    @Override
    protected SelectorItem<PersonShortView> makeSelectorItem(PersonShortView element, String elementHtml) {
        PopupSelectableItem<PersonShortView> item = new PopupSelectableItem<>();
        item.setElementHtml(elementHtml);
        item.setSelected(isSelected(element));
        return item;
    }

    @Override
    protected AbstractPageableSelector<PersonShortView> getSelector() {
        return selector;
    }

    @Override
    public void onUnload() {
        super.onUnload();
        model.clear();
    }

    @Override
    public boolean isAttached() {
        return true;
    }

    private MultiValuePageableSelector<PersonShortView> selector = new MultiValuePageableSelector<>();
    private UIObject relative;
    private EmployeeModel model;
}
