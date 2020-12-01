package ru.protei.portal.ui.ipreservation.client.view.widget.selector;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Subnet;
import ru.protei.portal.core.model.view.SubnetOption;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.util.LinkUtils;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonPopupSingleSelector;

public class SubnetButtonSelector extends ButtonPopupSingleSelector<SubnetOption> {

    @Inject
    void init(SubnetModel model) {
        setAsyncModel(model);
        setHasNullValue(true);

        setItemRenderer( value -> makeView( value ) );
    }

    private String makeView( SubnetOption subnetOption ) {
        String name = subnetOption == null ? defaultValue : subnetOption.getDisplayText();
        if(subnetOption != null
                && policyService.hasPrivilegeFor( En_Privilege.SUBNET_VIEW)){
            name = name + " <a class=\"full-screen-link\" href=\"" + LinkUtils.makePreviewLink( Subnet.class.getSimpleName(), subnetOption.getId() ) + "\" target=\"_blank\" id=\"" + DebugIds.RESERVED_IP.SUBNET_SELECTOR + "\"></a>";
        }
        return name;
    }


    @Inject
    PolicyService policyService;
}
