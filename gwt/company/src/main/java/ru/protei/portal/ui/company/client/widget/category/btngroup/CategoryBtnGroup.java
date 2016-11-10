package ru.protei.portal.ui.company.client.widget.category.btngroup;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CompanyCategory;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.client.widget.togglebtngroup.ToggleBtnGroup;
import ru.protei.portal.ui.company.client.widget.category.CategoryModel;

import java.util.List;

/**
 * Created by turik on 13.10.16.
 */
public class CategoryBtnGroup extends ToggleBtnGroup< CompanyCategory > implements ModelSelector< CompanyCategory > {

    @Inject
    public void init( CategoryModel categoryModel) {
        categoryModel.subscribe( this );
    }

    @Override
    public void fillOptions( List< CompanyCategory > categories ) {
        clear();

        for ( CompanyCategory category : categories ) {
            addBtn( category.getName(), category );
        }
    }
}
