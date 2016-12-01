package ru.protei.portal.ui.company.client.widget.category.buttonselector;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CompanyCategory;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;
import ru.protei.portal.ui.company.client.widget.category.CategoryModel;

import java.util.List;

/**
 * Селектор списка категорий компаний
 */
public class CategoryButtonSelector extends ButtonSelector< EntityOption > implements ModelSelector< EntityOption > {

    @Inject
    public void init( CategoryModel categoryModel ) {
        categoryModel.subscribe( this );
        setHasNullValue( false );
    }

    public void fillOptions( List< EntityOption > options ) {
        clearOptions();

        for ( EntityOption option : options ) {
            addOption( option.getDisplayText(), option );
        }
    }
}
