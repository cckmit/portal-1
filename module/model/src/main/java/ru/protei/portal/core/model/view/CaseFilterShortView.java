package ru.protei.portal.core.model.view;

import ru.protei.portal.core.model.view.filterwidget.AbstractFilterShortView;

import java.io.Serializable;

/**
 * Сокращенное представление фильтра обращений
 */
public class CaseFilterShortView implements AbstractFilterShortView, Serializable {

    private Long id;
    private String name;

    public CaseFilterShortView() {
    }

    public CaseFilterShortView( Long id, String name ) {
        this.id = id;
        this.name = name;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId( Long id ) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    @Override
    public boolean equals( Object obj ) {
        if (obj instanceof CaseFilterShortView ) {
            Long oid = ((CaseFilterShortView)obj).getId();
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
        return "CaseFilterShortView{" +
                "id=" + id +
                ", name=" + name +
                '}';
    }
}
