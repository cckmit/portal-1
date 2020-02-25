package ru.protei.portal.ui.issueassignment.client.widget.popupselector.state;

import com.google.gwt.user.client.ui.UIObject;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.ui.common.client.lang.En_CaseStateLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.issuestate.StateSelectorModel;
import ru.protei.portal.ui.issueassignment.client.widget.popupselector.PopupMultiSelector;

import java.util.Set;
import java.util.function.Consumer;

public class DeskStateMultiPopup extends PopupMultiSelector<En_CaseState> {

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
    public void onUnload() {
        super.onUnload();
        model.clear();
    }

    private StateSelectorModel model;
}
