package ru.protei.portal.ui.common.client.widget.selector.district;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.struct.DistrictInfo;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.client.widget.togglebtn.group.ToggleBtnGroup;

import java.util.List;

/**
 * Селектор состояния региона
 */
public class DistrictBtnGroup extends ToggleBtnGroup<DistrictInfo> implements ModelSelector<DistrictInfo> {

    @Inject
    public void init( DistrictModel model ) {
        model.subscribe( this );
//        fillOptions( null );
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

}