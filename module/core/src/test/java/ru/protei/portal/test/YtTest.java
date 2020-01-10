package ru.protei.portal.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.*;
import ru.protei.portal.core.client.youtrack.api.YoutrackApiClient;
import ru.protei.portal.core.client.youtrack.http.YoutrackHttpClient;
import ru.protei.portal.core.client.youtrack.mapper.YtDtoFieldsMapper;
import ru.protei.portal.core.client.youtrack.mapper.YtDtoFieldsMapperImpl;
import ru.protei.portal.core.client.youtrack.mapper.YtDtoObjectMapperProvider;
import ru.protei.portal.core.model.ent.YouTrackIssueStateChange;
import ru.protei.portal.core.model.yt.dto.YtDto;
import ru.protei.portal.core.model.yt.dto.activity.YtActivityCategory;
import ru.protei.portal.core.model.yt.dto.activity.YtActivityItem;
import ru.protei.portal.core.model.yt.dto.customfield.issue.YtIssueCustomField;
import ru.protei.portal.core.model.yt.dto.customfield.issue.YtSimpleIssueCustomField;
import ru.protei.portal.core.model.yt.dto.customfield.issue.YtStateIssueCustomField;
import ru.protei.portal.core.model.yt.dto.issue.YtIssue;
import ru.protei.portal.core.model.yt.dto.issue.YtIssueAttachment;
import ru.protei.portal.core.model.yt.dto.project.YtProject;
import ru.protei.portal.core.model.yt.YtFieldNames;
import ru.protei.portal.core.service.YoutrackService;
import ru.protei.portal.tools.notifications.NotificationConfiguration;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        PortalConfigTestConfiguration.class,
        DaoMockTestConfiguration.class,
        ServiceTestsConfiguration.class,
        TestEventConfiguration.class,
        NotificationConfiguration.class,
        TestNotificationConfiguration.class
})
public class YtTest { // TODO remove

    private static final String URL = "https://youtrack.protei.ru/api";

    @Test
    public void testGetIssue() {
//        String url = new YoutrackUrlProvider(URL).issue("PG-230");
//        Result<YtIssue> result = client.read(url, YtIssue.class);
        Result<YtIssue> result = apiClient.getIssue("PG-230");
        YtIssue issue = result.getData();
        System.out.println(issue);
    }

    @Test
    public void testBuildFields() {
        YtDtoFieldsMapper mapper = new YtDtoFieldsMapperImpl().setup();
        String query = mapper.getFields(YtIssue.class);
        System.out.println(query);
    }

    @Test
    public void testBuildFields2() {
        YtDtoFieldsMapper mapper = new YtDtoFieldsMapperImpl().setup();
        String query = mapper.getFields(YtActivityItem.class);
        System.out.println(query);
    }

