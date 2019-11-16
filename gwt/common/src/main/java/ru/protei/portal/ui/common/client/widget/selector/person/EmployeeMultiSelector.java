package ru.protei.portal.ui.common.client.widget.selector.person;

import com.google.inject.Inject;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.components.client.input.InputPopupMultiSelector;

/**
 * Селектор сотрудников
 */
public class EmployeeMultiSelector
    extends InputPopupMultiSelector<PersonShortView>
{

    @Inject
    public void init(EmployeeModel model, Lang lang) {
        this.model = model;
        setAsyncSelectorModel( model);
        setAddName(lang.buttonAdd());
        setClearName(lang.buttonClear());
        setFilter(personView -> !personView.isFired());
        setPageSize( CrmConstants.DEFAULT_SELECTOR_PAGE_SIZE );
        setSelectorItemRenderer( p -> p == null ? lang.employeeWithoutManager() : p.getDisplayShortName() );
    }

    @Override
    public void onUnload() {
        model.clear();
    }

    public void setFiredEmployeesVisible(boolean firedEmployeesVisible) {
        if (firedEmployeesVisible) {
            setFilter(personView -> true);
        }
    }

    private EmployeeModel model;
}
