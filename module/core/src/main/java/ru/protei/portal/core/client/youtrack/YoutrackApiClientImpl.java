package ru.protei.portal.core.client.youtrack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.UriComponentsBuilder;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.model.yt.api.IssueApi;
import ru.protei.portal.core.model.yt.api.issue.IssueCustomField;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ru.protei.portal.api.struct.CoreResponse.ok;
import static ru.protei.portal.core.model.helper.CollectionUtils.isEmpty;
import static ru.protei.portal.core.model.helper.CollectionUtils.listOf;
import static ru.protei.portal.core.model.helper.StringUtils.isEmpty;
import static ru.protei.portal.core.model.helper.StringUtils.join;

/**
 *  Api youtrack
 */
public class YoutrackApiClientImpl implements YoutrackApiClient {

    @PostConstruct
    public void initAuthHeadersAndUrl() {
        BASE_URL = portalConfig.data().youtrack().getApiBaseUrl();
        BASE_URL = BASE_URL.replaceAll( "/rest", "/api" );
    }

    @Override
    public CoreResponse<String> removeCrmNumber( IssueApi issue ) {
        String url = IssueQueryBuilder.create( BASE_URL, issue.id ).build();
        String body = makeChangeCustomField( issue.getCrmNumberField(), null );
        return client.update( url, String.class, body );
    }

    @Override
    public CoreResponse<String> setCrmNumber( IssueApi issue, Long caseNumber ) {
        String url = IssueQueryBuilder.create( BASE_URL, issue.id ).build();
        String body = makeChangeCustomField( issue.getCrmNumberField(), String.valueOf( caseNumber ) );
        return client.update( url, String.class, body );
    }

    @Override
    public CoreResponse<IssueApi> getIssue( String issueId ) {
        String url = IssueQueryBuilder.create( BASE_URL, issueId ).preset().
                idAndCustomFieldsDefaults().build();
        return client.read( url, IssueApi.class );
    }

    private static String makeChangeCustomField( IssueCustomField customField, String value ) {
        return join( "{ \"customFields\": [ {\"id\":\"", customField.id, "\",\"$type\":\"", customField.$type, "\",\"value\":", value, "} ] }" ).toString();
    }

    @Autowired
    private PortalConfig portalConfig;
    @Autowired
    private YoutrackHttpClient client;

    private String BASE_URL;

    private final static Logger log = LoggerFactory.getLogger( YoutrackApiClientImpl.class );
}

class IssueQueryBuilder implements QueryBuilder, FieldsBuilder, CustomFieldsBuilder, BuilderValueFields, ProjectCustomFieldsBuilder, PresetsBuilder {

    public static QueryBuilder create( String base_url, String issueId ) {
        return new IssueQueryBuilder( base_url, issueId );
    }

    private IssueQueryBuilder( String base_url, String issueId ) {
        this.base_url = base_url;
        this.issueId = issueId;
    }

    private List<String> fields = null;
    private List<String> customFields = null;
    private List<String> valueFields = null;
    private List<String> projectCustomFields = null;

    public void fields( String... fields ) {
        this.fields = Arrays.asList( fields );
    }

    public void customFields( String... fields ) {
        this.customFields = Arrays.asList( fields );
    }

    public FieldsBuilder fields() {
        fields = new ArrayList<>();
        return this;
    }

    @Override
    public BuilderValueFields valueFields() {
        valueFields = new ArrayList<>();
        return this;
    }

    @Override
    public CustomFieldsBuilder customFields() {
        customFields = new ArrayList<>();
        return this;
    }

    @Override
    public ProjectCustomFieldsBuilder projectCustomFields() {
        projectCustomFields = new ArrayList<>();
        return this;
    }

    @Override
    public IssueQueryBuilder builder() {
        return this;
    }

    public String build1() {
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
        if (sb.length() > 0) {
            uriBuilder.queryParam( "fields", sb.toString() );
        }

        return uriBuilder.build()
                .encode()
                .toUriString();

    }

    public String build() {
        StringBuilder sb = new StringBuilder();
        // id,name,customFields(projectCustomField(id,name,emptyFieldText,field(id,name,type)),value(id,name))
        String params = join( listOf(
                fields( fields ),
                customFields( customFields,
                        projectFields( projectCustomFields ),
                        valueFields( valueFields ) )
        ), "," );

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl( base_url + "/issues/" + issueId );
        if (!isEmpty( params )) {
            uriBuilder.queryParam( "fields", params );
        }

        return uriBuilder.build()
                .encode()
                .toUriString();
    }

