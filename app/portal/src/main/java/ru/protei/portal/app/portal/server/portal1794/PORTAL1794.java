package ru.protei.portal.app.portal.server.portal1794;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.MainConfiguration;
import ru.protei.portal.core.client.youtrack.api.YoutrackApi;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.youtrack.dto.customfield.issue.YtSingleEnumIssueCustomField;
import ru.protei.portal.core.model.youtrack.dto.issue.YtIssue;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.util.List;
import java.util.Objects;

public class PORTAL1794 {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(
                CoreConfigurationContext.class, JdbcConfigurationContext.class, MainConfiguration.class);

        YoutrackApi api = ctx.getBean(YoutrackApi.class);
        Result<List<YtIssue>> issueWithFieldsCommentsAttachments = api.getIssueByQuery(
                " дата работы: 2021-06-26 .. 2021-06-27");

        issueWithFieldsCommentsAttachments.getData().forEach(issue -> System.out.println(makeInfo(issue)));

        ctx.destroy();
    }

    static String makeInfo(YtIssue issue) {
        return String.format("id = %s, idReadable = %s, author = %s, client = %s",
                issue.id,
                issue.idReadable,
                issue.reporter.fullName,
                CollectionUtils.stream(issue.customFields).filter(field -> field.name.equals("Заказчик"))
                        .findAny()
                        .map(field -> ((YtSingleEnumIssueCustomField)field).value)
                        .filter(Objects::nonNull)
                        .map(field -> field.name).orElse("no client"));
    }
}
