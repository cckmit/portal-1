package ru.protei.portal.ui.company.client.widget.companycategorybtngroup;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CompanyCategory;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.client.widget.togglebuttongroup.group.ToggleButtonGroup;

import java.util.List;

/**
 * Created by turik on 13.10.16.
 */
public class CompanyCategoryBtnGroup extends ToggleButtonGroup< CompanyCategory > implements ModelSelector< CompanyCategory > {

    @Inject
    public void init( CompanyCategoryModel companyCategoryModel ) {
        companyCategoryModel.subscribe( this );
    }

    @Override
    public void fillOptions( List< CompanyCategory > categories ) {
        clear();

        for ( CompanyCategory category : categories ) {
            addBtn(category.getName(), category);
        }
    }
}
