package ru.protei.portal.ui.webts.client.activity;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.ActionBarEvents;
import ru.protei.portal.ui.common.client.events.DeliverySpecificationEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.webts.client.integration.NativeWebIntegration;
import ru.protei.portal.ui.webts.client.model.TsWebUnit;

public abstract class DeliverySpecificationActivity implements Activity {

    @PostConstruct
    public void onInit() {
    }

    @Event(Type.FILL_CONTENT)
    public void onShowDeliverySpecifications(DeliverySpecificationEvents.Show event) {
        typescriptUnitActivity.showUnit(TsWebUnit.delivery);
        fireEvent(new ActionBarEvents.Clear());
        if (policyService.hasPrivilegeFor(En_Privilege.DELIVERY_CREATE)) {
            fireEvent(new ActionBarEvents.Add(lang.buttonImport(), null, UiConstants.ActionBarIdentity.DELIVERY_SPECIFICATION_IMPORT));
        }
    }

    @Event(Type.FILL_CONTENT)
    public void onShowDeliverySpecificationsImport(DeliverySpecificationEvents.ShowImport event) {
        typescriptUnitActivity.showUnit(TsWebUnit.delivery);
        fireEvent(new ActionBarEvents.Clear());
    }

    @Event
    public void onShowDeliverySpecificationsImportClicked(ActionBarEvents.Clicked event) {
        if (!UiConstants.ActionBarIdentity.DELIVERY_SPECIFICATION_IMPORT.equals(event.identity)) {
            return;
        }
        fireEvent(new DeliverySpecificationEvents.ShowImport());
    }

    @Inject
    private TypescriptUnitActivity typescriptUnitActivity;
    @Inject
    private NativeWebIntegration nativeWebIntegration;
    @Inject
    private Lang lang;
    @Inject
    private PolicyService policyService;
}
