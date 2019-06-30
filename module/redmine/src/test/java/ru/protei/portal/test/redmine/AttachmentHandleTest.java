package ru.protei.portal.test.redmine;

import com.taskadapter.redmineapi.Include;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManagerFactory;
import com.taskadapter.redmineapi.bean.Issue;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.protei.portal.config.MainConfiguration;
import ru.protei.portal.core.model.dao.AttachmentDAO;
import ru.protei.portal.core.model.dao.RedmineEndpointDAO;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.RedmineEndpoint;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.redmine.config.RedmineConfigurationContext;
import ru.protei.portal.test.DebugConfContext;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.util.Set;
import java.util.stream.Collectors;

public class AttachmentHandleTest {

    @Test
    public void testEqualsAttachments() throws RedmineException {

        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(
                CoreConfigurationContext.class, JdbcConfigurationContext.class, MainConfiguration.class,
                RedmineConfigurationContext.class,
                DebugConfContext.class
        );

        RedmineEndpointDAO redmineEndpointDAO = ctx.getBean(RedmineEndpointDAO.class);
        final RedmineEndpoint endpoint = redmineEndpointDAO.getAll().get(0);
        final RedmineManager manager =
                RedmineManagerFactory.createWithApiKey(endpoint.getServerAddress(), endpoint.getApiKey());
        final Issue issue = manager.getIssueManager().getIssueById(125049, Include.attachments);
        if (CollectionUtils.isEmpty(issue.getAttachments())) return;

        AttachmentDAO attachmentDAO = ctx.getBean(AttachmentDAO.class);
        final Set<Integer> existingAttachmentsHashCodes = attachmentDAO.getListByCaseId(157519L).stream()
                .map( Attachment::toHashCodeForRedmineCheck)
                .collect(Collectors.toSet());

        issue.getAttachments().forEach(attachment ->  {
            logger.debug("Attachment with creation date {} and file name {} exists {}", attachment.getCreatedOn(), attachment.getFileName(), existingAttachmentsHashCodes.contains(toHashCode(attachment)));
        });
    }

    private int toHashCode(com.taskadapter.redmineapi.bean.Attachment attachment){
        return ((attachment.getCreatedOn() == null ? "" : attachment.getCreatedOn().getTime()) + (attachment.getFileName() == null ? "" : attachment.getFileName())).hashCode();
    }

    private final static Logger logger = LoggerFactory.getLogger(AttachmentHandleTest.class);
}
