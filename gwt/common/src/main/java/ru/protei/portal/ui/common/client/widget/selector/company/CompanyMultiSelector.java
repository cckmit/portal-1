package ru.protei.portal.ui.common.client.widget.selector.company;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.client.widget.selector.input.MultipleInputSelector;

import java.util.Arrays;
import java.util.List;

/**
 * Мультиселектор компаний
 */
public class CompanyMultiSelector extends MultipleInputSelector< EntityOption > implements SelectorWithModel< EntityOption > {

    @Inject
    public void init( CompanyModel model, Lang lang ) {
        model.subscribe( this, categories );
        setSelectorModel(model);
        setAddName( lang.buttonAdd() );
        setClearName( lang.buttonClear() );
    }

    public void fillOptions( List< EntityOption > options ) {
        clearOptions();
        this.options = options;
    }

    @Override
    protected void showPopup( IsWidget relative ) {
        for ( EntityOption option : options ) {
            addOption( option.getDisplayText(), option );
        }
        options.clear();
        super.showPopup( relative );
    }

    @Override
    public void refreshValue() {}

    private List<En_CompanyCategory > categories = Arrays.asList(
            En_CompanyCategory.CUSTOMER,
            En_CompanyCategory.PARTNER,
            En_CompanyCategory.SUBCONTRACTOR,
            En_CompanyCategory.HOME);
    private List<EntityOption> options;
}