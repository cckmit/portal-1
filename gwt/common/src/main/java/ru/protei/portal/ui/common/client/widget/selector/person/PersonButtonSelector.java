package ru.protei.portal.ui.common.client.widget.selector.person;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.inject.Inject;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonPopupSingleSelector;
import ru.protei.portal.ui.common.client.selector.SelectorItem;
import ru.protei.portal.ui.common.client.selector.popup.item.PopupSelectorItem;

import java.util.Set;
import java.util.logging.Logger;

import static ru.protei.portal.core.model.helper.CollectionUtils.contains;


/**
 * Селектор person
 */
public class PersonButtonSelector extends ButtonPopupSingleSelector< PersonShortView >
    implements Refreshable
{

    @Inject
    public void init(PersonModel model) {
        this.model = model;
        setModel(model);
        setItemRenderer( value -> value == null ? defaultValue : value.getName() );
    }

    @Override
    protected SelectorItem makeSelectorItem( PersonShortView value, String elementHtml ) {
        PopupSelectorItem item = new PopupSelectorItem();
        item.setName(elementHtml);
        if(value!=null){
            item.setIcon( value.isFired() ? "not-active" : "" );
            item.setIcon( value.isFired() ? "fa fa-ban ban" : "" );
        }
        return item;
    }

    public void setFired ( boolean value ) { this.fired = value; }

    public void setPeople(boolean isPeople) {
        this.isPeople = isPeople;
    }

    @Override
    public void refresh() {
        PersonShortView value = getValue();
        if (value != null
                && !contains( model.getValues(), value )) {
            setValue( null );
        }
    }

    @Override
    public void onShowPopupClicked( ClickEvent event) {

        if (CollectionUtils.isNotEmpty( companyIds )) {
            super.onShowPopupClicked(event);
            checkNoElements();
        } else {
            ru.protei.portal.ui.common.client.widget.selector.item.SelectorItem item = new ru.protei.portal.ui.common.client.widget.selector.item.SelectorItem();
            item.setName(lang.initiatorSelectACompany());
            item.getElement().addClassName(UiConstants.Styles.TEXT_CENTER);
            getPopup().getChildContainer().add(item);
            getPopup().showNear( button.getElement() );
        }
    }
    static int index = 0; //ToDO debug
    public void updateCompanies(Set<Long> companyIds) {
        log.info( "updateCompanies(): " + (index++)+ " " + defaultValue );
        this.companyIds = companyIds;
        if (model != null) {
            model.updateCompanies(this, isPeople, companyIds, fired);
        }
    }

    @Inject
    Lang lang;

    private PersonModel model;

    private boolean fired = false;
    private Boolean isPeople = null;
    private Set<Long> companyIds;

    private static final Logger log = Logger.getLogger( PersonButtonSelector.class.getName() );
}
