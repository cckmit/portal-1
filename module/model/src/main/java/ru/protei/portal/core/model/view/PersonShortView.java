package ru.protei.portal.core.model.view;

import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.marker.HasLongId;
import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;
import ru.protei.winter.jdbc.annotations.JdbcId;

import java.io.Serializable;

/**
 * Сокращенное представление контакта
 * без присоедиенных JOIN объектов
 */
@JdbcEntity(table = "person")
public class PersonShortView implements Serializable, HasLongId {

    @JdbcId(name = "id")
    private Long id;

    @JdbcColumn(name="company_id")
    private Long companyId;

    @JdbcColumn(name= Person.Columns.DISPLAY_NAME)
    private String displayName;

    @JdbcColumn(name= Person.Columns.DISPLAY_SHORT_NAME)
    private String displayShortName;

    @JdbcColumn(name = Person.Columns.IS_FIRED)
    private boolean isFired;

    /**
     * Произвольное имя
     */
    private String name;

    public PersonShortView() {}

    public PersonShortView(Long id) {
        this(null, id);
    }

    public PersonShortView(String name, Long id, boolean isFired) {
        this.name = name;
        this.id = id;
        this.isFired = isFired;
    }

    public PersonShortView(String name, Long id) {
        this(name, id, false);
    }

    public PersonShortView(EntityOption entityOption) {
        this(entityOption.getDisplayText(), entityOption.getId());
    }

    public PersonShortView( Person person ) {
        this.id = person.getId();
        this.displayName = person.getDisplayName();
        this.displayShortName = person.getDisplayShortName();
        this.companyId = person.getCompanyId();
        this.isFired = person.isFired();
    }

    public PersonShortView( PersonShortView personShortView ) {
        this.id = personShortView.getId();
        this.name = personShortView.name; // прямой доступ к полю
        this.displayName = personShortView.getDisplayName();
        this.displayShortName = personShortView.getDisplayShortName();
        this.companyId = personShortView.getCompanyId();
        this.isFired = personShortView.isFired();
    }

    public String getName() {
        return name != null ? name : displayName != null ? displayName : displayShortName;
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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName( String displayName ) {
        this.displayName = displayName;
    }

    public String getDisplayShortName() {
        return displayShortName;
    }

    public void setDisplayShortName( String displayShortName ) {
        this.displayShortName = displayShortName;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId( Long companyId ) {
        this.companyId = companyId;
    }

    @Override
    public boolean equals( Object obj ) {
        if (obj instanceof PersonShortView) {
            Long oid = ((PersonShortView)obj).getId();
            return this.id == null ? oid == null : this.id.equals(oid);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "PersonShortView{" +
                "id=" + id +
                ", name='" + getName() + '\'' +
                ", isFired=" + isFired +
                '}';
    }
}
