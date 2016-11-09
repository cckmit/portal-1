package ru.protei.portal.core.model.struct;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.protei.portal.core.model.dict.En_ContactDataAccess;
import ru.protei.portal.core.model.dict.En_PhoneType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by Mike on 09.11.2016.
 */
public class CustomContactInfoFacade {

    public ContactInfo contactInfo;


    public CustomContactInfoFacade () {
    }

    public CustomContactInfoFacade (ContactInfo info) {
        this.contactInfo = info;
    }

    public ContactInfo editItem() {
        return contactInfo == null ? (contactInfo = new ContactInfo()) : contactInfo;
    }

    public ContactInfo editItem (ContactInfo contactInfo) {
        return (this.contactInfo = contactInfo);
    }


    public List<ContactPhone> getFaxList () {
        return filterPhoneList(contactPhone -> contactPhone.type == En_PhoneType.FAX);
    }

    public List<ContactPhone> getMobilePhoneList () {
        return filterPhoneList(contactPhone -> contactPhone.type == En_PhoneType.MOBILE);
    }

    public List<ContactPhone> getGeneralPhoneList () {
        return filterPhoneList(contactPhone -> contactPhone.type == En_PhoneType.GENERAL);
    }


    /** utility part **/

    protected ContactPhone findOrCreatePhone (En_PhoneType type, En_ContactDataAccess accessType) {
        return findOrCreatePhone (phone -> phone.type == type && phone.access == accessType, () -> new ContactPhone(type,accessType));
    }

    protected ContactPhone findOrCreatePhone (En_PhoneType type) {
        return findOrCreatePhone (phone -> phone.type == type, () -> new ContactPhone(type, En_ContactDataAccess.PUBLIC));
    }

    protected ContactPhone findOrCreatePhone (Predicate<ContactPhone> predicate, Supplier<ContactPhone> factory) {
        ContactPhone phone = findPhone(predicate);
        if (phone == null) {
            editItem().phoneList.add(phone = factory.get());
        }
        return phone;
    }

    protected List<ContactPhone> filterPhoneList (Predicate<ContactPhone> predicate) {
        return editItem().phoneList.stream().filter(predicate).collect(Collectors.toList());
    }

    protected String findPhoneValue (En_PhoneType type, En_ContactDataAccess accessType) {
        return findPhoneValue(contactPhone -> contactPhone.type == type && contactPhone.access == accessType);
    }

    protected String findPhoneValue (Predicate<ContactPhone> predicate) {
        return findPhoneValue(predicate, null);
    }

    protected String findPhoneValue (Predicate<ContactPhone> predicate, String defValue) {
        ContactPhone item = findPhone(predicate);
        return  item != null ? item.getPhone() : defValue;
    }

    protected ContactPhone findPhone (Predicate<ContactPhone> predicate) {
        return !editItem().phoneList.isEmpty() ? editItem().phoneList.stream().filter(predicate).findFirst().orElse(null) : null;
    }


    protected String findEmailValue (En_ContactDataAccess accessType) {
        ContactEmail e = findEmail(accessType);
        return e == null ? null : e.email;
    }

    protected ContactEmail findEmail (En_ContactDataAccess accessType) {
        return !editItem().emailList.isEmpty() ?
                editItem().emailList.stream().filter(item -> item.access == accessType).findFirst().orElse(null) : null;
    }

    protected ContactEmail findOrCreateEmail (En_ContactDataAccess accessType) {
        ContactEmail email = findEmail (accessType);
        if (email == null) {
            editItem().emailList.add(email = new ContactEmail(accessType));
        }
        return email;
    }
}
