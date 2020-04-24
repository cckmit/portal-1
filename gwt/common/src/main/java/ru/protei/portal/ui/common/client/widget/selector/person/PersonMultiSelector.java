package ru.protei.portal.ui.common.client.widget.selector.person;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.inject.Inject;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.util.TransliterationUtils;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.input.InputPopupMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.item.PopupSelectableItem;
import ru.protei.portal.ui.common.client.widget.selector.item.SelectorItem;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Селектор контактов
 */
public class PersonMultiSelector extends InputPopupMultiSelector<PersonShortView> implements Refreshable
{
    @Inject
    public void init(Lang lang) {
        this.lang = lang;
        setAddName( lang.buttonAdd() );
        setClearName( lang.buttonClear() );
        setItemRenderer(this::makeName);
    }

    private String makeName(PersonShortView personShortView) {
        return TransliterationUtils.transliterate(personShortView.getName(), LocaleInfo.getCurrentLocale().getLocaleName());
    }

    @Override
    public void onShowPopupClicked(ClickEvent event) {
        if (initiatorModel == null) {
            super.onShowPopupClicked(event);
            return;
        }

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
            value.retainAll( initiatorModel.getValues() );
        }
        setValue( value );
    }

    @Override
    protected ru.protei.portal.ui.common.client.selector.SelectorItem<PersonShortView> makeSelectorItem(PersonShortView value, String elementHtml) {
        PopupSelectableItem<PersonShortView> item = new PopupSelectableItem<>();

        if (value != null && value.isFired()) {
            item.setIcon("fa fa-ban ban m-r-5");
        }

        item.setElementHtml(elementHtml);
        item.setSelected(isSelected(value));
        return item;
    }

    public void updateCompanies() {
        if (initiatorModel == null || companiesSupplier == null) {
            return;
        }
        Set<Long> companyIds = null;
        Set<EntityOption> companies = companiesSupplier.get();
        if (CollectionUtils.isEmpty( companies )) {
            setValue(null);
        } else {
            companyIds = companies.stream().map(EntityOption::getId).collect(Collectors.toSet());
        }

        initiatorModel.updateCompanies(this, companyIds, null);

    }

    public void setCompaniesSupplier(Supplier<Set<EntityOption>> companiesSupplier) {
        this.companiesSupplier = companiesSupplier;
    }

    public void setInitiatorModel(InitiatorModel model) {
        this.initiatorModel = model;
        setModel( model );
    }

    public void setPersonModel(PersonModel model) {
        setAsyncSearchModel(model);
    }

    Lang lang;
    private InitiatorModel initiatorModel;

    private Supplier<Set<EntityOption>> companiesSupplier = () -> Collections.EMPTY_SET;
}
