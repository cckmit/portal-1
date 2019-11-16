package ru.protei.portal.ui.common.client.widget.selector.person;

import com.google.inject.Inject;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.components.client.input.InputPopupMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.client.widget.selector.input.MultipleInputSelector;

import java.util.List;
import java.util.logging.Logger;

/**
 * Селектор сотрудников
 */
public class EmployeeMultiSelector
//        extends MultipleInputSelector<PersonShortView>
    extends InputPopupMultiSelector<PersonShortView>
//        implements SelectorWithModel<PersonShortView>
{

    @Inject
    public void init(EmployeeModel model, Lang lang) {
        this.model = model;
        this.lang = lang;
        setSelectorModel(model);
        setAddName(lang.buttonAdd());
        setClearName(lang.buttonClear());
        setFilter(personView -> !personView.isFired());
        setSelectorItemRenderer( p -> p == null ? lang.employeeWithoutManager() : p.getDisplayShortName() );
    }

//    @Override
//    public void fillOptions(List< PersonShortView > options) {
//        clearOptions();
//
//        if (hasWithoutValue) {
//            addOption(lang.employeeWithoutManager(), new PersonShortView(lang.employeeWithoutManager(), CrmConstants.Employee.UNDEFINED));
//        }
//        for (PersonShortView personView : options) {
//            addOption(personView.getDisplayShortName(), personView);
//        }
//    }

    @Override
    public void onLoad() {
        model.refreshOptions(  );
    }

    public void setHasWithoutValue( boolean hasWithoutValue) {
        this.hasWithoutValue = hasWithoutValue;
    }

    public void setFiredEmployeesVisible(boolean firedEmployeesVisible) {
        if (firedEmployeesVisible) {
            setFilter(personView -> true);
        }
    }



    private static final Logger log = Logger.getLogger( EmployeeMultiSelector.class.getName() );

    private EmployeeModel model;
    private Lang lang;
    private boolean hasWithoutValue = false;
}
