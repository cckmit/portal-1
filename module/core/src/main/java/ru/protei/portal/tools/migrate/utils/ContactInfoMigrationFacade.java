package ru.protei.portal.tools.migrate.utils;

import ru.protei.portal.core.model.dict.En_ContactDataAccess;
import ru.protei.portal.core.model.dict.En_ContactItemType;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.struct.ContactInfo;

public class ContactInfoMigrationFacade {

    ContactInfo info;

    public ContactInfoMigrationFacade (ContactInfo info) {
        this.info = info;
    }

    public void addEmail (String v) {
        addItem(En_ContactItemType.EMAIL, En_ContactDataAccess.PUBLIC, v);
    }

    public void addPrivateEmail (String v) {
        addItem(En_ContactItemType.EMAIL, En_ContactDataAccess.PRIVATE, v);
    }

    public void addHomePhone (String v) {
        addItem(En_ContactItemType.GENERAL_PHONE, En_ContactDataAccess.PRIVATE, v);
    }

    public void addWorkPhone (String v) {
        addItem(En_ContactItemType.GENERAL_PHONE, En_ContactDataAccess.PUBLIC, v);
    }

    public void addMobilePhone (String v) {
        addItem(En_ContactItemType.MOBILE_PHONE, En_ContactDataAccess.PUBLIC, v);
    }

    public void addFax (String v) {
        addItem(En_ContactItemType.FAX, En_ContactDataAccess.PUBLIC, v);
    }

    public void addHomeFax (String v) {
        addItem(En_ContactItemType.FAX, En_ContactDataAccess.PRIVATE, v);
    }

    public void addAddress (String v) {
        addItem(En_ContactItemType.ADDRESS, En_ContactDataAccess.PUBLIC, v);
    }

    public void addPrivateAddress (String v) {
        addItem(En_ContactItemType.ADDRESS, En_ContactDataAccess.PRIVATE, v);
    }

    public void addLegalAddress (String v) {
        addItem(En_ContactItemType.ADDRESS_LEGAL, En_ContactDataAccess.PUBLIC, v);
    }

    public void addItem (En_ContactItemType type, String v) {
        addItem(type, En_ContactDataAccess.PUBLIC, v);
    }

    public void addItem (En_ContactItemType type, En_ContactDataAccess access, String v) {
        if (HelperFunc.isNotEmpty(v)) {
            info.addItem(type,access).modify(v);
        }
    }
}
