package ru.protei.portal.ui.company.client.widget.group.inputselector;

import com.google.inject.Inject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.client.widget.selector.input.InputSelector;
import ru.protei.portal.ui.company.client.widget.group.GroupModel;

import java.util.List;

/**
 * Селектор списка групп компаний c возможностью ввода текста
 */
public class GroupInputSelector extends InputSelector< EntityOption > implements ModelSelector< EntityOption > {

    @Inject
    public void init( GroupModel groupModel) {
        groupModel.subscribe( this );
    }

    @Override
    public void fillOptions( List< EntityOption > options ) {
        clearOptions();

        addHiddenOption("", new EntityOption()); // add empty option
        setNullOption("");

        for ( EntityOption option : options ) {
            addOption( option.getDisplayText(), option );
        }
    }

    public void addAndSetOption( EntityOption newCompanyGroup ){
        addOption( newCompanyGroup.getDisplayText(), newCompanyGroup );
        setValue( newCompanyGroup );
    }


}
