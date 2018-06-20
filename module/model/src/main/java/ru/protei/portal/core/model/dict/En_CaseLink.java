package ru.protei.portal.core.model.dict;

import ru.protei.portal.core.model.dict.config.CaseLinkConfig;
import ru.protei.portal.core.model.dict.lang.CaseLinkLang;

public enum En_CaseLink {

    CRM(false) {
        @Override
        public String getName(CaseLinkLang lang) {
            return lang.caseLinkCrm();
        }
        @Override
        public String getLink(CaseLinkConfig config, String id) {
            return config.getLinkCrm().replace("%id%", id);
        }
    },
    CRM_OLD(true) {
        @Override
        public String getName(CaseLinkLang lang) {
            return lang.caseLinkOldCrm();
        }
        @Override
        public String getLink(CaseLinkConfig config, String id) {
            return config.getLinkOldCrm().replace("%id%", id);
        }
    },
    YT(true) {
        @Override
        public String getName(CaseLinkLang lang) {
            return lang.caseLinkYouTrack();
        }
        @Override
        public String getLink(CaseLinkConfig config, String id) {
            return config.getLinkYouTrack().replace("%id%", id);
        }
    };

    En_CaseLink(boolean forcePrivacy) {
        this.forcePrivacy = forcePrivacy;
    }

    private final boolean forcePrivacy;

    public abstract String getName(CaseLinkLang lang);

    public abstract String getLink(CaseLinkConfig config, String id);

    public boolean isForcePrivacy() {
        return forcePrivacy;
    }
}
