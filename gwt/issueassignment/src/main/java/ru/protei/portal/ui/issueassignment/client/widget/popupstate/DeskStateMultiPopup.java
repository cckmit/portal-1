package ru.protei.portal.ui.issueassignment.client.widget.popupstate;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.UIObject;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.ui.common.client.lang.En_CaseStateLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.AbstractPopupSelector;
import ru.protei.portal.ui.common.client.selector.SelectorItem;
import ru.protei.portal.ui.common.client.selector.pageable.AbstractPageableSelector;
import ru.protei.portal.ui.common.client.selector.pageable.MultiValuePageableSelector;
import ru.protei.portal.ui.common.client.widget.issuestate.StateSelectorModel;
import ru.protei.portal.ui.common.client.widget.selector.item.PopupSelectableItem;

import java.util.Set;
import java.util.function.Consumer;

public class DeskStateMultiPopup extends AbstractPopupSelector<En_CaseState> implements HasValue<Set<En_CaseState>> {

    @Inject
    public void init(StateSelectorModel model, Lang lang, En_CaseStateLang caseStateLang) {
        this.model = model;
        setAsyncModel(model);
        setItemRenderer(caseStateLang::getStateName);
        setPageSize(CrmConstants.DEFAULT_SELECTOR_PAGE_SIZE);
        setEmptyListText(lang.emptySelectorList());
        setEmptySearchText(lang.searchNoMatchesFound());
    }

    public void show(UIObject relative, Consumer<Set<En_CaseState>> onDone) {
        this.relative = relative;
        setPopupUnloadHandler(() -> onDone.accept(getValue()));
        getPopup().getChildContainer().clear();
        getSelector().fillFromBegin(this);
        getPopup().showNear(relative);
    }

    @Override
    public Set<En_CaseState> getValue() {
        return selector.getValue();
    }

    @Override
    public void setValue(Set<En_CaseState> value) {
        setValue(value, false);
    }

    @Override
    public void setValue(Set<En_CaseState> value, boolean fireEvents) {
        selector.setValue(value);
        if (fireEvents) {
            ValueChangeEvent.fire(this, selector.getValue());
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Set<En_CaseState>> handler) {
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
    protected SelectorItem<En_CaseState> makeSelectorItem(En_CaseState element, String elementHtml) {
        PopupSelectableItem<En_CaseState> item = new PopupSelectableItem<>();
        item.setElementHtml(elementHtml);
        item.setSelected(isSelected(element));
        return item;
    }

    @Override
    protected AbstractPageableSelector<En_CaseState> getSelector() {
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

    private MultiValuePageableSelector<En_CaseState> selector = new MultiValuePageableSelector<>();
    private UIObject relative;
    private StateSelectorModel model;
}
