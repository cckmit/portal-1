package ru.protei.portal.core.model.view;

import java.io.Serializable;

/**
 * Сокращенное представление фильтра обращений
 */
public class IssueFilterShortView implements Serializable {

    private Long id;
    private String name;

    public IssueFilterShortView() {
    }

    public IssueFilterShortView( Long id, String name ) {
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
        if (obj instanceof IssueFilterShortView) {
            Long oid = ((IssueFilterShortView)obj).getId();
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
        return "IssueFilterShortView{" +
                "id=" + id +
                ", name=" + name +
                '}';
    }
}
