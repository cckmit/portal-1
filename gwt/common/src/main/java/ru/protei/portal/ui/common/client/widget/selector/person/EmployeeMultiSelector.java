package ru.protei.portal.ui.common.client.widget.selector.person;

import com.google.inject.Inject;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.SelectorItem;
import ru.protei.portal.ui.common.client.widget.selector.input.InputPopupMultiSelector;

/**
 * Селектор сотрудников
 */
public class EmployeeMultiSelector
    extends InputPopupMultiSelector<PersonShortView>
{

    @Inject
    public void init(EmployeeModel model, Lang lang) {
        this.model = model;
        setAsyncModel( model);
        setAddName(lang.buttonAdd());
        setClearName(lang.buttonClear());
        setFilter(personView -> !personView.isFired());
        setItemRenderer(PersonShortView::getName);
        setNullItem(() -> new PersonShortView(lang.employeeWithoutManager(), CrmConstants.Employee.UNDEFINED));
    }

    @Override
    protected SelectorItem<PersonShortView> makeSelectorItem( PersonShortView value, String elementHtml ) {
        return PersonSelectorItemRenderer.makeMultipleSelectorItem(value, elementHtml, isSelected(value));
    }

    @Override
    public void onUnload() {
        super.onUnload();
        model.clear();
    }

    public void setFiredEmployeesVisible(boolean firedEmployeesVisible) {
        if (firedEmployeesVisible) {
            setFilter(null);
        }
    }

    private EmployeeModel model;
}
