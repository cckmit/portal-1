package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;

/**
 * Информация о человеке с ролью
 */
@JdbcEntity(table = "case_location")
public class CaseLocation implements Serializable{

    @JdbcId(name="id" , idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name="CASE_ID")
    private Long caseId;

    @JdbcColumn(name="LOCATION_ID")
    private Long locationId;

    @JdbcJoinedObject(localColumn = "LOCATION_ID", remoteColumn = "id", updateLocalColumn = false )
    private Location location;

    public CaseLocation() {
    }

    public Long getId() {
        return id;
    }

    public void setId( Long id ) {
        this.id = id;
    }

    public Long getCaseId() {
        return caseId;
    }

    public void setCaseId( Long caseId ) {
        this.caseId = caseId;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId( Long locationId ) {
        this.locationId = locationId;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation( Location location ) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "CaseLocation{" +
            "id=" + id +
            ", caseId=" + caseId +
            ", locationId=" + locationId +
            ", location=" + location +
            '}';
    }

    public static CaseLocation makeLocationOf( CaseObject caseObject, EntityOption location ) {
        CaseLocation result = new CaseLocation();
        result.setCaseId( caseObject.getId() );
        result.setLocationId( location.getId() );
        return result;
    }
}
