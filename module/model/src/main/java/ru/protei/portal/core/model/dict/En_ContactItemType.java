package ru.protei.portal.core.model.dict;

import ru.protei.portal.core.model.dict.lang.ContactItemLang;

import java.io.Serializable;

/**
 * Created by michael on 08.11.16.
 */
public enum En_ContactItemType implements Serializable {
    /**
     * используется для заполнения в default-конструкторе, в UI не используется
     */
    UNDEFINED {
        @Override
        public String getMessage(ContactItemLang lang) {
            return "-";
        }
    },

    EMAIL{
        @Override
        public String getMessage(ContactItemLang lang) {
            return lang.contactEmail();
        }
    },
    /**
     * адрес
     */
    ADDRESS{
        @Override
        public String getMessage(ContactItemLang lang) {
            return lang.contactActualAddress();
        }
    },
    /**
     * De-Jure == LEGAL
     */
    ADDRESS_LEGAL{
        @Override
        public String getMessage(ContactItemLang lang) {
            return lang.contactLegalAddress();
        }
    },

    FAX{
        @Override
        public String getMessage(ContactItemLang lang) {
            return lang.contactFax();
        }
    },
    MOBILE_PHONE{
        @Override
        public String getMessage(ContactItemLang lang) {
            return lang.contactMobilePhone();
        }
    },
    GENERAL_PHONE{
        @Override
        public String getMessage(ContactItemLang lang) {
            return lang.contactPersonalPhone();
        }
    },

    ICQ{
        @Override
        public String getMessage(ContactItemLang lang) {
            return lang.contactIcq();
        }
    },
    JABBER{
        @Override
        public String getMessage(ContactItemLang lang) {
            return lang.contactJabber();
        }
    },
    SKYPE{
        @Override
        public String getMessage(ContactItemLang lang) {
            return lang.contactSkype();
        }
    },

    WEB_SITE{
        @Override
        public String getMessage(ContactItemLang lang) {
            return lang.contactWebSite();
        }
    },

    SOCIAL_NET{
        @Override
        public String getMessage(ContactItemLang lang) {
            return lang.contactSocialNet();
        }
    };


    En_ContactItemType() {}

    public abstract String getMessage(ContactItemLang lang);

}
