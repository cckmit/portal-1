package ru.protei.portal.ui.common.client.widget.selector.person;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.inject.Inject;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.input.InputPopupMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.item.SelectorItem;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Селектор сотрудников
 */
public class InitiatorMultiSelector extends InputPopupMultiSelector<PersonShortView> implements Refreshable
{
    @Inject
    public void init(InitiatorModel model, Lang lang) {
        this.model = model;
        this.lang = lang;
        setModel( model );
        setAddName( lang.buttonAdd() );
        setClearName( lang.buttonClear() );

        setItemRenderer( person -> person.getName() );
    }

    @Override
    public void onShowPopupClicked(ClickEvent event) {
        Collection companies = companiesSupplier.get();
        if (!CollectionUtils.isEmpty( companies )) {
            super.onShowPopupClicked(event);
        } else {
            SelectorItem item = new SelectorItem();
            item.setName(lang.initiatorSelectACompany());
            item.getElement().addClassName(UiConstants.Styles.TEXT_CENTER);
            getPopup().getChildContainer().add(item);
            getPopup().showNear( itemContainer );
        }
    }

    @Override
    public void refresh() {
        Set<PersonShortView> value = getValue();
        if (!CollectionUtils.isEmpty( value )) {
            value.retainAll( model.getValues() );
        }
        setValue( value );
    }

    public void updateCompanies() {
        if (model == null || companiesSupplier == null) {
            return;
        }
        Set<Long> companyIds = null;
        Set<EntityOption> companies = companiesSupplier.get();
        if (CollectionUtils.isEmpty( companies )) {
            setValue(null);
        } else {
            companyIds = companies.stream().map(EntityOption::getId).collect(Collectors.toSet());
        }

        model.updateCompanies(this, companyIds, false);

    }

    public void setCompaniesSupplier(Supplier<Set<EntityOption>> companiesSupplier) {
        this.companiesSupplier = companiesSupplier;
    }

    Lang lang;
    private InitiatorModel model;


    private Supplier<Set<EntityOption>> companiesSupplier = new Supplier<Set<EntityOption>>() {
        @Override
        public Set<EntityOption> get() {
            return Collections.EMPTY_SET;
        }
    };
}