    private CharSequence valueFields( List<String> fields ) {
        if (isEmpty( fields )) return null;
        return join( "value(", join( fields, "," ), ")" );
    }

    private CharSequence projectFields( List<String> fields ) {
        if (fields == null) return null;
        return join( "projectCustomField(", join( fields, "," ), (isEmpty( fields ) ? "" : ","), "field(id,name,type))" );
    }

    private CharSequence customFields( List<String> customFields, CharSequence projectFields, CharSequence valueFields ) {
        if (isEmpty( customFields ) && isEmpty( projectFields ) && isEmpty( valueFields )) return null;
        // customFields(projectCustomField(id,name,emptyFieldText,field(id,name,type)),value(id,name))
        return join( "customFields(",
                join( listOf( join( customFields, "," ), projectFields, valueFields ), "," ),
                ")" );
    }

    private CharSequence fields( List<String> fields ) {
        if (fields == null) return null;
        return join( fields, "," );
    }

    /* Issue Fields */

    @Override
    public FieldsBuilder id() {
        fields.add( IssueApi.Fields.id );
        return this;
    }

    @Override
    public FieldsBuilder idReadable() {
        fields.add( IssueApi.Fields.idReadable );
        return this;
    }

    @Override
    public FieldsBuilder name() {
        fields.add( IssueApi.Fields.name );
        return this;
    }

    @Override
    public FieldsBuilder summary() {
        fields.add( IssueApi.Fields.summary );
        return this;
    }

    /* Custom */

    @Override
    public CustomFieldsBuilder customId() {
        customFields.add( IssueApi.CustomFields.id );
        return this;
    }

    @Override
    public CustomFieldsBuilder customName() {
        customFields.add( IssueApi.CustomFields.name );
        return this;
    }

    /* Project */

    @Override
    public ProjectCustomFieldsBuilder projectCfId() {
        projectCustomFields.add( IssueApi.Project.id );
        return this;
    }

    @Override
    public ProjectCustomFieldsBuilder projectCfName() {
        projectCustomFields.add( IssueApi.Project.name );
        return this;
    }

    @Override
    public ProjectCustomFieldsBuilder emptyFieldText() {
        projectCustomFields.add( IssueApi.Project.emptyFieldText );
        return this;
    }

    /* Value */

    @Override
    public BuilderValueFields valueId() {
        valueFields.add( IssueApi.Value.id );
        return this;
    }

    @Override
    public BuilderValueFields valueName() {
        valueFields.add( IssueApi.Value.name );
        return this;
    }

    @Override
    public BuilderValueFields localizedName() {
        valueFields.add( IssueApi.Value.localizedName );
        return this;
    }

    /* Presets */

    @Override
    public PresetsBuilder preset() {
        return this;
    }

    @Override
    public QueryBuilder fieldsDefaults() {
        return fields().id().idReadable().builder();
    }

    @Override
    public QueryBuilder customFieldsDefaults() {
        return customFields().customId().customName()
                .valueFields().valueId().valueName()
                .builder();
    }


    @Override
    public QueryBuilder idAndCustomFieldsDefaults() {
        return fields().id()
                .customFields().customId().customName()
                .valueFields().valueId().valueName()
                .builder();
    }

    private String base_url;
    private String issueId;
}

interface PresetsBuilder {

    QueryBuilder fieldsDefaults();

    QueryBuilder customFieldsDefaults();

    QueryBuilder idAndCustomFieldsDefaults();
}

interface QueryBuilder {
    FieldsBuilder fields();

    CustomFieldsBuilder customFields();

    PresetsBuilder preset();

    String build();
}

interface FieldsBuilder {
    QueryBuilder builder();

    CustomFieldsBuilder customFields();

    FieldsBuilder id();

    FieldsBuilder idReadable();

    FieldsBuilder name();

    FieldsBuilder summary();
}

interface ProjectCustomFieldsBuilder {
    QueryBuilder builder();

    BuilderValueFields valueFields();

    ProjectCustomFieldsBuilder projectCfId();

    ProjectCustomFieldsBuilder projectCfName();

    ProjectCustomFieldsBuilder emptyFieldText();
}

interface CustomFieldsBuilder {
    QueryBuilder builder();

    BuilderValueFields valueFields();

    ProjectCustomFieldsBuilder projectCustomFields();

    CustomFieldsBuilder customId();

    CustomFieldsBuilder customName();
}

interface BuilderValueFields {
    QueryBuilder builder();

    ProjectCustomFieldsBuilder projectCustomFields();

    BuilderValueFields valueId();

    BuilderValueFields valueName();

    BuilderValueFields localizedName();
}
