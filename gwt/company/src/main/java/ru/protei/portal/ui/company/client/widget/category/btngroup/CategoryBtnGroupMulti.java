package ru.protei.portal.ui.company.client.widget.category.btngroup;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.test.client.DebugIdsHelper;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.client.widget.togglebtn.group.ToggleBtnGroupMulti;
import ru.protei.portal.ui.company.client.widget.category.CategoryModel;

import java.util.List;

/**
 * Created by turik on 13.10.16.
 */
public class CategoryBtnGroupMulti extends ToggleBtnGroupMulti< EntityOption > implements SelectorWithModel< EntityOption > {

    @Inject
    public void init( CategoryModel categoryModel) {
        categoryModel.subscribe( this );
    }

    @Override
    public void fillOptions( List< EntityOption > options ) {
        clear();

        for ( EntityOption option : options ) {
            En_CompanyCategory category = En_CompanyCategory.findById(option.getId());
            if ( En_CompanyCategory.OFFICIAL.equals( category ) || category == null ) {
                continue;
            }

            addBtnWithImage( "./images/company_" + category.name().toLowerCase() + ".svg" , "btn btn-default no-border company-category", null, option, null );
            setEnsureDebugId(option, DebugIdsHelper.COMPANY_CATEGORY_BUTTON.byId(option.getId()));
        }
    }
}
