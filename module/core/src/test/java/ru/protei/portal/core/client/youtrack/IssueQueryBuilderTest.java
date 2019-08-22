package ru.protei.portal.core.client.youtrack;

import org.junit.Test;

import static org.junit.Assert.*;

public class IssueQueryBuilderTest {

    @Test
    public void buildUrl() {
        String url = IssueQueryBuilder.create( BASE_URL, ISSUE_ID ).build();
        assertNotNull( "expected url with base url and issue id", url );
        assertEquals( BASE_URL + "/issues/" + ISSUE_ID, url );
    }

    @Test
    public void buildFields() {
        String url = IssueQueryBuilder.create( BASE_URL, ISSUE_ID )
                .fields().id().name().summary().idReadable()
                .builder().build();

        assertEquals( URL_ISSUES + "?fields=id,name,summary,idReadable", url );
    }

    @Test
    public void buildCustomFields() {
        String url = IssueQueryBuilder.create( BASE_URL, ISSUE_ID )
                .customFields().customId().customName()
                .builder().build();

        assertEquals( URL_ISSUES + "?fields=customFields(id,name)", url );
    }

    @Test
    public void buildProjectFields() {
        String url = IssueQueryBuilder.create( BASE_URL, ISSUE_ID )
                .customFields().projectCustomFields().projectCfId().projectCfName().emptyFieldText()
                .builder().build();

        assertEquals( URL_ISSUES + "?fields=customFields(projectCustomField(id,name,emptyFieldText,field(id,name,type)))", url );
    }

    @Test
    public void buildValues() {
        String url = IssueQueryBuilder.create( BASE_URL, ISSUE_ID )
                .customFields().valueFields().valueId().valueName().localizedName()
                .builder().build();

        assertEquals( URL_ISSUES + "?fields=customFields(value(id,name,localizedName))", url );
    }

    @Test
    public void buildFullAbilities() {
        String url = IssueQueryBuilder.create( BASE_URL, ISSUE_ID )
                .fields().id().name().summary().idReadable()
                .customFields().customId().customName()
                .projectCustomFields().projectCfId().projectCfName().emptyFieldText()
                .valueFields().valueId().valueName().localizedName()
                .builder().build();

        assertEquals( URL_ISSUES +
                        "?fields=id,name,summary,idReadable," +
                        "customFields(id,name," +
                        "projectCustomField(id,name,emptyFieldText,field(id,name,type))," +
                        "value(id,name,localizedName))",
                url );
    }

    private static final String BASE_URL = "http://localhost:9999/api";
    private static final String ISSUE_ID = "PG-209";
    private static final String URL_ISSUES = BASE_URL + "/issues/" + ISSUE_ID;
}