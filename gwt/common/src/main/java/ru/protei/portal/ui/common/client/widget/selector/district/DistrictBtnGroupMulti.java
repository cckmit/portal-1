package ru.protei.portal.ui.common.client.widget.selector.district;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.struct.DistrictInfo;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.client.widget.togglebtn.group.ToggleBtnGroupMulti;

import java.util.List;

/**
 * Селектор состояния региона
 */
public class DistrictBtnGroupMulti extends ToggleBtnGroupMulti<DistrictInfo> implements SelectorWithModel<DistrictInfo> {

    @Inject
    public void init( DistrictModel model ) {
        setSelectorModel(model);
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
            addBtn( district.shortName, district, "btn btn-default" );
        }
    }

}