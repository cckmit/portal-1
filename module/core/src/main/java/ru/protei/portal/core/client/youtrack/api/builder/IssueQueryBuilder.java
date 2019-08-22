package ru.protei.portal.core.client.youtrack.api.builder;

import org.springframework.web.util.UriComponentsBuilder;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.yt.api.IssueApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ru.protei.portal.core.model.helper.CollectionUtils.listOf;
import static ru.protei.portal.core.model.helper.StringUtils.isEmpty;
import static ru.protei.portal.core.model.helper.StringUtils.join;

public class IssueQueryBuilder implements QueryBuilder, FieldsBuilder, CustomFieldsBuilder, BuilderValueFields, ProjectCustomFieldsBuilder, PresetsBuilder {

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
        if (CollectionUtils.isEmpty( fields )) return null;
        return join( "value(", join( fields, "," ), ")" );
    }

    private CharSequence projectFields( List<String> fields ) {
        if (fields == null) return null;
        return join( "projectCustomField(", join( fields, "," ), (CollectionUtils.isEmpty( fields ) ? "" : ","), "field(id,name,type))" );
    }

    private CharSequence customFields( List<String> customFields, CharSequence projectFields, CharSequence valueFields ) {
        if (CollectionUtils.isEmpty( customFields ) && isEmpty( projectFields ) && isEmpty( valueFields )) return null;
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
