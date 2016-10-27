package ru.protei.portal.ui.company.client.widget.group.inputSelector;

import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CompanyGroup;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.client.widget.selector.input.InputSelector;
import ru.protei.portal.ui.company.client.widget.group.GroupModel;

import java.util.List;

/**
 * Селектор списка групп компаний c возможностью ввода текста
 */
public class GroupInputSelector extends InputSelector<CompanyGroup> implements ModelSelector<CompanyGroup> {

    @Inject
    public void init( GroupModel groupModel) {
        groupModel.subscribe( this );
    }

    @Override
    public void fillOptions( List< CompanyGroup > groups ) {
        clearOptions();

        for ( CompanyGroup group : groups ) {
            addOption( group.getName(), group );
        }
    }

    public void addAndSetOption(CompanyGroup newCompanyGroup){
        addOption(newCompanyGroup.getName(), newCompanyGroup);
        setValue(newCompanyGroup);
    }


}
