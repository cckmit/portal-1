package ru.protei.portal.ui.common.client.widget.selector.person;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.client.widget.selector.input.MultipleInputSelector;
import ru.protei.portal.ui.common.client.widget.selector.item.SelectorItem;
import ru.protei.portal.ui.common.client.widget.selector.popup.SelectorPopup;

import java.util.List;
import java.util.Set;

/**
 * Селектор сотрудников
 */
public class InitiatorMultiSelector
        extends MultipleInputSelector<PersonShortView>
        implements ModelSelector<PersonShortView>
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
        if(companyIds==null){
            SelectorItem item = new SelectorItem();
            item.setName(lang.initiatorSelectACompany());
            item.getElement().addClassName(UiConstants.Styles.TEXT_CENTER);
            popup.getChildContainer().add(item);
        }
    }

    @Override
    public void clearOptions() {
        super.clearOptions();
        companyIds = null;
    }

    @Override
    public void refreshValue() {

    }

    public void setFired ( boolean value ) { this.fired = value; }

    public void updateCompanies(Set<Long> companyIds) {
        this.companyIds = companyIds;
        if(model!=null){
            model.updateCompanies(companyIds, fired);
        }
    }

    Lang lang;
    Provider<SelectorPopup> popupProvider;
    private InitiatorModel model;
    private Set<Long> companyIds;
    private boolean fired;

}
