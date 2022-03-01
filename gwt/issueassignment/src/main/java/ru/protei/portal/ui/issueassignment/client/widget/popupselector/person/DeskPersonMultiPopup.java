package ru.protei.portal.ui.issueassignment.client.widget.popupselector.person;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.SelectorPopup;
import ru.protei.portal.ui.common.client.widget.composite.popper.PopperComposite;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeCustomModel;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeModel;
import ru.protei.portal.ui.issueassignment.client.widget.popupselector.PopupMultiSelector;

import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;

public class DeskPersonMultiPopup extends PopupMultiSelector<PersonShortView> {

    @Inject
    public void init(EmployeeCustomModel model, Lang lang) {
        this.model = model;
        setAsyncModel(model);
        model.setEmployeeQuery(new EmployeeQuery(null, false, null, En_SortField.person_full_name, En_SortDir.ASC));
        setFilter(personView -> !personView.isFired());
        setItemRenderer(PersonShortView::getDisplayName);
        setPageSize(CrmConstants.DEFAULT_SELECTOR_PAGE_SIZE);
        setEmptyListText(lang.emptySelectorList());
        setEmptySearchText(lang.searchNoMatchesFound());
    }

    @Override
    public void onPopupHide(SelectorPopup selectorPopup) {
        super.onPopupHide(selectorPopup);
        getPopup().asWidget().getElement().removeFromParent();
    }

    public void show(Element relative, Collection<PersonShortView> exclude, Consumer<Set<PersonShortView>> onDone) {
        this.relative = relative;
        setFilter(personView -> !personView.isFired() && !exclude.contains(personView));
        setPopupUnloadHandler(() -> onDone.accept(getValue()));
        getPopup().getContainer().clear();
        getSelector().fillFromBegin(this);
        RootPanel.get().add(getPopup());
        relative.appendChild(getPopup().asWidget().getElement());
        getPopup().showNear(relative, PopperComposite.Placement.BOTTOM);
    }

    @Override
    public void onUnload() {
        super.onUnload();
        model.clear();
    }

    private EmployeeCustomModel model;
}
