package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DevUnitPersonRoleType;


public class En_PersonRoleTypeLang {

    public String getName(En_DevUnitPersonRoleType type) {

        switch (type) {
            case HEAD_MANAGER:
                return lang.personHeadManager();
            case DEPLOY_MANAGER:
                return lang.personDeployManager();
            case SALES_MANAGER:
                return lang.personSalesManager();
            case ADS_MANAGER:
                return lang.personAdsManager();
            case ART_MANAGER:
                return lang.personArtManager();
        }
        return null;
    }

    @Inject
    Lang lang;
}
