package ru.protei.portal.core.model.dict;

import ru.protei.portal.core.model.dict.config.CaseLinkConfig;
import ru.protei.portal.core.model.dict.lang.CaseLinkLang;

public enum En_CaseLink {

    CRM(1, false) {
        @Override
        public String getName(CaseLinkLang lang) {
            return lang.caseLinkCrm();
        }
        @Override
        public String getLink(CaseLinkConfig config, String id) {
            return config.getLinkCrm().replace("%id%", id);
        }
    },
    OLD_CRM(2, true) {
        @Override
        public String getName(CaseLinkLang lang) {
            return lang.caseLinkOldCrm();
        }
        @Override
        public String getLink(CaseLinkConfig config, String id) {
            return config.getLinkOldCrm().replace("%id%", id);
        }
    },
    YT(3, true) {
        @Override
        public String getName(CaseLinkLang lang) {
            return lang.caseLinkYouTrack();
        }
        @Override
        public String getLink(CaseLinkConfig config, String id) {
            return config.getLinkYouTrack().replace("%id%", id);
        }
    };

    En_CaseLink(int id, boolean forcePrivacy) {
        this.id = id;
        this.forcePrivacy = forcePrivacy;
    }

    private final int id;
    private final boolean forcePrivacy;

    public int getId() {
        return id;
    }

    public abstract String getName(CaseLinkLang lang);

    public abstract String getLink(CaseLinkConfig config, String id);

    public boolean isForcePrivacy() {
        return forcePrivacy;
    }

    public static En_CaseLink findById(int id) {
        for (En_CaseLink value : En_CaseLink.values()) {
            if (value.getId() == id) {
                return value;
            }
        }
        return null;
    }
}
