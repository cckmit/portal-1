package ru.protei.portal.ui.issueassignment.client.widget.popupselector.person;

import com.google.gwt.user.client.ui.UIObject;
import com.google.inject.Inject;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeModel;
import ru.protei.portal.ui.issueassignment.client.widget.popupselector.PopupMultiSelector;

import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;

public class DeskPersonMultiPopup extends PopupMultiSelector<PersonShortView> {

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

    public void show(UIObject relative, Collection<PersonShortView> exclude, Consumer<Set<PersonShortView>> onDone) {
        this.relative = relative;
        setFilter(personView -> !personView.isFired() && !exclude.contains(personView));
        setPopupUnloadHandler(() -> onDone.accept(getValue()));
        getPopup().getChildContainer().clear();
        getSelector().fillFromBegin(this);
        getPopup().showNear(relative);
    }

    @Override
    public void onUnload() {
        super.onUnload();
        model.clear();
    }

    private EmployeeModel model;
}
