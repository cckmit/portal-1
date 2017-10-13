package ru.protei.portal.ui.common.client.widget.selector.company;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.lang.Lang;
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
        setSearchEnabled( true );
        setSearchAutoFocus( true );
    }

    public void subscribeToModel() {
        model.subscribe(this, categories);
    }

    public void fillOptions( List< EntityOption > options ) {
        clearOptions();

        if(defaultValue != null) {
            addOption( defaultValue, null );
            setValue(null);
        }

        options.forEach(option -> addOption(option.getDisplayText(),option));
    }

    public void setDefaultValue( String value ) {
        this.defaultValue = value;
    }

    public void setCategories(List<En_CompanyCategory> categories) {
        this.categories = categories;
    }

    @Inject
    Lang lang;

    private String defaultValue = null;

    private List<En_CompanyCategory> categories = Arrays.asList(
            En_CompanyCategory.CUSTOMER, En_CompanyCategory.PARTNER,
            En_CompanyCategory.SUBCONTRACTOR);
    private CompanyModel model;
}
