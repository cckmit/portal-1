package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DeliveryType;

public class En_DeliveryTypeLang {
    public String getName(En_DeliveryType value) {
        switch (value) {
            case UPGRADE:
                return lang.deliveryTypeUpgrade();
            case UPGRADE_HW:
                return lang.deliveryTypeUpgradeHW();
            case UPGRADE_SW:
                return lang.deliveryTypeUpgradeSW();
            case REPLACEMENT_HW:
                return lang.deliveryTypeReplacementHW();
            case BUGFIX:
                return lang.deliveryTypeBugfix();
            case NEW_VERSION:
                return lang.deliveryTypeNewVersion();
            case NEW_VERSION_SW:
                return lang.deliveryTypeNewVersionSW();
            case NEW_DELIVERY:
                return lang.deliveryTypeNewDelivery();
            case TRIAL_OPERATION:
                return lang.deliveryTypeTrialOperation();
            case DELIVERY:
                return lang.deliveryTypeDelivery();
            case SUPPORT:
                return lang.deliveryTypeSupport();
            default:
                return lang.unknownField();
        }
    }

    @Inject
    Lang lang;
}
