package ru.protei.portal.tools.migrate.struct;

import java.util.HashMap;
import java.util.Map;

public class ExternalPersonInfo {

    public ExternalPerson personData;
    public ExternalPersonExtension proteiExtension;
    public Map<String, ExtContactProperty> contactData;

    public ExternalPersonInfo(ExternalPerson personData, ExternalPersonExtension proteiExtension) {
        this.personData = personData;
        this.proteiExtension = proteiExtension;
    }

    public void addContactData (ExtContactProperty property) {
        if (contactData == null) {
            contactData = new HashMap<>();
        }

        contactData.put(property.makeKey(), property);
    }

    public Long getPersonId () {
        return this.personData.getId();
    }
}
