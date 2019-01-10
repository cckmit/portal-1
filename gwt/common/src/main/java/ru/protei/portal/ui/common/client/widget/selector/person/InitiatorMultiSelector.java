package ru.protei.portal.ui.common.client.widget.selector.person;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.inject.Inject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.client.widget.selector.input.MultipleInputSelector;
import ru.protei.portal.ui.common.client.widget.selector.item.SelectorItem;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.isEmpty;

/**
 * Селектор сотрудников
 */
public class InitiatorMultiSelector
        extends MultipleInputSelector<PersonShortView>
        implements SelectorWithModel<PersonShortView>
{
    @Inject
    public void init(InitiatorModel model, Lang lang) {
        this.model = model;
        this.lang = lang;
        model.subscribe( this );
        setAddName( lang.buttonAdd() );
        setClearName( lang.buttonClear() );
    }

    @Override
    public void fillOptions( List< PersonShortView > options ) {
        clearOptions();

        for ( PersonShortView type : options ) {
            addOption( type.getDisplayShortName(), type );
        }
    }

    @Override
    public void onShowPopupClicked(ClickEvent event) {
        super.onShowPopupClicked(event);
        Collection companies = companiesSupplier.get();
        if(isEmpty(companies)){
            super.clearOptions();
            SelectorItem item = new SelectorItem();
            item.setName(lang.initiatorSelectACompany());
            item.getElement().addClassName(UiConstants.Styles.TEXT_CENTER);
            popup.getChildContainer().add(item);
        }
    }

    @Override
    public void refreshValue() {

    }

    public void setFired ( boolean value ) { this.fired = value; }

    public void updateCompanies() {
        if (model == null || companiesSupplier == null) {
            return;
        }
        Set<Long> companyIds = null;
        Set<EntityOption> companies = companiesSupplier.get();
        if (!isEmpty(companies)) {
            companyIds = companies.stream().map(EntityOption::getId).collect(Collectors.toSet());
        }else{
            clearOptions();
            setValue(null);
        }

        model.updateCompanies(companyIds, fired);

    }

    public void setCompaniesSupplier(Supplier<Set<EntityOption>> companiesSupplier) {
        this.companiesSupplier = companiesSupplier;
    }

    Lang lang;
    private InitiatorModel model;
    private boolean fired;



    private Supplier<Set<EntityOption>> companiesSupplier = new Supplier<Set<EntityOption>>() {
        @Override
        public Set<EntityOption> get() {
            return Collections.EMPTY_SET;
        }
    };
}
