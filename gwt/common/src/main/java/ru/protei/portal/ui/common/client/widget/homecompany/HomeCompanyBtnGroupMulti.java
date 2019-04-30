package ru.protei.portal.ui.common.client.widget.homecompany;

import com.google.inject.Inject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.client.widget.togglebtn.group.ToggleBtnGroupMulti;

import java.util.List;

/**
 * Домашние компании
 */
public class HomeCompanyBtnGroupMulti extends ToggleBtnGroupMulti< EntityOption > implements SelectorWithModel< EntityOption > {

    @Inject
    public void init( HomeCompanyModel homeCompanyModel ) {
        homeCompanyModel.subscribe( this );
        setSelectorModel(homeCompanyModel);
    }

    @Override
    public void fillOptions( List< EntityOption > options ) {
        clear();
        options.forEach( entityOption -> {
            addBtn( entityOption.getDisplayText(), entityOption );
        } );
    }

}
