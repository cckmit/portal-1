package ru.protei.portal.ui.company.client.widget.category.btngroup;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.test.client.DebugIdsHelper;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.client.widget.togglebtn.group.ToggleBtnGroupMulti;
import ru.protei.portal.ui.company.client.widget.category.CategoryModel;

import java.util.List;

/**
 * Created by turik on 13.10.16.
 */
public class CategoryBtnGroupMulti extends ToggleBtnGroupMulti< En_CompanyCategory > implements SelectorWithModel< En_CompanyCategory > {

    @Inject
    public void init( CategoryModel categoryModel) {
        categoryModel.subscribe( this );
    }

    @Override
    public void fillOptions( List< En_CompanyCategory > options ) {
        clear();

        for ( En_CompanyCategory category : options ) {
            if ( En_CompanyCategory.OFFICIAL.equals( category ) || category == null ) {
                continue;
            }

            addBtnWithImage( "./images/company_" + category.name().toLowerCase() + ".svg" , "btn btn-default no-border company-category", null, category, null );
            setEnsureDebugId(category, byId(category.getId()));
        }
    }

    public static String byId(int id) {
        if (id == 1) {
            return DebugIds.COMPANY_CATEGORY_BUTTON.CUSTOMER;
        }
        if (id == 2) {
            return DebugIds.COMPANY_CATEGORY_BUTTON.PARTNER;
        }
        if (id == 3) {
            return DebugIds.COMPANY_CATEGORY_BUTTON.SUBCONTRACTOR;
        }
        if (id == 4) {
            return DebugIds.COMPANY_CATEGORY_BUTTON.OFFICIAL;
        }
        if (id == 5) {
            return DebugIds.COMPANY_CATEGORY_BUTTON.HOME_COMPANY;
        }
        return DebugIds.COMPANY_CATEGORY_BUTTON.DEFAULT + id;
    }


}
