package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_InternalResource;

public class En_InternalResourceLang {
    public String getName(En_InternalResource resource) {
        if (resource == null)
            return "";

        switch (resource) {
            case YOUTRACK:
                return lang.internalResourceYoutrack();
            case CVS:
                return lang.internalResourceCvs();
            case SVN:
                return lang.internalResourceSvn();
            case MERCURIAL:
                return lang.internalResourceMercurial();
            case GIT:
                return lang.internalResourceGit();
            case CRM:
                return lang.internalResourceCrm();
            case STORE_DELIVERY:
                return lang.internalResourceStoreDelivery();
            case EMAIL:
                return lang.internalResourceEmail();
            case VPN:
                return lang.internalResourceVpn();
        }
        return lang.unknownField();
    }

    @Inject
    private Lang lang;
}
