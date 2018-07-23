package ru.protei.portal.ui.common.client.widget.selector.person;

import com.google.inject.Inject;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.client.widget.selector.input.MultipleInputSelector;

import java.util.Collection;
import java.util.List;

/**
 * Селектор сотрудников
 */
public class EmployeeMultiSelector
        extends MultipleInputSelector<PersonShortView>
        implements ModelSelector<PersonShortView>
{
    @Inject
    public void init(EmployeeModel model, Lang lang ) {
        model.subscribe( this );
        setAddName( lang.buttonAdd() );
        setClearName( lang.buttonClear() );
    }

    @Override
    public void fillOptions( List< PersonShortView > options ) {
        clearOptions();

        for ( PersonShortView type : options ) {
            addOption( type.getDisplayShortName(), type );
        }
    }

    @Override
    public void refreshValue() {

    }
}
