package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.DutyType;

public class DutyTypeLang {

    public String getName( DutyType value ) {
        if (value == null)
            return lang.unknownField();
        switch (value) {
            case BG:
                return lang.dutyTypeBG();
            case IP:
                return lang.dutyTypeIP();
            case BILLING:
                return lang.dutyTypeBilling();
            case MOBILE:
                return lang.dutyTypeMobile();
            case DPI:
                return lang.dutyTypeDPI();
            case MKSP_VKS:
                return lang.dutyTypeMKSP_VKS();
            case NGN:
                return lang.dutyTypeNGN();
            case SORM:
                return lang.dutyTypeSORM();
        }
        return lang.unknownField();
    }

    @Inject
    Lang lang;
}
