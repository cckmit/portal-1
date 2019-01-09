package ru.protei.portal.ui.common.client.widget.selector.district;

import com.google.inject.Inject;
import org.slf4j.LoggerFactory;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.struct.DistrictInfo;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.client.widget.togglebtn.group.ToggleBtnGroupMulti;

import java.util.List;
import java.util.logging.Logger;

/**
 * Селектор состояния региона
 */
public class DistrictBtnGroupMulti extends ToggleBtnGroupMulti<DistrictInfo> implements ModelSelector<DistrictInfo> {

    @Inject
    public void init( DistrictModel model ) {
        model.subscribe( this );
    }

    @PostConstruct
    public void onConstruct() {
        addStyleName( "status-group" );
    }

    @Override
    public void fillOptions( List<DistrictInfo> options ) {
        clear();

        if ( options == null ) {
            return;
        }

        for ( DistrictInfo district : options ) {
            addBtn( district.shortName, district, "btn btn-white" );
        }
    }

    @Override
    public void clearOptions() {
        log.warning( "clearOptions(): Not implemented." );//TODO NotImplemented

    }

    private static final Logger log = Logger.getLogger( DistrictBtnGroupMulti.class.getName() );
}