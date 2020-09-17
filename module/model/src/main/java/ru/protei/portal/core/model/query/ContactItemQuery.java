package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_ContactDataAccess;
import ru.protei.portal.core.model.dict.En_ContactItemType;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;

import java.util.List;

public class ContactItemQuery extends BaseQuery {

    private List<En_ContactItemType> itemTypes;

    private List<En_ContactDataAccess> accessTypes;

    private List<Long> personIds;

    private List<Long> companyIds;

    public ContactItemQuery() {
        this("");
    }

    public ContactItemQuery(String searchString) {
        this(searchString, En_SortField.value, En_SortDir.ASC);
    }

    public ContactItemQuery(String searchString, En_SortField sortField, En_SortDir sortDir) {
        super(searchString, sortField, sortDir);
    }

    public List<En_ContactItemType> getItemTypes() {
        return itemTypes;
    }

    public void setItemTypes(List<En_ContactItemType> itemTypes) {
        this.itemTypes = itemTypes;
    }

    public List<En_ContactDataAccess> getAccessTypes() {
        return accessTypes;
    }

    public void setAccessTypes(List<En_ContactDataAccess> accessTypes) {
        this.accessTypes = accessTypes;
    }

    public List<Long> getPersonIds() {
        return personIds;
    }

    public void setPersonIds(List<Long> personIds) {
        this.personIds = personIds;
    }

    public List<Long> getCompanyIds() {
        return companyIds;
    }

    public void setCompanyIds(List<Long> companyIds) {
        this.companyIds = companyIds;
    }

    @Override
    public String toString() {
        return "ContactItemQuery{" +
                "searchString='" + searchString + '\'' +
                ", itemTypes=" + itemTypes +
                ", accessTypes=" + accessTypes +
                ", personIds=" + personIds +
                ", companyIds=" + companyIds +
                '}';
    }
}
