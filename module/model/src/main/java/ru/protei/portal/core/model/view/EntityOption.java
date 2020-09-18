package ru.protei.portal.core.model.view;

import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.dto.ProductDirectionInfo;
import ru.protei.portal.core.model.marker.HasLongId;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by michael on 08.11.16.
 */
public class EntityOption implements Serializable, HasLongId {

    private String displayText;
    private Long id;
    private String info;

    public EntityOption() {
    }

    public EntityOption(Long id) {
        this(null, id, null);
    }

    public EntityOption(String displayText, Long id) {
        this(displayText, id, null);
    }

    public EntityOption(String displayText, Long id, String info) {
        this.displayText = displayText;
        this.id = id;
        this.info = info;
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

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EntityOption) {
            Long oid = ((EntityOption)obj).getId();
            return Objects.equals(this.id, oid);
        }

        return false;
    }

    public static EntityOption fromCompany(Company company){
        if(company == null)
            return null;
        return new EntityOption(company.getCname(), company.getId());
    }

    public static EntityOption fromProductDirectionInfo( ProductDirectionInfo info ) {
        if ( info == null ) {
            return null;
        }
        return new EntityOption( info.name, info.id );
    }

    public static EntityOption fromLocation( Location location ) {
        if ( location == null ) {
            return null;
        }
        return new EntityOption( location.getName(), location.getId() );
    }
}
