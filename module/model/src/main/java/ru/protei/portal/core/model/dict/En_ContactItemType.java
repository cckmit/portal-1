package ru.protei.portal.core.model.dict;

import ru.protei.portal.core.model.dict.lang.ContactItemLang;
import ru.protei.winter.core.utils.enums.HasId;

import java.io.Serializable;

public enum En_ContactItemType implements HasId, Serializable {

    EMAIL(1) {
        public String getMessage(ContactItemLang lang) {
            return lang.contactEmail();
        }
    },

    ADDRESS(2) {
        public String getMessage(ContactItemLang lang) {
            return lang.contactActualAddress();
        }
    },

    ADDRESS_LEGAL(3) {
        public String getMessage(ContactItemLang lang) {
            return lang.contactLegalAddress();
        }
    },

    FAX(4) {
        public String getMessage(ContactItemLang lang) {
            return lang.contactFax();
        }
    },

    MOBILE_PHONE(5) {
        public String getMessage(ContactItemLang lang) {
            return lang.contactMobilePhone();
        }
    },

    GENERAL_PHONE(6) {
        public String getMessage(ContactItemLang lang) {
            return lang.contactWorkPhone();
        }
    },

    WEB_SITE(7) {
        public String getMessage(ContactItemLang lang) {
            return lang.contactWebSite();
        }
    },


    @Deprecated
    UNDEFINED(0) {
        public String getMessage(ContactItemLang lang) {
            return "-";
        }
    },
    @Deprecated
    ICQ(-1) {
        public String getMessage(ContactItemLang lang) {
            return lang.contactIcq();
        }
    },
    @Deprecated
    JABBER(-2) {
        public String getMessage(ContactItemLang lang) {
            return lang.contactJabber();
        }
    },
    @Deprecated
    SKYPE(-3) {
        public String getMessage(ContactItemLang lang) {
            return lang.contactSkype();
        }
    },
    @Deprecated
    SOCIAL_NET(-4) {
        public String getMessage(ContactItemLang lang) {
            return lang.contactSocialNet();
        }
    },
    ;


    En_ContactItemType(int id) {
        this.id = id;
    }
    private final int id;
    public int getId() {
        return id;
    }
    public abstract String getMessage(ContactItemLang lang);
}
