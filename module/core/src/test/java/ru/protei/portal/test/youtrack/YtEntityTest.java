package ru.protei.portal.test.youtrack;

import org.junit.Assert;
import org.junit.Test;
import ru.protei.portal.core.client.youtrack.mapper.YtDtoFieldsMapper;
import ru.protei.portal.core.client.youtrack.mapper.YtDtoFieldsMapperImpl;
import ru.protei.portal.core.model.youtrack.dto.activity.YtActivityItem;
import ru.protei.portal.core.model.youtrack.dto.bundleelemenet.YtBundleElement;
import ru.protei.portal.core.model.youtrack.dto.customfield.issue.YtIssueCustomField;
import ru.protei.portal.core.model.youtrack.dto.filterfield.YtFilterField;
import ru.protei.portal.core.model.youtrack.dto.issue.YtIssue;
import ru.protei.portal.core.model.youtrack.dto.issue.YtIssueAttachment;
import ru.protei.portal.core.model.youtrack.dto.issue.YtIssueComment;
import ru.protei.portal.core.model.youtrack.dto.project.YtProject;
import ru.protei.portal.core.model.youtrack.dto.user.YtUser;

public class YtEntityTest {

    @Test
    public void testIssueEmpty() {
        String fields = getMapper().getFields(YtIssue.class, false);
        String expected = "$type,id,idReadable";
        Assert.assertEquals(expected, fields);
    }

    @Test
    public void testIssueSimple() {
        String fields = getMapper().getFields(YtIssue.class, true);
        String expected = "$type,description,id,idReadable,summary";
        Assert.assertEquals(expected, fields);
    }

    @Test
    public void testIssueWithFieldsCommentsAttachments() {
        String fields = getMapper().getFields(YtIssue.class, true, YtIssueComment.class, YtIssueAttachment.class, YtUser.class, YtIssueCustomField.class);
        String expected = "$type,attachments($type,author($type,avatarUrl,email,fullName,id,login),charset,created,extension,id,metaData,mimeType,name,removed,size,thumbnailURL,updated,url),comments($type,attachments($type,author($type,avatarUrl,email,fullName,id,login),charset,created,extension,id,metaData,mimeType,name,removed,size,thumbnailURL,updated,url),author($type,avatarUrl,email,fullName,id,login),created,deleted,id,text,textPreview,updated,usesMarkdown),customFields($type,id,name,value($type,archived,description,id,isResolved,localizedName,markdownText,name,text)),description,id,idReadable,reporter($type,avatarUrl,email,fullName,id,login),summary,updater($type,avatarUrl,email,fullName,id,login)";
        Assert.assertEquals(expected, fields);
    }

    @Test
    public void testCustomFieldActivityItem() {
        String fields = getMapper().getFields(YtActivityItem.class, true, YtFilterField.class, YtBundleElement.class, YtUser.class);
        String expected = "$type,added($type,archived,description,id,isResolved,localizedName,name),author($type,avatarUrl,email,fullName,id,login),field($type,id,name,presentation),id,markup,removed($type,archived,description,id,isResolved,localizedName,name),timestamp";
        Assert.assertEquals(expected, fields);
    }

    @Test
    public void testProjectEmpty() {
        String fields = getMapper().getFields(YtProject.class, false);
        String expected = "$type,id,shortName";
        Assert.assertEquals(expected, fields);
    }

    private YtDtoFieldsMapper getMapper() {
        return new YtDtoFieldsMapperImpl();
    }
}
