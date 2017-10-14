package ru.protei.portal.ui.common.client.widget.selector.company;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.Arrays;
import java.util.List;

/**
 * Селектор списка компаний
 */
public class CompanySelector extends ButtonSelector< EntityOption > implements ModelSelector <EntityOption> {

    @Inject
    public void init( CompanyModel companyModel ) {
        this.model = companyModel;
        model.subscribe(this, categories);

        setSearchEnabled( true );
        setSearchAutoFocus( true );

        setDisplayOptionCreator( value -> new DisplayOption( value == null ? defaultValue : value.getDisplayText() ) );
    }

    public void fillOptions( List< EntityOption > options ) {
        clearOptions();

        if( defaultValue != null ) {
            addOption( null );
            setValue( null );
        }

        options.forEach(this :: addOption);
    }

    public void setDefaultValue( String value ) {
        this.defaultValue = value;
    }

    public void setCategories(List<En_CompanyCategory> categories) {
        this.categories = categories;
    }

    private List<En_CompanyCategory> categories = Arrays.asList(
            En_CompanyCategory.CUSTOMER, En_CompanyCategory.PARTNER,
            En_CompanyCategory.SUBCONTRACTOR);

    private CompanyModel model;
    private String defaultValue = null;
}
