package ru.protei.portal.ui.common.client.widget.delivery.optionlist.kit;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.Kit;
import ru.protei.portal.ui.common.client.lang.DeliveryStateLang;
import ru.protei.portal.ui.common.client.widget.delivery.optionlist.list.OptionList;

import java.util.List;

/**
 * Список комплектов
 */
public class KitList extends OptionList<Kit> {

    @Inject
    public void init(DeliveryStateLang stateLang) {this.stateLang = stateLang;}

    @Override
    protected void onUnload() {
        super.onUnload();
        filter = null;
    }

    @Override
    public void fillOptions(List<Kit> kits) {
        clearOptions();
        kits.forEach( kit -> {
            addOption(
                    kit.getSerialNumber(),
                    stateLang.getStateName(kit.getState()),
                    kit.getState().getColor(),
                    kit.getName(),
                    kit ) ;

        } );
    }

    private DeliveryStateLang stateLang;
}
