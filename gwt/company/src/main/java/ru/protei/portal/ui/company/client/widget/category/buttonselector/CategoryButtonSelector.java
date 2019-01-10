package ru.protei.portal.ui.company.client.widget.category.buttonselector;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;
import ru.protei.portal.ui.company.client.widget.category.CategoryModel;

import java.util.List;

/**
 * Селектор списка категорий компаний
 */
public class CategoryButtonSelector extends ButtonSelector< EntityOption > implements SelectorWithModel< EntityOption > {

    @Inject
    public void init( CategoryModel categoryModel ) {
        categoryModel.subscribe( this );
        setHasNullValue( false );

        setDisplayOptionCreator( value -> new DisplayOption( value == null ? UiConstants.UNDEFINED_ENTRY : value.getDisplayText() ));
    }

    public void fillOptions( List< EntityOption > options ) {
        clearOptions();

        for ( EntityOption option : options ) {
            if ( option.getId().equals( En_CompanyCategory.OFFICIAL.getId() )) {
                continue;
            }
            addOption( option );
        }
    }
}
