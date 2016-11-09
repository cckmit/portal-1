package ru.protei.portal.ui.company.client.widget.category.buttonselector;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CompanyCategory;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;
import ru.protei.portal.ui.company.client.widget.category.CategoryModel;

import java.util.List;

/**
 * Селектор списка категорий компаний
 */
public class CategoryButtonSelector extends ButtonSelector< CompanyCategory > implements ModelSelector< CompanyCategory > {

    @Inject
    public void init( CategoryModel categoryModel ) {
        categoryModel.subscribe( this );
    }

    public void fillOptions( List< CompanyCategory > categories ) {
        clearOptions();

        int rowNum = 0;
        for ( CompanyCategory category : categories ) {
            addOption( category.getName(), category );
            if ( ++ rowNum == 1 ) {
                firstCategory = category;
            }
        }
    }

    public CompanyCategory getFirstCategory() {
        return firstCategory;
    }

    private CompanyCategory firstCategory;
}
