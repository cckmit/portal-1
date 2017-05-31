package ru.protei.portal.ui.company.client.widget.category.btngroup;

import com.google.inject.Inject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.client.widget.togglebtn.group.ToggleBtnGroupMulti;
import ru.protei.portal.ui.company.client.widget.category.CategoryModel;

import java.util.List;

/**
 * Created by turik on 13.10.16.
 */
public class CategoryBtnGroupMulti extends ToggleBtnGroupMulti< EntityOption > implements ModelSelector< EntityOption > {

    @Inject
    public void init( CategoryModel categoryModel) {
        categoryModel.subscribe( this );
    }

    @Override
    public void fillOptions( List< EntityOption > options ) {
        clear();

        for ( EntityOption option : options ) {
            addBtn( option.getDisplayText(), option );
        }
    }
}
