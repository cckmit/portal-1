package ru.protei.portal.ui.company.client.widget.category.buttonselector;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.lang.En_CompanyCategoryLang;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;
import ru.protei.portal.ui.company.client.widget.category.CategoryModel;

import java.util.List;

/**
 * Селектор списка категорий компаний
 */
public class CategoryButtonSelector extends ButtonSelector< En_CompanyCategory > implements SelectorWithModel< En_CompanyCategory > {

    @Inject
    public void init( CategoryModel categoryModel ) {
        categoryModel.subscribe( this );
        setHasNullValue( false );

        setDisplayOptionCreator( value -> new DisplayOption( value == null ? UiConstants.UNDEFINED_ENTRY : lang.getName( value ) ));
    }

    public void fillOptions( List< En_CompanyCategory > options ) {
        clearOptions();

        for ( En_CompanyCategory category : options ) {
            if ( En_CompanyCategory.OFFICIAL == category ) {
                continue;
            }
            addOption( category );
        }
    }

    @Inject
    En_CompanyCategoryLang lang;
}
