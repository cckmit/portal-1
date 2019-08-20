package ru.protei.portal.core.model.yt.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.NumberUtils;
import ru.protei.portal.core.model.yt.api.issue.IssueCustomField;
import ru.protei.portal.core.model.yt.fields.YtFields;
import ru.protei.portal.core.model.yt.fields.issue.IssueField;
import ru.protei.portal.core.model.yt.fields.issue.StringArrayWithIdArrayIssueField;

import java.util.List;
import java.util.Objects;

/**
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IssueApi {

    public Long getCrmNumber() {
        IssueCustomField field = getField( YtFields.crmNumber );
        if(field==null) return null;
        return NumberUtils.parseLong( field.getValue() );

    }

    @Override
    public String toString() {
        return "IssueApi{" +
                "id='" + id + '\'' +
                ", $type='" + $type + '\'' +
                ", idReadable='" + idReadable + '\'' +
                ", summary='" + summary + '\'' +
                ", customFields=" + customFields +
                '}';
    }

    public String $type;
    public String id;
    public String idReadable;
    public String summary;
    public List<IssueCustomField> customFields;

    public interface Fields {
        String id = "id";
        String type = "type";
        String name = "name";
        String summary = "summary";
        String idReadable = "idReadable";
    }

    public interface Value {
        String id = "id";
        String type = "type";
        String name = "name";
        String localizedName = "localizedName";
    }

    public interface CustomFields {
        String id = "id";
        String type = "type";
        String name = "name";
        String emptyFieldText = "emptyFieldText";
        String crm = YtFields.crmNumber;
    }

    private IssueCustomField getField( String name ) {
        if (name == null) return null;
        return CollectionUtils.stream( customFields )
                .filter( Objects::nonNull )
                .filter( ( field ) ->
                        name.equals( field.name )
                )
                .findFirst()
                .orElse( null );
    }

    private static final Logger log = LoggerFactory.getLogger( IssueApi.class );
}
