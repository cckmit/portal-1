package ru.protei.portal.core.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.util.UriComponentsBuilder;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.yt.api.IssueApi;
import ru.protei.portal.core.model.yt.fields.YtFields;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ru.protei.portal.api.struct.CoreResponse.ok;
import static ru.protei.portal.core.model.helper.StringUtils.join;

/**
 *
 */
public class YoutrackApiDaoImpl implements YoutrackApiDAO {

    @PostConstruct
    public void initAuthHeadersAndUrl() {
        BASE_URL = portalConfig.data().youtrack().getApiBaseUrl();
        BASE_URL = BASE_URL.replaceAll( "/rest", "/api" );
    }

    public static void main( String[] args ) {
//        IssueQueryBuilder qury = new IssueQueryBuilder();
//        qury.fields( IssueApi.Fields.id, IssueApi.Fields.name, IssueApi.Fields.summary );
//        qury.customFields( IssueApi.CustomFields.id );
//        String build = qury.build();
//
//        IssueQueryBuilder qury = new IssueQueryBuilder();
//        qury.fields().id().name().summary().idReadable()
//                .customFields().customId().customName().emptyFieldText().valueFields().localizedName();
//        String build = qury.build();
//        if(true) return;


        YoutrackApiDaoImpl dao = new YoutrackApiDaoImpl();
        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.setAccept( Arrays.asList( MediaType.APPLICATION_JSON ) );
        authHeaders.setContentType( MediaType.APPLICATION_JSON_UTF8 );
        authHeaders.set( "Authorization", "Bearer perm:cG9ydGFs.cG9ydGFs.IOOlyzNfo22S7FpoanxYQB7Ap9FW7e" );
        YoutrackHttpClientImpl client = new YoutrackHttpClientImpl();
        client.setAuthHeaders( authHeaders );
        String BASE_URL = "https://youtrack.protei.ru/api";

//        String body =  "{ \"customFields\": [ {\"name\":\""+YtFields.crmNumber+"\",\"$type\":\"SimpleIssueCustomField\",\"value\":"+String.valueOf( 100451L)+"} ] }";

//        CoreResponse<String> response =
//                client.update( BASE_URL + "/issues/PG-209", String.class, body );
        IssueQueryBuilder qury = new IssueQueryBuilder(BASE_URL, "PG-209");

        qury.fields().id().name().summary().idReadable()
                .customFields().customId().customName().emptyFieldText().valueFields().localizedName();
        String build = qury.build();

        QueryBuilder qury2 = new IssueQueryBuilder(BASE_URL, "PG-209");
        String url = qury2.customFields().builder().build();
        if (true) return;
//        String customFields = "customFields(projectCustomField(id,name,emptyFieldText,field(id,name,type)),value(id,name))";
//        String fieldsString = "id,idReadable,summary" + "," + customFields;

        String url2 = UriComponentsBuilder.fromHttpUrl( BASE_URL + "/issues/PG-209" )
                .queryParam( "fields", build )
                .build()
                .encode()
                .toUriString();

        CoreResponse<IssueApi> response = client.read( url, IssueApi.class );
        IssueApi data = response.getData();

        log.info( "main(): {}", data );

        Long crmNumber = data.getCrmNumber();


        int stop = 0;
    }

    @Override
    public CoreResponse<String> removeCrmNumber( String issueId ) {
        String body = "{ \"customFields\": [ {\"name\":\"" + YtFields.crmNumber + "\",\"$type\":\"SimpleIssueCustomField\",\"value\":null} ] }";
        return client.update( BASE_URL + "/issues/" + issueId, String.class, body );
    }

    @Override
    public CoreResponse<String> setCrmNumber( String issueId, Long caseNumber ) {
        String body = "{ \"customFields\": [ {\"name\":\"" + YtFields.crmNumber + "\",\"$type\":\"SimpleIssueCustomField\",\"value\":" + String.valueOf( caseNumber ) + "} ] }";
        return client.update( BASE_URL + "/issues/" + issueId, String.class, body );
    }

    @Override
    public CoreResponse<IssueApi> getIssue( String issueId ) {
        QueryBuilder qury = new IssueQueryBuilder(BASE_URL, issueId);
        String url = qury.customFields().builder().build();
        return client.read( url, IssueApi.class );
    }

    @Autowired
    private PortalConfig portalConfig;
    @Autowired
    private YoutrackHttpClient client;

