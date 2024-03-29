package ru.protei.portal.ui.common.client.widget.selector.person;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.inject.Inject;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.SelectorItem;
import ru.protei.portal.ui.common.client.selector.popup.item.PopupSelectorItem;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonPopupSingleSelector;

import java.util.logging.Logger;

import static ru.protei.portal.core.model.helper.CollectionUtils.contains;


/**
 * Селектор person
 */
public class PersonButtonSelector extends ButtonPopupSingleSelector< PersonShortView >
    implements Refreshable
{

    @Inject
    public void init() {
        setItemRenderer( value -> value == null ? defaultValue : value.getName() );
    }

    public void setAsyncPersonModel( PersonModel model){
        this.model = model;
        setAsyncModel( model );
    }

    @Override
    protected SelectorItem makeSelectorItem( PersonShortView value, String elementHtml ) {
        PopupSelectorItem item = new PopupSelectorItem();
        item.setName(elementHtml);
        item.setTitle( elementHtml );
        if(value!=null){
            item.setIcon( value.isFired() ? "not-active" : "" );
            item.setIcon( value.isFired() ? "fa fa-ban ban" : "" );
        }
        return item;
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

        if (model.isCompaniesPresent()) {
            super.onShowPopupClicked(event);
            checkNoElements();
        } else {
            getPopup().setNoElements(true, lang.initiatorSelectACompany());
            getPopup().showNear( button.getElement() );
        }
    }

    @Inject
    Lang lang;

    private PersonModel model;

    private static final Logger log = Logger.getLogger( PersonButtonSelector.class.getName() );
}
