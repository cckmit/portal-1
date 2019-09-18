package ru.protei.portal.ui.common.client.widget.selector.person;

import com.google.inject.Inject;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.client.widget.selector.input.MultipleInputSelector;

import java.util.List;

/**
 * Селектор сотрудников
 */
public class EmployeeMultiSelector
        extends MultipleInputSelector<PersonShortView>
        implements SelectorWithModel<PersonShortView>
{

    @Inject
    public void init(EmployeeModel model, Lang lang) {
        this.lang = lang;

        setSelectorModel(model);
        setAddName(lang.buttonAdd());
        setClearName(lang.buttonClear());
        setFilter(personView -> !personView.isFired());
    }

    @Override
    public void fillOptions(List< PersonShortView > options) {
        clearOptions();

        if (hasWithoutValue) {
            addOption(lang.employeeWithoutManager(), new PersonShortView(lang.employeeWithoutManager(), CrmConstants.Employee.UNDEFINED));
        }
        for (PersonShortView personView : options) {
            addOption(personView.getDisplayShortName(), personView);
        }
    }

    public void setHasWithoutValue(boolean hasWithoutValue) {
        this.hasWithoutValue = hasWithoutValue;
    }

    public void setFiredEmployeesVisible(boolean firedEmployeesVisible) {
        if (firedEmployeesVisible) {
            setFilter(personView -> true);
        }
    }

    private Lang lang;
    private boolean hasWithoutValue = false;
}
