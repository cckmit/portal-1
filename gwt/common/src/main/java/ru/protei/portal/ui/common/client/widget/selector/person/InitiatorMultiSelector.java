package ru.protei.portal.ui.common.client.widget.selector.person;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Label;
import com.google.inject.Inject;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.client.widget.selector.input.MultipleInputSelector;
import ru.protei.portal.ui.common.client.widget.selector.item.SelectableItem;
import ru.protei.portal.ui.common.client.widget.selector.item.SelectorItem;

import java.util.List;
import java.util.Set;

import static ru.protei.portal.core.model.helper.CollectionUtils.isEmpty;

/**
 * Селектор сотрудников
 */
public class InitiatorMultiSelector
        extends MultipleInputSelector<PersonShortView>
        implements ModelSelector<PersonShortView>
{
    @Inject
    public void init(InitiatorModel model, Lang lang ) {
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

    public void updateCompanies(Set<Long> companyIds) {
        this.companyIds = companyIds;
        if(model!=null){
            model.updateCompanies(companyIds);
        }
    }

    Lang lang;
    private InitiatorModel model;
    private Set<Long> companyIds;

}
