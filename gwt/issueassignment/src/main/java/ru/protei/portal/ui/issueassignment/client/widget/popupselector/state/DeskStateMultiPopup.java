package ru.protei.portal.ui.issueassignment.client.widget.popupselector.state;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.inject.Inject;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.SelectorPopup;
import ru.protei.portal.ui.common.client.widget.composite.popper.PopperComposite;
import ru.protei.portal.ui.common.client.widget.issuestate.StateSelectorModel;
import ru.protei.portal.ui.issueassignment.client.widget.popupselector.PopupMultiSelector;

import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;

public class DeskStateMultiPopup extends PopupMultiSelector<EntityOption> {

    @Inject
    public void init(StateSelectorModel model, Lang lang) {
        this.model = model;
        setAsyncModel(model);
        setItemRenderer(EntityOption::getDisplayText);
        setPageSize(CrmConstants.DEFAULT_SELECTOR_PAGE_SIZE);
        setEmptyListText(lang.emptySelectorList());
        setEmptySearchText(lang.searchNoMatchesFound());
    }

    @Override
    public void onPopupHide(SelectorPopup selectorPopup) {
        super.onPopupHide(selectorPopup);
        getPopup().asWidget().getElement().removeFromParent();
    }

    public void show(Element relative, Collection<EntityOption> exclude, Consumer<Set<EntityOption>> onDone) {
        this.relative = relative;
        setFilter(state -> !exclude.contains(state));
        setPopupUnloadHandler(() -> onDone.accept(getValue()));
        getPopup().getChildContainer().clear();
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

    private StateSelectorModel model;
}
