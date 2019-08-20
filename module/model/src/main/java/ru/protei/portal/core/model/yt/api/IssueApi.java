package ru.protei.portal.core.model.yt.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.yt.fields.issue.StringArrayWithIdArrayIssueField;
import ru.protei.portal.core.model.yt.fields.issue.StringIssueField;

import java.util.List;

/**
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IssueApi {

    public Long getCrmNumber() {
        log.warn( "getCrmNumber(): Not implemented." );//TODO NotImplemented
        return null;
    }

    public String id;
    public String idReadable;
    public String summary;
    public List<CustomField> customFields;
    public String $type;


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

    private static final Logger log = LoggerFactory.getLogger( IssueApi.class );

    public interface Fields {
        String id = "id";
        String idReadable = "idReadable";
        String name = "name";
        String type = "type";
        String summary = "summary";
    }

    public interface Value {
        String id = "id";
        String name = "name";
        String type = "type";
        String localizedName = "localizedName";
    }

    public interface CustomFields {
        String id = "id";
        String name = "name";
        String type = "type";
        String localizedName = "localizedName";
        String emptyFieldText = "emptyFieldText";
        String crm = "crm";
    }
}
