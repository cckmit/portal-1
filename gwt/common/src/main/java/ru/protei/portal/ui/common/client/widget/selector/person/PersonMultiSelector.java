package ru.protei.portal.ui.common.client.widget.selector.person;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.inject.Inject;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.util.TransliterationUtils;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.input.InputPopupMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.item.PopupSelectableItem;
import ru.protei.portal.ui.common.client.widget.selector.item.SelectorItem;

import java.util.Collections;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Селектор контактов
 */
public class PersonMultiSelector extends InputPopupMultiSelector<PersonShortView> implements Refreshable {
    @Inject
    public void init(Lang lang) {
        this.lang = lang;
        setAddName( lang.buttonAdd() );
        setClearName( lang.buttonClear() );
        setItemRenderer(this::makeName);
        selectCompanyMessage = lang.initiatorSelectACompany();
    }

    private String makeName(PersonShortView personShortView) {
        return TransliterationUtils.transliterate(personShortView.getName(), LocaleInfo.getCurrentLocale().getLocaleName());
    }

    @Override
    public boolean isValid() {
        return CollectionUtils.isNotEmpty(getValue());
    }

    @Override
    public void onShowPopupClicked(ClickEvent event) {
        if (personModel == null) {
            super.onShowPopupClicked(event);
            return;
        }

        if (personModel.isCompaniesPresent()) {
            super.onShowPopupClicked(event);
        } else {
            SelectorItem item = new SelectorItem();
            item.setName( selectCompanyMessage );
            item.getElement().addClassName(UiConstants.Styles.TEXT_CENTER);
            getPopup().getChildContainer().clear();
            getPopup().getChildContainer().add(item);
            getPopup().showNear( select2.getElement() );
        }
    }

    @Override
    public void refresh() {
        Set<PersonShortView> value = getValue();
        if (!CollectionUtils.isEmpty( value )) {
            value.retainAll( personModel.getValues() );
        }
        setValue( value );
    }

    @Override
    protected ru.protei.portal.ui.common.client.selector.SelectorItem<PersonShortView> makeSelectorItem( PersonShortView value, String elementHtml ) {
        PopupSelectableItem<PersonShortView> item = new PopupSelectableItem<>();

        if (value != null && value.isFired()) {
            item.setIcon("fa fa-ban ban m-r-5");
        }

        item.setElementHtml(elementHtml);
        item.setSelected(isSelected(value));
        return item;
    }

    public void setPersonModel(PersonModel model) {
        this.personModel = model;
        setAsyncModel( model );
    }

    public void setAsyncPersonModel(AsyncPersonModel model) {
        this.asyncPersonModel = model;
        setAsyncSearchModel(model);
    }

    public void setSelectCompanyMessage(String selectCompanyMessage) {
        this.selectCompanyMessage = selectCompanyMessage;
    }

    public void setFiredVisible(boolean isVisible) {
        if (!isVisible) {
            setFilter(personShortView -> !personShortView.isFired());
        } else {
            setFilter(personShortView -> true);
        }
    }

    private Lang lang;
    private PersonModel personModel;
    private AsyncPersonModel asyncPersonModel;
    private String selectCompanyMessage;

    private Supplier<Set<EntityOption>> companiesSupplier1 = () -> Collections.EMPTY_SET;
}
