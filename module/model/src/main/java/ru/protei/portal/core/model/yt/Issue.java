package ru.protei.portal.core.model.yt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ru.protei.portal.core.model.yt.fields.*;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by admin on 15/11/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Issue {

    private String id;
    private String entityId;
    private List<Field> field;

    public String getId() {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId( String entityId ) {
        this.entityId = entityId;
    }

    public List< Field > getField() {
        return field;
    }

    public void setField( List< Field > field ) {
        this.field = field;
    }

    public String getProjectShortName() {
        Optional<StringField> fieldValue = getField( "projectShortName" );
        if ( !fieldValue.isPresent() ) {
            return null;
        }
        return fieldValue.get().getValue();
    }

    public String getType() {
        Optional<StringArrayField> typeField = getField( "Type" );
        if ( !typeField.isPresent() ) {
            return "!Unknown type";
        }
        List<String> listValue = typeField.get().getValue();
        if ( listValue.isEmpty() ) {
            return "!Unknown type";
        }
        return listValue.get( 0 );
    }

    public String getAssignee() {
        Optional<PersonArrayField> fieldValue = getField( "Assignee" );
        if ( !fieldValue.isPresent() ) {
            return "!No assignee";
        }
        return fieldValue.get().getValue().get( 0 ).getValue();
    }

    public Date getResolved() {
        Optional<DateField> fieldValue = getField( "resolved" );
        if ( !fieldValue.isPresent() ) {
            return null;
        }

        return fieldValue.get().getValue();
    }

    public Date getCreated() {
        Optional<DateField > fieldValue = getField( "created" );
        if ( !fieldValue.isPresent() ) {
            return null;
        }

        return fieldValue.get().getValue();
    }

    public String getCrmNumber() {
        Optional<StringField > fieldValue = getField( "Номер обращения в CRM" );
        if ( !fieldValue.isPresent() ) {
            return "!Без обращения";
        }
        return fieldValue.get().getValue();
    }

    public String getReporterFullName() {
        Optional<StringField > fieldValue = getField( "reporterFullName" );
        if ( !fieldValue.isPresent() ) {
            return "!Нет заявителя";
        }
        return fieldValue.get().getValue();
    }

    public String getCustomer() {
        Optional<StringArrayField> fieldValue = getField( "Заказчик" );
        if ( !fieldValue.isPresent() ) {
            return null;
        }
        List<String> listValue = fieldValue.get().getValue();
        if ( listValue.isEmpty() ) {
            return null;
        }
        return listValue.get( 0 );
    }

    public List<String> getParents() {
        Optional<LinkArrayField> fieldValue = getField( "links" );
        if ( !fieldValue.isPresent() ) {
            return Collections.emptyList();
        }

        return fieldValue.get().getValue().stream()
                .filter( (issue)->issue.getRole().equalsIgnoreCase( "подзадача" ) )
                .map( Link::getValue )
                .collect( Collectors.toList() );
    }

    public List<String> getChildren() {
        //Родитель для
        Optional<LinkArrayField> fieldValue = getField( "links" );
        if ( !fieldValue.isPresent() ) {
            return Collections.emptyList();
        }

        return fieldValue.get().getValue().stream()
                .filter( (issue)->issue.getRole().equalsIgnoreCase( "родитель для" ) )
                .map( Link::getValue )
                .collect( Collectors.toList() );
    }

    private <T extends Field> Optional< T > getField( String name ) {
        if ( field == null ) {
            return Optional.empty();
        }

        try {
            Optional< Field > fieldValue = field.stream().filter( ( field ) -> field.getName().equals( name ) ).findFirst();
            if ( !fieldValue.isPresent() ) {
                return Optional.empty();
            }
            return (Optional< T >) fieldValue;
        }
        catch ( NullPointerException e ) {
            System.out.println("NPE");
            System.out.println( this );
        }

        return null;
    }

    @Override
    public String toString() {
        return "Issue{" +
                "id='" + id + '\'' +
                ", entityId='" + entityId + '\'' +
                ", field=" + field +
                '}';
    }
}
