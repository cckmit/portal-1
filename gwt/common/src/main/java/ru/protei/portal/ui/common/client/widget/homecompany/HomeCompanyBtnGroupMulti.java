package ru.protei.portal.ui.common.client.widget.homecompany;

import com.google.inject.Inject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.client.widget.togglebtn.group.ToggleBtnGroupMulti;

import java.util.List;
import java.util.logging.Logger;

/**
 * Домашние компании
 */
public class HomeCompanyBtnGroupMulti extends ToggleBtnGroupMulti< EntityOption > implements ModelSelector< EntityOption > {

    @Inject
    public void init( HomeCompanyModel homeCompanyModel ) {
        homeCompanyModel.subscribe( this );
    }

    @Override
    public void fillOptions( List< EntityOption > options ) {
        clear();
        options.forEach( entityOption -> {
            addBtn( entityOption.getDisplayText(), entityOption );
        } );
    }

    @Override
    public void clearOptions() {
        log.warning( "clearOptions(): Not implemented." );//TODO NotImplemented

    }

    private static final Logger log = Logger.getLogger( HomeCompanyBtnGroupMulti.class.getName() );
}
