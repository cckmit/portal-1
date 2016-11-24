package ru.protei.portal.core.model.view;

import ru.protei.portal.core.model.ent.Person;

import java.io.Serializable;

/**
 * Сокращенное представление контакта
 */
public class ContactShortView implements Serializable {
    private Long id;
    private String displayShortName;
    private boolean isFired;

    public ContactShortView() {}

    public ContactShortView( String displayShortName, Long id, boolean isFired ) {
        this.displayShortName = displayShortName;
        this.id = id;
        this.isFired = isFired;
    }

    public String getDisplayShortName() {
        return displayShortName;
    }

    public void setDisplayShortName( String displayShortName ) {
        this.displayShortName = displayShortName;
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
        if (obj instanceof ContactShortView) {
            Long oid = ((ContactShortView)obj).getId();
            return this.id == null ? oid == null : oid != null && this.id.equals(oid);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public static ContactShortView fromPerson( Person person ){
        if(person == null)
            return null;
        return new ContactShortView( person.getDisplayShortName(), person.getId(), person.isFired() );
    }

}
