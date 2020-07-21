package ru.protei.portal.core.model.view;

import java.io.Serializable;

public class AbsenceFilterShortView implements Serializable {

    private Long id;
    private String name;

    public AbsenceFilterShortView() {
    }

    public AbsenceFilterShortView( Long id, String name ) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId( Long id ) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    @Override
    public boolean equals( Object obj ) {
        if (obj instanceof AbsenceFilterShortView ) {
            Long oid = ((AbsenceFilterShortView)obj).getId();
            return this.id == null ? oid == null : oid != null && this.id.equals(oid);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "AbsenceFilterShortView{" +
                "id=" + id +
                ", name=" + name +
                '}';
    }
}