    private String BASE_URL;

    private final static Logger log = LoggerFactory.getLogger( YoutrackApiDaoImpl.class );
}

class IssueQueryBuilder implements QueryBuilder, BuilderFields, BuilderCustomFields, BuilderValueFields {
    public IssueQueryBuilder( String base_url, String issueId ) {
        this.base_url = base_url;
        this.issueId = issueId;
    }

    List<String> fields = null;
    List<String> customFields = null;
    List<String> valueFields = null;

    public void fields( String... fields ) {
        this.fields = Arrays.asList( fields );
    }

    public void customFields( String... fields ) {
        this.customFields = Arrays.asList( fields );
    }

    public BuilderFields fields() {
        fields = new ArrayList<>();
        return this;
    }
    @Override
    public BuilderValueFields valueFields() {
        valueFields = new ArrayList<>();
        return this;
    }
    @Override
    public BuilderCustomFields customFields() {
        customFields = new ArrayList<>();
        return this;
    }

    @Override
    public IssueQueryBuilder builder() {
        return this;
    }

    public String build() {
        StringBuilder sb = new StringBuilder();
        if (fields != null) {
            sb.append( join( fields, "," ) );
        }
        if (customFields != null) {
            StringBuilder valueFields = new StringBuilder( "value(name" ); //всегда нужны
            if (this.valueFields != null) {
                valueFields = join( valueFields, ",", join( this.valueFields, "," ) );
            }
            valueFields = join( valueFields, ")" );
//            customFields(projectCustomField(id,name,emptyFieldText,field(id,name,type)),value(id,name))
            if (sb.length() > 0) sb.append( "," );
            sb = join( sb,
                    "customFields(id,name,projectCustomField(",
                    join( customFields, "," ), (customFields.size() > 0 ? "," : "")
                    , "field(id,name,type))", ",", valueFields,
                    ")" );
        }

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl( base_url + "/issues/" + issueId );
        if(sb.length()>0) {
                uriBuilder.queryParam( "fields", sb.toString() );
        }

        return uriBuilder.build()
                .encode()
                .toUriString();

    }

    @Override
    public BuilderFields id() {
        fields.add( IssueApi.Fields.id );
        return this;
    }

    @Override
    public BuilderFields idReadable() {
        fields.add( IssueApi.Fields.idReadable );
        return this;
    }

    @Override
    public BuilderFields name() {
        fields.add( IssueApi.Fields.name );
        return this;
    }

    @Override
    public BuilderFields summary() {
        fields.add( IssueApi.Fields.summary );
        return this;
    }

    /* Custom */

    @Override
    public BuilderCustomFields crmNumber() {
        customFields.add( IssueApi.CustomFields.crm );
        return this;
    }

    @Override
    public BuilderCustomFields customId() {
        customFields.add( IssueApi.CustomFields.id );
        return this;
    }

    @Override
    public BuilderCustomFields customName() {
        customFields.add( IssueApi.CustomFields.name );
        return this;
    }

    @Override
    public BuilderCustomFields emptyFieldText() {
        customFields.add( IssueApi.CustomFields.emptyFieldText );
        return this;
    }

    /* Value */

    @Override
    public BuilderValueFields valueId() {
        valueFields.add( IssueApi.Value.id );
        return this;
    }
//
//    @Override
//    public BuilderValueFields valueName() {
//        valueFields.add( IssueApi.Value.name );
//        return this;
//    }

    @Override
    public BuilderValueFields localizedName() {
        valueFields.add( IssueApi.Value.localizedName );
        return this;
    }

    private String base_url;
    private String issueId;
}

interface BuilderFields {
    QueryBuilder builder();
    BuilderCustomFields customFields();

    BuilderFields id();
    BuilderFields idReadable();
    BuilderFields name();
    BuilderFields summary();
}

interface BuilderCustomFields {
    QueryBuilder builder();
    BuilderValueFields valueFields();

    BuilderCustomFields customId();
    BuilderCustomFields customName();
    BuilderCustomFields emptyFieldText();
    BuilderCustomFields crmNumber();
}

interface BuilderValueFields {
    QueryBuilder builder();

    BuilderValueFields valueId();
//    BuilderValueFields valueName(); // Всегда добавляется
    BuilderValueFields localizedName();
}

interface QueryBuilder {
    BuilderCustomFields customFields();
    BuilderFields fields();
    String build();
}
