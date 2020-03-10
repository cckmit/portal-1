package ru.protei.portal.core.model.view;

import ru.protei.portal.core.model.ent.Person;

import java.io.Serializable;

/**
 * Сокращенное представление контакта
 */
public class PersonShortView implements Serializable {
    private Long id;
    private String name;
    private boolean isFired;

    public PersonShortView() {}

    public PersonShortView(Long id) {
        this.id = id;
    }

    public PersonShortView(String name, Long id, boolean isFired ) {
        this.name = name;
        this.id = id;
        this.isFired = isFired;
    }

    public PersonShortView(String name, Long id) {
        this.name = name;
        this.id = id;
    }

    public PersonShortView(EntityOption entityOption) {
        this.name = entityOption.getDisplayText();
        this.id = entityOption.getId();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId( Long id ) {
        this.id = id;
    }

    public boolean isFired() {
        return isFired;
    }

    public void setFired( boolean isFired ) {
        this.isFired = isFired;
    }

    @Override
    public boolean equals( Object obj ) {
        if (obj instanceof PersonShortView) {
            Long oid = ((PersonShortView)obj).getId();
            return this.id == null ? oid == null : oid != null && this.id.equals(oid);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public static PersonShortView fromPerson( Person person ){
        if(person == null)
            return null;
        return new PersonShortView( person.getDisplayShortName(), person.getId(), person.isFired() );
    }
}
