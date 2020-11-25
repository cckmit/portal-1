package ru.protei.portal.ui.common.client.widget.selector.person;

import com.google.inject.Inject;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.selector.SelectorItem;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonPopupSingleSelector;

/**
 * Селектор сотрудников домашней компании
 * EmployeeCustomModel - не синглтон и для каждого селектора создается своя модель
 */
public class EmployeeCustomButtonSelector extends ButtonPopupSingleSelector<PersonShortView> {

    @Inject
    public void init(EmployeeCustomModel model) {
        this.model = model;
        setAsyncModel(model);
        setFilter(personView -> !personView.isFired());
        setItemRenderer(value -> value == null ? defaultValue : value.getDisplayName());
    }

    @Override
    protected SelectorItem makeSelectorItem(PersonShortView value, String elementHtml, String title ) {
        return PersonSelectorItemRenderer.makeSingleSelectorItem(value, elementHtml);
    }

    @Override
    public void onUnload() {
        super.onUnload();
        model.clear();
    }

    public void setEmployeeQuery(EmployeeQuery query) {
        if (model != null) {
            model.setEmployeeQuery(query);
        }
    }

    private EmployeeCustomModel model;
}
