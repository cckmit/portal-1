package ru.protei.portal.core.model.view;

import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.ent.Person;

import java.io.Serializable;

/**
 * Created by michael on 08.11.16.
 */
public class EntityOption implements Serializable {

    private String displayText;
    private Long id;

    public EntityOption() {
    }

    public EntityOption(String displayText, Long id) {
        this.displayText = displayText;
        this.id = id;
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EntityOption) {
            Long oid = ((EntityOption)obj).getId();
            return this.id == null ? oid == null : oid != null && this.id.equals(oid);
        }

        return false;
    }


    public static EntityOption fromCompany(Company company){
        if(company == null)
            return null;
        return new EntityOption(company.getCname(), company.getId());
    }

    public static EntityOption fromProduct(DevUnit product){
        if(product == null)
            return null;
        return new EntityOption(product.getName(), product.getId());
    }

    public static EntityOption fromPerson(Person person){
        if(person == null)
            return null;
        return new EntityOption(person.getDisplayShortName(), person.getId());
    }
}