    @Test
    public void testDeserialize() {
        ObjectMapper mapper = YtDtoObjectMapperProvider.getMapper(new YtDtoFieldsMapperImpl().setup());
        try {
            YtDto object = mapper.readValue(JSON, YtDto.class);
            System.out.println(object);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSerialize() {
        ObjectMapper mapper = YtDtoObjectMapperProvider.getMapper(new YtDtoFieldsMapperImpl().setup());
        try {
            List<String> forceIncludeFields = Arrays.asList("value");

            YtIssue issue = new YtIssue();
            issue.customFields = new ArrayList<>();
            issue.customFields.add(makeCrmNumberCustomField(null));
            issue.customFields.add(makeStateIssueCustomField("Hello"));

            String json = mapper
                    .writer(YtDtoObjectMapperProvider.getFilterProvider(forceIncludeFields))
                    .writeValueAsString(issue);

            System.out.println(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testUpdateCrmNumber() {
        Result<YtIssue> result = apiClient.setCrmNumber("PG-230", 100010010026488L);
        YtIssue issue = result.getData();
        System.out.println(issue);
    }

    @Test
    public void testRemoveCrmNumber() {
        Result<YtIssue> result = apiClient.removeCrmNumber("PG-230");
        YtIssue issue = result.getData();
        System.out.println(issue);
    }

    @Test
    public void testGetAttachments() {
        Result<List<YtIssueAttachment>> result = apiClient.getIssueAttachments("PG-230");
        List<YtIssueAttachment> attachments = result.getData();
        System.out.println(attachments);
    }

    @Test
    public void testGetProjects() {
        Result<List<YtProject>> result = apiClient.getProjectsByName("PG");
        List<YtProject> projects = result.getData();
        System.out.println(projects);
    }

    @Test
    public void testCreateIssue() {
        Result<YtIssue> result = apiClient.createIssue("PG", "Test issue to check new YT api - created by api", "Issue that have been created by portal-youtrack api\n:)");
        YtIssue issue = result.getData();
        System.out.println(issue);
    }

    @Test
    public void testGetIssuesByProjectAndUpdated() throws ParseException {
        Result<List<YtIssue>> result = apiClient.getIssuesByProjectAndUpdated("PG", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2019-12-28 14:43:25"));
        List<YtIssue> issues = result.getData();
        System.out.println(issues);
    }

    @Test
    public void testGetActivityItems() {
//        String url = new YoutrackUrlProvider(URL).issueActivities("PG-230");
//
//        Map<String, String> params = new HashMap<>();
//        params.put("categories", CollectionUtils.stream(YtActivityCategory.getAllCategoryIds()).collect(Collectors.joining(",")));
////        params.put("categories", YtActivityCategory.CustomFieldCategory.getCategoryId());
//
//        Result<List<YtActivityItem>> result = client.read(url, params, YtActivityItem[].class).map(Arrays::asList);
//        List<YtActivityItem> data = result.getData();

        Result<List<YtActivityItem>> result = apiClient.getIssueActivityChanges("PG-230", YtActivityCategory.CustomFieldCategory);
        List<YtActivityItem> data = result.getData();

        System.out.println(data);
    }

    @Test
    public void testGetIssueCustomFieldsChanges() {
        Result<List<YtActivityItem>> result = youtrackService.getIssueCustomFieldsChanges("PG-230");
        List<YtActivityItem> data = result.getData();
        System.out.println(data);
    }

    @Test
    public void testGetIssueStateChanges() {
        Result<List<YouTrackIssueStateChange>> result = youtrackService.getIssueStateChanges("PG-230");
        List<YouTrackIssueStateChange> data = result.getData();
        System.out.println(data);
    }

    private YtIssueCustomField makeCrmNumberCustomField(Long caseNumber) {
        YtSimpleIssueCustomField cf = new YtSimpleIssueCustomField();
        cf.name = YtFieldNames.crmNumber;
        cf.value = caseNumber == null ? null : String.valueOf(caseNumber);
        return cf;
    }

    private YtIssueCustomField makeStateIssueCustomField(String name) {
        YtIssueCustomField customField = new YtStateIssueCustomField();
        customField.name = name;
        return customField;
    }

    @Autowired
    YoutrackHttpClient client;
    @Autowired
    YoutrackApiClient apiClient;
    @Autowired
    YoutrackService youtrackService;

    private String JSON = "{\n" +
            "  \"description\": \"Some desc\\nHere\\nAnd there\",\n" +
            "  \"summary\": \"Test issue to check new YT api\",\n" +
            "  \"idReadable\": \"PG-230\",\n" +
            "  \"customFields\": [\n" +
            "    {\n" +
            "      \"projectCustomField\": {\n" +
            "        \"bundle\": {\n" +
            "          \"name\": \"67-459-1496760913089\",\n" +
            "          \"id\": \"35-17\",\n" +
            "          \"$type\": \"EnumBundle\"\n" +
            "        },\n" +
            "        \"project\": {\n" +
            "          \"description\": \"Песочница\",\n" +
            "          \"shortName\": \"PG\",\n" +
            "          \"archived\": false,\n" +
            "          \"iconUrl\": \"https://youtrack.protei.ru:8443/hub/api/rest/projects/a5922bb5-b1bc-4b0f-8886-99ca5f83035a/icon?etag=default-PG\",\n" +
            "          \"name\": \"playground\",\n" +
            "          \"id\": \"66-66\",\n" +
            "          \"$type\": \"Project\"\n" +
            "        },\n" +
            "        \"canBeEmpty\": false,\n" +
            "        \"emptyFieldText\": \"Нет приоритета\",\n" +
            "        \"field\": {\n" +
            "          \"localizedName\": \"Приоритет\",\n" +
            "          \"name\": \"Priority\",\n" +
            "          \"id\": \"34-1\",\n" +
            "          \"$type\": \"CustomField\"\n" +
            "        },\n" +
            "        \"id\": \"67-480\",\n" +
            "        \"$type\": \"EnumProjectCustomField\"\n" +
            "      },\n" +
            "      \"value\": {\n" +
            "        \"localizedName\": \"\",\n" +
            "        \"description\": null,\n" +
            "        \"name\": \"Important\",\n" +
            "        \"id\": \"36-82\",\n" +
            "        \"$type\": \"EnumBundleElement\"\n" +
            "      },\n" +
            "      \"name\": \"Priority\",\n" +
            "      \"id\": \"67-480\",\n" +
            "      \"$type\": \"SingleEnumIssueCustomField\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"projectCustomField\": {\n" +
            "        \"bundle\": {\n" +
            "          \"name\": \"dpi-type-package\",\n" +
            "          \"id\": \"35-4\",\n" +
            "          \"$type\": \"EnumBundle\"\n" +
            "        },\n" +
            "        \"project\": {\n" +
            "          \"description\": \"Песочница\",\n" +
            "          \"shortName\": \"PG\",\n" +
            "          \"archived\": false,\n" +
            "          \"iconUrl\": \"https://youtrack.protei.ru:8443/hub/api/rest/projects/a5922bb5-b1bc-4b0f-8886-99ca5f83035a/icon?etag=default-PG\",\n" +
            "          \"name\": \"playground\",\n" +
            "          \"id\": \"66-66\",\n" +
            "          \"$type\": \"Project\"\n" +
            "        },\n" +
            "        \"canBeEmpty\": false,\n" +
            "        \"emptyFieldText\": \"Нет типа\",\n" +
            "        \"field\": {\n" +
            "          \"localizedName\": \"Тип\",\n" +
            "          \"name\": \"Type\",\n" +
            "          \"id\": \"34-0\",\n" +
            "          \"$type\": \"CustomField\"\n" +
            "        },\n" +
            "        \"id\": \"67-479\",\n" +
            "        \"$type\": \"EnumProjectCustomField\"\n" +
            "      },\n" +
            "      \"value\": {\n" +
            "        \"localizedName\": \"\",\n" +
            "        \"description\": null,\n" +
            "        \"name\": \"Bug\",\n" +
            "        \"id\": \"36-28\",\n" +
            "        \"$type\": \"EnumBundleElement\"\n" +
            "      },\n" +
            "      \"name\": \"Type\",\n" +
            "      \"id\": \"67-479\",\n" +
            "      \"$type\": \"SingleEnumIssueCustomField\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"projectCustomField\": {\n" +
            "        \"bundle\": {\n" +
            "          \"name\": \"67-481-1526584526292\",\n" +
            "          \"id\": \"38-35\",\n" +
            "          \"$type\": \"StateBundle\"\n" +
            "        },\n" +
            "        \"project\": {\n" +
            "          \"description\": \"Песочница\",\n" +
            "          \"shortName\": \"PG\",\n" +
            "          \"archived\": false,\n" +
            "          \"iconUrl\": \"https://youtrack.protei.ru:8443/hub/api/rest/projects/a5922bb5-b1bc-4b0f-8886-99ca5f83035a/icon?etag=default-PG\",\n" +
            "          \"name\": \"playground\",\n" +
            "          \"id\": \"66-66\",\n" +
            "          \"$type\": \"Project\"\n" +
            "        },\n" +
            "        \"canBeEmpty\": false,\n" +
            "        \"emptyFieldText\": \"Нет состояния\",\n" +
            "        \"field\": {\n" +
            "          \"localizedName\": \"Состояние\",\n" +
            "          \"name\": \"State\",\n" +
            "          \"id\": \"34-2\",\n" +
            "          \"$type\": \"CustomField\"\n" +
            "        },\n" +
            "        \"id\": \"67-481\",\n" +
            "        \"$type\": \"StateProjectCustomField\"\n" +
            "      },\n" +
            "      \"value\": {\n" +
            "        \"isResolved\": false,\n" +
            "        \"localizedName\": \"\",\n" +
            "        \"description\": null,\n" +
            "        \"name\": \"Active\",\n" +
            "        \"id\": \"39-299\",\n" +
            "        \"$type\": \"StateBundleElement\"\n" +
            "      },\n" +
            "      \"name\": \"State\",\n" +
            "      \"id\": \"67-481\",\n" +
            "      \"$type\": \"StateMachineIssueCustomField\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"projectCustomField\": {\n" +
            "        \"bundle\": {\n" +
            "          \"name\": \"6b1127be-1c3b-4f4c-b0db-519dcf03cdf5\",\n" +
            "          \"id\": \"35-21\",\n" +
            "          \"$type\": \"EnumBundle\"\n" +
            "        },\n" +
            "        \"project\": {\n" +
            "          \"description\": \"Песочница\",\n" +
            "          \"shortName\": \"PG\",\n" +
            "          \"archived\": false,\n" +
            "          \"iconUrl\": \"https://youtrack.protei.ru:8443/hub/api/rest/projects/a5922bb5-b1bc-4b0f-8886-99ca5f83035a/icon?etag=default-PG\",\n" +
            "          \"name\": \"playground\",\n" +
            "          \"id\": \"66-66\",\n" +
            "          \"$type\": \"Project\"\n" +
            "        },\n" +
            "        \"canBeEmpty\": false,\n" +
            "        \"emptyFieldText\": \"Нет заказчика\",\n" +
            "        \"field\": {\n" +
            "          \"localizedName\": null,\n" +
            "          \"name\": \"Заказчик\",\n" +
            "          \"id\": \"34-53\",\n" +
            "          \"$type\": \"CustomField\"\n" +
            "        },\n" +
            "        \"id\": \"67-706\",\n" +
            "        \"$type\": \"EnumProjectCustomField\"\n" +
            "      },\n" +
            "      \"value\": {\n" +
            "        \"localizedName\": null,\n" +
            "        \"description\": null,\n" +
            "        \"name\": \"НТЦ Протей\",\n" +
            "        \"id\": \"36-97\",\n" +
            "        \"$type\": \"EnumBundleElement\"\n" +
            "      },\n" +
            "      \"name\": \"Заказчик\",\n" +
            "      \"id\": \"67-706\",\n" +
            "      \"$type\": \"SingleEnumIssueCustomField\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"projectCustomField\": {\n" +
            "        \"bundle\": {\n" +
            "          \"name\": \"Подсистемы\",\n" +
            "          \"id\": \"40-0\",\n" +
            "          \"$type\": \"OwnedBundle\"\n" +
            "        },\n" +
            "        \"project\": {\n" +
            "          \"description\": \"Песочница\",\n" +
            "          \"shortName\": \"PG\",\n" +
            "          \"archived\": false,\n" +
            "          \"iconUrl\": \"https://youtrack.protei.ru:8443/hub/api/rest/projects/a5922bb5-b1bc-4b0f-8886-99ca5f83035a/icon?etag=default-PG\",\n" +
            "          \"name\": \"playground\",\n" +
            "          \"id\": \"66-66\",\n" +
            "          \"$type\": \"Project\"\n" +
            "        },\n" +
            "        \"canBeEmpty\": false,\n" +
            "        \"emptyFieldText\": \"Нет подсистемы\",\n" +
            "        \"field\": {\n" +
            "          \"localizedName\": \"Подсистема\",\n" +
            "          \"name\": \"Subsystem\",\n" +
            "          \"id\": \"34-3\",\n" +
            "          \"$type\": \"CustomField\"\n" +
            "        },\n" +
            "        \"id\": \"67-482\",\n" +
            "        \"$type\": \"OwnedProjectCustomField\"\n" +
            "      },\n" +
            "      \"value\": {\n" +
            "        \"description\": null,\n" +
            "        \"name\": \"gui\",\n" +
            "        \"id\": \"41-137\",\n" +
            "        \"$type\": \"OwnedBundleElement\"\n" +
            "      },\n" +
            "      \"name\": \"Subsystem\",\n" +
            "      \"id\": \"67-482\",\n" +
            "      \"$type\": \"SingleOwnedIssueCustomField\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"projectCustomField\": {\n" +
            "        \"bundle\": {\n" +
            "          \"name\": \"playground: Исполнитель\",\n" +
            "          \"id\": \"69-70\",\n" +
            "          \"$type\": \"UserBundle\"\n" +
            "        },\n" +
            "        \"project\": {\n" +
            "          \"description\": \"Песочница\",\n" +
            "          \"shortName\": \"PG\",\n" +
            "          \"archived\": false,\n" +
            "          \"iconUrl\": \"https://youtrack.protei.ru:8443/hub/api/rest/projects/a5922bb5-b1bc-4b0f-8886-99ca5f83035a/icon?etag=default-PG\",\n" +
            "          \"name\": \"playground\",\n" +
            "          \"id\": \"66-66\",\n" +
            "          \"$type\": \"Project\"\n" +
            "        },\n" +
            "        \"canBeEmpty\": true,\n" +
            "        \"emptyFieldText\": \"Нет исполнителя\",\n" +
            "        \"field\": {\n" +
            "          \"localizedName\": \"Исполнитель\",\n" +
            "          \"name\": \"Assignee\",\n" +
            "          \"id\": \"34-5\",\n" +
            "          \"$type\": \"CustomField\"\n" +
            "        },\n" +
            "        \"id\": \"68-70\",\n" +
            "        \"$type\": \"UserProjectCustomField\"\n" +
            "      },\n" +
            "      \"value\": {\n" +
            "        \"name\": \"Бухмастов Алексей\",\n" +
            "        \"id\": \"25-461\",\n" +
            "        \"$type\": \"User\"\n" +
            "      },\n" +
            "      \"name\": \"Assignee\",\n" +
            "      \"id\": \"68-70\",\n" +
            "      \"$type\": \"SingleUserIssueCustomField\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"projectCustomField\": {\n" +
            "        \"bundle\": {\n" +
            "          \"name\": \"playground: Рецензент\",\n" +
            "          \"id\": \"69-134\",\n" +
            "          \"$type\": \"UserBundle\"\n" +
            "        },\n" +
            "        \"project\": {\n" +
            "          \"description\": \"Песочница\",\n" +
            "          \"shortName\": \"PG\",\n" +
            "          \"archived\": false,\n" +
            "          \"iconUrl\": \"https://youtrack.protei.ru:8443/hub/api/rest/projects/a5922bb5-b1bc-4b0f-8886-99ca5f83035a/icon?etag=default-PG\",\n" +
            "          \"name\": \"playground\",\n" +
            "          \"id\": \"66-66\",\n" +
            "          \"$type\": \"Project\"\n" +
            "        },\n" +
            "        \"canBeEmpty\": true,\n" +
            "        \"emptyFieldText\": \"Нет рецензента\",\n" +
            "        \"field\": {\n" +
            "          \"localizedName\": null,\n" +
            "          \"name\": \"Рецензент\",\n" +
            "          \"id\": \"34-52\",\n" +
            "          \"$type\": \"CustomField\"\n" +
            "        },\n" +
            "        \"id\": \"68-133\",\n" +
            "        \"$type\": \"UserProjectCustomField\"\n" +
            "      },\n" +
            "      \"value\": {\n" +
            "        \"name\": \"Пономарева Надежда\",\n" +
            "        \"id\": \"25-203\",\n" +
            "        \"$type\": \"User\"\n" +
            "      },\n" +
            "      \"name\": \"Рецензент\",\n" +
            "      \"id\": \"68-133\",\n" +
            "      \"$type\": \"SingleUserIssueCustomField\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"projectCustomField\": {\n" +
            "        \"project\": {\n" +
            "          \"description\": \"Песочница\",\n" +
            "          \"shortName\": \"PG\",\n" +
            "          \"archived\": false,\n" +
            "          \"iconUrl\": \"https://youtrack.protei.ru:8443/hub/api/rest/projects/a5922bb5-b1bc-4b0f-8886-99ca5f83035a/icon?etag=default-PG\",\n" +
            "          \"name\": \"playground\",\n" +
            "          \"id\": \"66-66\",\n" +
            "          \"$type\": \"Project\"\n" +
            "        },\n" +
            "        \"canBeEmpty\": true,\n" +
            "        \"emptyFieldText\": \"Нет: номера обращений в crm\",\n" +
            "        \"field\": {\n" +
            "          \"localizedName\": null,\n" +
            "          \"name\": \"Номер обращения в CRM\",\n" +
            "          \"id\": \"34-82\",\n" +
            "          \"$type\": \"CustomField\"\n" +
            "        },\n" +
            "        \"id\": \"139-394\",\n" +
            "        \"$type\": \"SimpleProjectCustomField\"\n" +
            "      },\n" +
            "      \"value\": \"100010010026488\",\n" +
            "      \"name\": \"Номер обращения в CRM\",\n" +
            "      \"id\": \"139-394\",\n" +
            "      \"$type\": \"SimpleIssueCustomField\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"projectCustomField\": {\n" +
            "        \"project\": {\n" +
            "          \"description\": \"Песочница\",\n" +
            "          \"shortName\": \"PG\",\n" +
            "          \"archived\": false,\n" +
            "          \"iconUrl\": \"https://youtrack.protei.ru:8443/hub/api/rest/projects/a5922bb5-b1bc-4b0f-8886-99ca5f83035a/icon?etag=default-PG\",\n" +
            "          \"name\": \"playground\",\n" +
            "          \"id\": \"66-66\",\n" +
            "          \"$type\": \"Project\"\n" +
            "        },\n" +
            "        \"canBeEmpty\": true,\n" +
            "        \"emptyFieldText\": \"?\",\n" +
            "        \"field\": {\n" +
            "          \"localizedName\": null,\n" +
            "          \"name\": \"Оценка\",\n" +
            "          \"id\": \"34-9\",\n" +
            "          \"$type\": \"CustomField\"\n" +
            "        },\n" +
            "        \"id\": \"100-136\",\n" +
            "        \"$type\": \"PeriodProjectCustomField\"\n" +
            "      },\n" +
            "      \"value\": {\n" +
            "        \"id\": \"P1DT3H15M\",\n" +
            "        \"$type\": \"PeriodValue\"\n" +
            "      },\n" +
            "      \"name\": \"Оценка\",\n" +
            "      \"id\": \"100-136\",\n" +
            "      \"$type\": \"PeriodIssueCustomField\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"projectCustomField\": {\n" +
            "        \"project\": {\n" +
            "          \"description\": \"Песочница\",\n" +
            "          \"shortName\": \"PG\",\n" +
            "          \"archived\": false,\n" +
            "          \"iconUrl\": \"https://youtrack.protei.ru:8443/hub/api/rest/projects/a5922bb5-b1bc-4b0f-8886-99ca5f83035a/icon?etag=default-PG\",\n" +
            "          \"name\": \"playground\",\n" +
            "          \"id\": \"66-66\",\n" +
            "          \"$type\": \"Project\"\n" +
            "        },\n" +
            "        \"canBeEmpty\": true,\n" +
            "        \"emptyFieldText\": \"?\",\n" +
            "        \"field\": {\n" +
            "          \"localizedName\": null,\n" +
            "          \"name\": \"Затраченное время\",\n" +
            "          \"id\": \"34-10\",\n" +
            "          \"$type\": \"CustomField\"\n" +
            "        },\n" +
            "        \"id\": \"100-135\",\n" +
            "        \"$type\": \"PeriodProjectCustomField\"\n" +
            "      },\n" +
            "      \"value\": {\n" +
            "        \"id\": \"PT42M\",\n" +
            "        \"$type\": \"PeriodValue\"\n" +
            "      },\n" +
            "      \"name\": \"Затраченное время\",\n" +
            "      \"id\": \"100-135\",\n" +
            "      \"$type\": \"PeriodIssueCustomField\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"projectCustomField\": {\n" +
            "        \"project\": {\n" +
            "          \"description\": \"Песочница\",\n" +
            "          \"shortName\": \"PG\",\n" +
            "          \"archived\": false,\n" +
            "          \"iconUrl\": \"https://youtrack.protei.ru:8443/hub/api/rest/projects/a5922bb5-b1bc-4b0f-8886-99ca5f83035a/icon?etag=default-PG\",\n" +
            "          \"name\": \"playground\",\n" +
            "          \"id\": \"66-66\",\n" +
            "          \"$type\": \"Project\"\n" +
            "        },\n" +
            "        \"canBeEmpty\": true,\n" +
            "        \"emptyFieldText\": \"Нет\",\n" +
            "        \"field\": {\n" +
            "          \"localizedName\": null,\n" +
            "          \"name\": \"Дедлайн\",\n" +
            "          \"id\": \"34-44\",\n" +
            "          \"$type\": \"CustomField\"\n" +
            "        },\n" +
            "        \"id\": \"139-88\",\n" +
            "        \"$type\": \"SimpleProjectCustomField\"\n" +
            "      },\n" +
            "      \"value\": 1577793600000,\n" +
            "      \"name\": \"Дедлайн\",\n" +
            "      \"id\": \"139-88\",\n" +
            "      \"$type\": \"DateIssueCustomField\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"projectCustomField\": {\n" +
            "        \"bundle\": {\n" +
            "          \"name\": \"Сборки\",\n" +
            "          \"id\": \"42-0\",\n" +
            "          \"$type\": \"BuildBundle\"\n" +
            "        },\n" +
            "        \"project\": {\n" +
            "          \"description\": \"Песочница\",\n" +
            "          \"shortName\": \"PG\",\n" +
            "          \"archived\": false,\n" +
            "          \"iconUrl\": \"https://youtrack.protei.ru:8443/hub/api/rest/projects/a5922bb5-b1bc-4b0f-8886-99ca5f83035a/icon?etag=default-PG\",\n" +
            "          \"name\": \"playground\",\n" +
            "          \"id\": \"66-66\",\n" +
            "          \"$type\": \"Project\"\n" +
            "        },\n" +
            "        \"canBeEmpty\": true,\n" +
            "        \"emptyFieldText\": \"Следующая сборка\",\n" +
            "        \"field\": {\n" +
            "          \"localizedName\": \"Исправлено в сборке\",\n" +
            "          \"name\": \"Fixed in build\",\n" +
            "          \"id\": \"34-4\",\n" +
            "          \"$type\": \"CustomField\"\n" +
            "        },\n" +
            "        \"id\": \"67-1268\",\n" +
            "        \"$type\": \"BuildProjectCustomField\"\n" +
            "      },\n" +
            "      \"value\": null,\n" +
            "      \"name\": \"Fixed in build\",\n" +
            "      \"id\": \"67-1268\",\n" +
            "      \"$type\": \"SingleBuildIssueCustomField\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"projectCustomField\": {\n" +
            "        \"bundle\": {\n" +
            "          \"name\": \"playground: Версия релиза\",\n" +
            "          \"id\": \"44-126\",\n" +
            "          \"$type\": \"VersionBundle\"\n" +
            "        },\n" +
            "        \"project\": {\n" +
            "          \"description\": \"Песочница\",\n" +
            "          \"shortName\": \"PG\",\n" +
            "          \"archived\": false,\n" +
            "          \"iconUrl\": \"https://youtrack.protei.ru:8443/hub/api/rest/projects/a5922bb5-b1bc-4b0f-8886-99ca5f83035a/icon?etag=default-PG\",\n" +
            "          \"name\": \"playground\",\n" +
            "          \"id\": \"66-66\",\n" +
            "          \"$type\": \"Project\"\n" +
            "        },\n" +
            "        \"canBeEmpty\": true,\n" +
            "        \"emptyFieldText\": \"Нет: версия релиза\",\n" +
            "        \"field\": {\n" +
            "          \"localizedName\": null,\n" +
            "          \"name\": \"Версия релиза\",\n" +
            "          \"id\": \"34-63\",\n" +
            "          \"$type\": \"CustomField\"\n" +
            "        },\n" +
            "        \"id\": \"67-1540\",\n" +
            "        \"$type\": \"VersionProjectCustomField\"\n" +
            "      },\n" +
            "      \"value\": null,\n" +
            "      \"name\": \"Версия релиза\",\n" +
            "      \"id\": \"67-1540\",\n" +
            "      \"$type\": \"SingleVersionIssueCustomField\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"projectCustomField\": {\n" +
            "        \"bundle\": {\n" +
            "          \"name\": \"playground: version\",\n" +
            "          \"id\": \"44-127\",\n" +
            "          \"$type\": \"VersionBundle\"\n" +
            "        },\n" +
            "        \"project\": {\n" +
            "          \"description\": \"Песочница\",\n" +
            "          \"shortName\": \"PG\",\n" +
            "          \"archived\": false,\n" +
            "          \"iconUrl\": \"https://youtrack.protei.ru:8443/hub/api/rest/projects/a5922bb5-b1bc-4b0f-8886-99ca5f83035a/icon?etag=default-PG\",\n" +
            "          \"name\": \"playground\",\n" +
            "          \"id\": \"66-66\",\n" +
            "          \"$type\": \"Project\"\n" +
            "        },\n" +
            "        \"canBeEmpty\": true,\n" +
            "        \"emptyFieldText\": \"Нет: version\",\n" +
            "        \"field\": {\n" +
            "          \"localizedName\": null,\n" +
            "          \"name\": \"version\",\n" +
            "          \"id\": \"34-76\",\n" +
            "          \"$type\": \"CustomField\"\n" +
            "        },\n" +
            "        \"id\": \"67-1541\",\n" +
            "        \"$type\": \"VersionProjectCustomField\"\n" +
            "      },\n" +
            "      \"value\": null,\n" +
            "      \"name\": \"version\",\n" +
            "      \"id\": \"67-1541\",\n" +
            "      \"$type\": \"SingleVersionIssueCustomField\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"projectCustomField\": {\n" +
            "        \"bundle\": {\n" +
            "          \"name\": \"playground: Статус заявки\",\n" +
            "          \"id\": \"38-55\",\n" +
            "          \"$type\": \"StateBundle\"\n" +
            "        },\n" +
            "        \"project\": {\n" +
            "          \"description\": \"Песочница\",\n" +
            "          \"shortName\": \"PG\",\n" +
            "          \"archived\": false,\n" +
            "          \"iconUrl\": \"https://youtrack.protei.ru:8443/hub/api/rest/projects/a5922bb5-b1bc-4b0f-8886-99ca5f83035a/icon?etag=default-PG\",\n" +
            "          \"name\": \"playground\",\n" +
            "          \"id\": \"66-66\",\n" +
            "          \"$type\": \"Project\"\n" +
            "        },\n" +
            "        \"canBeEmpty\": true,\n" +
            "        \"emptyFieldText\": \"Нет: статус заявки\",\n" +
            "        \"field\": {\n" +
            "          \"localizedName\": null,\n" +
            "          \"name\": \"Статус заявки\",\n" +
            "          \"id\": \"34-157\",\n" +
            "          \"$type\": \"CustomField\"\n" +
            "        },\n" +
            "        \"id\": \"67-1899\",\n" +
            "        \"$type\": \"StateProjectCustomField\"\n" +
            "      },\n" +
            "      \"value\": {\n" +
            "        \"isResolved\": false,\n" +
            "        \"localizedName\": null,\n" +
            "        \"description\": null,\n" +
            "        \"name\": \"New\",\n" +
            "        \"id\": \"39-448\",\n" +
            "        \"$type\": \"StateBundleElement\"\n" +
            "      },\n" +
            "      \"name\": \"Статус заявки\",\n" +
            "      \"id\": \"67-1899\",\n" +
            "      \"$type\": \"StateIssueCustomField\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"projectCustomField\": {\n" +
            "        \"bundle\": {\n" +
            "          \"name\": \"playground: Статус заказа\",\n" +
            "          \"id\": \"38-56\",\n" +
            "          \"$type\": \"StateBundle\"\n" +
            "        },\n" +
            "        \"project\": {\n" +
            "          \"description\": \"Песочница\",\n" +
            "          \"shortName\": \"PG\",\n" +
            "          \"archived\": false,\n" +
            "          \"iconUrl\": \"https://youtrack.protei.ru:8443/hub/api/rest/projects/a5922bb5-b1bc-4b0f-8886-99ca5f83035a/icon?etag=default-PG\",\n" +
            "          \"name\": \"playground\",\n" +
            "          \"id\": \"66-66\",\n" +
            "          \"$type\": \"Project\"\n" +
            "        },\n" +
            "        \"canBeEmpty\": true,\n" +
            "        \"emptyFieldText\": \"Нет: статус заказа\",\n" +
            "        \"field\": {\n" +
            "          \"localizedName\": null,\n" +
            "          \"name\": \"Статус заказа\",\n" +
            "          \"id\": \"34-85\",\n" +
            "          \"$type\": \"CustomField\"\n" +
            "        },\n" +
            "        \"id\": \"67-1900\",\n" +
            "        \"$type\": \"StateProjectCustomField\"\n" +
            "      },\n" +
            "      \"value\": {\n" +
            "        \"isResolved\": false,\n" +
            "        \"localizedName\": null,\n" +
            "        \"description\": null,\n" +
            "        \"name\": \"Заказан\",\n" +
            "        \"id\": \"39-453\",\n" +
            "        \"$type\": \"StateBundleElement\"\n" +
            "      },\n" +
            "      \"name\": \"Статус заказа\",\n" +
            "      \"id\": \"67-1900\",\n" +
            "      \"$type\": \"StateIssueCustomField\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"projectCustomField\": {\n" +
            "        \"bundle\": {\n" +
            "          \"name\": \"playground: Reviewer\",\n" +
            "          \"id\": \"69-449\",\n" +
            "          \"$type\": \"UserBundle\"\n" +
            "        },\n" +
            "        \"project\": {\n" +
            "          \"description\": \"Песочница\",\n" +
            "          \"shortName\": \"PG\",\n" +
            "          \"archived\": false,\n" +
            "          \"iconUrl\": \"https://youtrack.protei.ru:8443/hub/api/rest/projects/a5922bb5-b1bc-4b0f-8886-99ca5f83035a/icon?etag=default-PG\",\n" +
            "          \"name\": \"playground\",\n" +
            "          \"id\": \"66-66\",\n" +
            "          \"$type\": \"Project\"\n" +
            "        },\n" +
            "        \"canBeEmpty\": true,\n" +
            "        \"emptyFieldText\": \"Нет: reviewer\",\n" +
            "        \"field\": {\n" +
            "          \"localizedName\": null,\n" +
            "          \"name\": \"Reviewer\",\n" +
            "          \"id\": \"34-18\",\n" +
            "          \"$type\": \"CustomField\"\n" +
            "        },\n" +
            "        \"id\": \"68-675\",\n" +
            "        \"$type\": \"UserProjectCustomField\"\n" +
            "      },\n" +
            "      \"value\": [\n" +
            "        {\n" +
            "          \"name\": \"Елисеев Алексей\",\n" +
            "          \"id\": \"25-361\",\n" +
            "          \"$type\": \"User\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"name\": \"Reviewer\",\n" +
            "      \"id\": \"68-675\",\n" +
            "      \"$type\": \"MultiUserIssueCustomField\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"projectCustomField\": {\n" +
            "        \"project\": {\n" +
            "          \"description\": \"Песочница\",\n" +
            "          \"shortName\": \"PG\",\n" +
            "          \"archived\": false,\n" +
            "          \"iconUrl\": \"https://youtrack.protei.ru:8443/hub/api/rest/projects/a5922bb5-b1bc-4b0f-8886-99ca5f83035a/icon?etag=default-PG\",\n" +
            "          \"name\": \"playground\",\n" +
            "          \"id\": \"66-66\",\n" +
            "          \"$type\": \"Project\"\n" +
            "        },\n" +
            "        \"canBeEmpty\": true,\n" +
            "        \"emptyFieldText\": \"Нет: реализовано\",\n" +
            "        \"field\": {\n" +
            "          \"localizedName\": null,\n" +
            "          \"name\": \"Реализовано\",\n" +
            "          \"id\": \"34-220\",\n" +
            "          \"$type\": \"CustomField\"\n" +
            "        },\n" +
            "        \"id\": \"224-15\",\n" +
            "        \"$type\": \"TextProjectCustomField\"\n" +
            "      },\n" +
            "      \"value\": null,\n" +
            "      \"name\": \"Реализовано\",\n" +
            "      \"id\": \"224-15\",\n" +
            "      \"$type\": \"TextIssueCustomField\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"id\": \"78-125011\",\n" +
            "  \"$type\": \"Issue\"\n" +
            "}";
}

