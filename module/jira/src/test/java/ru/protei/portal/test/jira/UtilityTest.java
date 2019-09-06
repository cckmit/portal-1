package ru.protei.portal.test.jira;

import org.junit.Assert;
import org.junit.Test;
import ru.protei.portal.core.model.struct.JiraExtAppData;

public class UtilityTest {

//    @Test
//    public void testSeverityParse () {
//        Assert.assertEquals("10", CommonUtils.dirtyHackForSeverity("10-Critical"));
//        Assert.assertEquals("01", CommonUtils.dirtyHackForSeverity("01 - Emergency, caused by reason beyond control of Peter-Service"));
//        Assert.assertEquals("50", CommonUtils.dirtyHackForSeverity("50 - Basic"));
//    }

    @Test
    public void testIssueStateParse () {
        JiraExtAppData state = JiraExtAppData.fromJSON("{\"cid\":[1,2,3]}");

        Assert.assertEquals(3, state.commentsCount());
        Assert.assertEquals(0, state.attachmentsCount());

        state = JiraExtAppData.fromJSON("{\"cid\":[55], \"aid\" : [\"4\",\"5\"]}");

        Assert.assertEquals(1, state.commentsCount());
        Assert.assertEquals(2, state.attachmentsCount());
    }


    @Test
    public void testIssuePack () {
        JiraExtAppData mergeState = new JiraExtAppData();
        mergeState.appendComment(1)
                .appendComment(2)
                .appendComment(3)
                .appendAttachment("4")
                .appendAttachment("4")
                .appendAttachment("7");

//        System.out.println(mergeState.toString());

        Assert.assertEquals("{\"issueType\":null,\"severity\":null,\"cid\":[1,2,3],\"aid\":[\"4\",\"7\"]}", mergeState.toString());

    }

    @Test
    public void testIssueTypeAndSeverity () {

        JiraExtAppData state = JiraExtAppData.fromJSON("{\"issueType\":\"Error\",\"severity\":\"10\"}");
        Assert.assertEquals("Error", state.issueType());
        Assert.assertEquals("10", state.severity());

        String json = state.toString();
        Assert.assertEquals("{\"issueType\":\"Error\",\"severity\":\"10\",\"cid\":[],\"aid\":[]}", json);
    }
}
